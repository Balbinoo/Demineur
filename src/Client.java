import java.net.*;  
import java.io.*; 
import java.util.Scanner;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int playerNumero;
    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectInputStream objectIn;  // For receiving objects
    private Champ champ;  // Assuming Champ is a class you've defined
    private Gui gui;
    private Scanner scanner = new Scanner(System.in);
    private Thread connection;
    private boolean sending;


    Client() {
        System.out.println("Client created");
    }

    public void connectServerBackground() {
        connection = new Thread(()-> initConnection());
        connection.start();
    }

    public void initConnection() {
        try {
            sock = new Socket("localhost", 10000);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());
            objectIn = new ObjectInputStream(sock.getInputStream());
    
            // player number 
            int playerNumber = in.readInt();
            System.out.println("Player number received: " + playerNumber);
            this.setPlayerNumero(playerNumber);

            // receive the Champ object
            champ = (Champ) objectIn.readObject();
            System.out.println("Champ object received from server");
            champ.display();
    
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("Main do client");
        //Client client = new Client();
        //client.connectServerBackground();
    }
}
