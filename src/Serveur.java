import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;

public class Serveur implements Serializable {
    private final long serialVersionUID = 1L; 
    private static boolean firstClick = false;
    private int nbJoeur = 0;
    private Thread listenConexions, listenClient;
    private boolean listening, receiving;
    private Champ champ;
    private App app;
    private Gui gui;
    private Case ca;
    private int[] scorePlayers = new int[20];
    private Thread[] listConexions = new Thread[10];  // Initialize with an expected size
    private List<String> playerNames = new ArrayList<>();
    private List<Socket> clientSockets = new ArrayList<>();  // Thread-safe list
    private List<ObjectOutputStream> outputs = new ArrayList<>();  // Thread-safe list

    private ServerSocket gestSock;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    Serveur(App ap,Champ champ, Case ca) {
        System.out.println("Démarrage du serveur"); 
        this.app = ap;
        this.listening = false;
        this.receiving = false;
        this.champ = champ;

        this.ca = ca;
        startServerInBackground(); 
    }

    public int get_nbJoueur(){
        return nbJoeur;
    }

    public void count_nbJoueur(){
        nbJoeur++;
    }

    public void uncount_nbJoueur(){
        nbJoeur--;
    }

    public void reset_nbJoueur(){
        nbJoeur++;
    }

    public boolean get_firstClick(){
        return firstClick;
    }

    public void set_firstClick(){
        firstClick = true;
    }

    public void countScorePlayer(int player){
        scorePlayers[player]++;
    }

    public void unCountScorePlayer(int player){
        scorePlayers[player]--;
    }

    public int getScorePlayer(int player){
        return  scorePlayers[player];
    }

    public int[] getAllScorePlayers(){
        return scorePlayers;
    }

    public void startServerInBackground() {
        if (!listening) {
            listening = true;
            listenConexions = new Thread(() -> initServer());
            listenConexions.start();
        }
    }

