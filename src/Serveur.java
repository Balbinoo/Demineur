import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Serveur implements Serializable {
    private final long serialVersionUID = 1L; 
    private int nbJoeur = 0;
    private Thread listenConexions, listenClient;
    private boolean listening, receiving;
    private Champ champ;
    private Gui gui;
    private Case ca;
    private Thread[] listConexions = new Thread[10];  // Initialize with an expected size
    private List<String> playerNames = new ArrayList<>();
    private ServerSocket gestSock;
    private DataInputStream entree;
    private DataOutputStream out;

    Serveur(Champ champ, Case ca) {
        System.out.println("Démarrage du serveur"); 
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
                
                entree = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
              
                count_nbJoueur(); // Increment only after starting the thread
                System.out.println("SERVEUR = Waiting for player name... client number = " + get_nbJoueur());
    
                // Receive player name
                String nomJoueur = entree.readUTF();
                System.out.println("SERVEUR = Player connected: " + nomJoueur);
                playerNames.add(nomJoueur);
                
                synchronized(this) {
                    // Create and start a new thread for each client connection
                    listConexions[get_nbJoueur()] = new Thread(() -> handleClient(get_nbJoueur(), socket, out));
                    listConexions[get_nbJoueur()].start();
                }

                // Broadcast updated player list to all connected clients
                //broadcastPlayerNames();
            }
        } catch (IOException e) {
            System.out.println("SERVEUR = Exception occurred: " + e.getMessage());
            stopListening();
        }
    }

    public void handleClient(int nbJouer, Socket socket, DataOutputStream out) {
        try {


            // Send the player number
            System.out.println("SERVEUR - avant d'envoyer numero "+get_nbJoueur());
            out.writeInt(get_nbJoueur());
            out.flush();

            // Send the updated player list
            //out.writeUTF(nomJoueur);
            //out.flush();

            while(true){
                for (String player : playerNames) {
                    out.writeUTF(player);
                    out.flush();
                    System.out.println("SERVEUR LOOP player=" + player);
                }
                break;
            }

        } catch (IOException e) {
            System.out.println("Error in handling client: " + e.getMessage());
            e.printStackTrace();
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
