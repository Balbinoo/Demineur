import java.net.*;  
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private App app;
    private String playerName;
    private int playerNumero=0;
    private Socket sock;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Champ champ;  // Assuming Champ is a class you've defined
    private Gui gui;
    private Scanner scanner = new Scanner(System.in);
    private Thread connection;
    private boolean sending = false;
    private boolean receiving = false;
    private List<String> playerNames = new ArrayList<>();
    private String nomJoueur;
    private Thread receivePlayersBroadcast;

    
    public ObjectOutputStream get_out(){
        return out;
    }

    public List<String> get_playersNames(){
        return playerNames;
    }

    Client(App app, Gui gui) {
        this.app = app;
        this.gui = gui;
        System.out.println("Client created");
    }

    public void connectServerBackground() {
        sending = true;
        connection = new Thread(()-> initConnection());
        connection.start();
    }

    public void initConnection() {
        try {
            sock = new Socket("localhost", 10000);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());

            // player number 
           // playerNumero = (int) in.readObject();                 
           // this.setPlayerNumero(playerNumero);
            //System.out.println("CLIENT - numero = "+ playerNumero);

            // create thread to receive broqdcast
            receivePlayersBroadcast = new Thread(() -> receiveBroadcast(in));
            receivePlayersBroadcast.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
    
    public void receiveBroadcast(ObjectInputStream in) {
        receiving = true;
        while (receiving) {
            System.out.println("CLIENT - before receiving player names");
            try {
                Object message = in.readObject(); // Read the incoming object
                
                if (message instanceof MessageType) {
                    MessageType messageType = (MessageType) message;
                    switch (messageType) {
                        case PLAYER_LIST:
                            // Expecting the next object to be the player list
                            playerNames = (ArrayList<String>) in.readObject();
                            /*System.out.println("Player names received:");
                            for (String player : playerNames) {
                                System.out.println("player= " + player);
                            }*/
                            app.updateNames(playerNames);
                            break;
                        case PLAYER_NUMBER:
                            // Expecting the next object to be the player number
                            playerNumero = (Integer) in.readObject();
                            this.setPlayerNumero(playerNumero);
                            System.out.println("CLIENT - numero = " + playerNumero);
                            break;
                        default:
                            System.out.println("Unknown message type: " + messageType);
                            break;
                    }
                }
    
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Connection closed or data stream error.");
                receiving = false; // Exit loop if stream closes or an error occurs
            }
        }
    }

    public void sendPlayerName() {
        System.out.println("Client = Sending player name to server: " + this.getPlayerName());
        try {
            this.out.writeObject(this.getPlayerName());
            this.out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopReceiving() {
        receiving = false;
        if (receivePlayersBroadcast != null && receivePlayersBroadcast.isAlive()) {
            try {
                receivePlayersBroadcast.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopListening() {
        sending = false;
        if (connection != null && connection.isAlive()) {
            try {
                connection.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerNumero(int newPlayerNumero) {
        this.playerNumero = newPlayerNumero;
    }

    public int getPlayerNumero() {
        return this.playerNumero;
    }

    public static void main(String[] args) {
        System.out.println("Client = Main do client");
        //Client client = new Client();
        //client.connectServerBackground();
    }
}