    public void initServer() {
        try {
            gestSock = new ServerSocket(10000);
            while (listening) { 
                System.out.println("SERVEUR = En attente de connexion...");
                Socket socket = gestSock.accept();
                System.out.println("SERVEUR = connecté");
                
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                // Receive player name from interface
                System.out.println("SERVEUR = Waiting for player name... ");
                String nomJoueur = (String) in.readObject();
                System.out.println("SERVEUR = Player connected: " + nomJoueur);
                playerNames.add(nomJoueur);
                
                // Create and start a new thread for each client connection
                clientSockets.add(socket);
                outputs.add(out);
                
                broadcastPlayerNames();

                listConexions[get_nbJoueur()] = new Thread(() -> handleClient(get_nbJoueur(), socket, out, in));
                listConexions[get_nbJoueur()].start();

                count_nbJoueur(); // Increment only after starting the thread
                // Broadcast updated player list to all connected clients
                //broadcastPlayerNames(outputs);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("SERVEUR = Exception occurred: " + e.getMessage());
            stopListening();
        }
    }

    public void handleClient(int nbJouer, Socket socket, ObjectOutputStream out, ObjectInputStream in) {

        int x, y;
        // send champ
        System.out.println("SERVEUR: First click?"+ get_firstClick());
        if(get_firstClick()){
            broadcastTabMines(champ);
            broadcastTabRevealed(champ);  
        }
            
        try {
            // Send the player number
            out.reset();
            out.writeObject(MessageType.PLAYER_NUMBER);
            out.writeObject(get_nbJoueur());
            out.flush();
            System.out.println("SERVEUR - apres d'envoyer numero "+get_nbJoueur());
            receiving = true;

            while (receiving) {
                System.out.println("SERVEUR - Inside loop receiving");

                // Receive player click
                List<Integer>xy =  (List<Integer>)in.readObject();
                x = xy.get(0);
                y = xy.get(1);
                champ.setRevealed(x, y);
                countScorePlayer(get_nbJoueur());
                System.out.println("ScorePlayer:"+get_nbJoueur()+" : "+getScorePlayer(get_nbJoueur()));
                System.out.println("SERVEUR x="+x + " y="+y);

                if(!firstClick){
                    // initialize server
                    champ.init(x, y, 0);
                    System.out.println("SERVEUR - visualize champ:");
                    champ.display();
                    set_firstClick();
                    broadcastTabMines(champ);
                    broadcastTabRevealed(champ);  
                    broadcastXY(x, y);                    
                }else{
                    // send champ
                    broadcastXY(x, y);
                }

                if(champ.isMine(x, y)){
                    unCountScorePlayer(get_nbJoueur());
                    System.out.println("Stepped in a mine!");
                    System.out.println("Score:");
                    int j=0;
                    for (int i = 1; i <= playerNames.size(); i++) {
                        String name = playerNames.get(j++);
                        System.out.println(name + " : " + getScorePlayer(i));
                    }
                    broadcastGameOver();
                    receiving = false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client Was disconnected! ");
            uncount_nbJoueur();
            playerNames.remove(this.get_nbJoueur());
            clientSockets.remove(this.get_nbJoueur());
            outputs.remove(this.get_nbJoueur());

            broadcastPlayerNames();
            if(get_nbJoueur() == 0){
                playerNames.clear();           
                clientSockets.clear();         
                outputs.clear();               
                //stopListening();
                stopReceiving();
                firstClick = false;
            }

        }
    }

    public void broadcastPlayerNames() {

        synchronized (outputs) {
            for (ObjectOutputStream out : outputs) {
                try {
                    out.reset();  // Prevent redundant headers
                    out.writeObject(MessageType.PLAYER_LIST);
                    out.writeObject(playerNames);  // Send updated player names
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Error sending player list to client: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastXY(int row, int col) {
        System.out.println("SERVEUR - Sending X et Y");
        List<Integer>xy = new ArrayList<Integer>();
        xy.add(row);
        xy.add(col);

        synchronized (outputs) {
            for (ObjectOutputStream out : outputs) {
                try {
                    out.reset();
                    out.writeObject(MessageType.PLAYER_XY);
                    out.writeObject(xy);
                    out.flush();
                    out.flush();   
                } catch (IOException e) {
                    System.out.println("Error sending player  X and Y to client: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastGameOver() {

        synchronized (outputs) {
            for (ObjectOutputStream out : outputs) {
                try {
                    out.reset();
                    out.writeObject(MessageType.GAME_OVER);
                    out.writeObject(getAllScorePlayers());
                    out.flush();
                    out.flush();   
                } catch (IOException e) {
                    System.out.println("Error sending player list to client: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastTabRevealed(Champ champ) {

        synchronized (outputs) {
            for (ObjectOutputStream out : outputs) {
                try {
                    out.reset();
                    out.writeObject(MessageType.PLAYER_REVEALED);
                    out.writeObject(champ.get_tabRevealed());
                    out.flush();   
                } catch (IOException e) {
                    System.out.println("Error sending champ revealed to client: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastTabMines(Champ champ) {

        synchronized (outputs) {
            for (ObjectOutputStream out : outputs) {
                try {
                    out.reset();
                    out.writeObject(MessageType.PLAYER_CHAMP);
                    out.writeObject(champ.get_tabMines());
                    out.flush();   
                } catch (IOException e) {
                    System.out.println("Error sending champ of mines to client: " + e.getMessage());
                }
            }
        }
    }

    public List<String> get_playersName(){
        return playerNames;
    }
    
    public void stopListening() {
        listening = false;
        if (listenConexions != null && listenConexions.isAlive()) {
            try {
                listenConexions.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopReceiving(){
        receiving = false;
        if (listenClient != null && listenClient.isAlive()) {
            try {
                listenClient.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }  
    }

    public static void main(String[] args) { 
        // Create and start the server here
    }
}
