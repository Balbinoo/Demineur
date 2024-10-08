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
    private DataInputStream in;
    private DataOutputStream out;
    private Champ champ;  // Assuming Champ is a class you've defined
    private Gui gui;
    private Scanner scanner = new Scanner(System.in);
    private Thread connection;
    private boolean sending = false;
    private boolean receiving = false;
    private List<String> playerNames = new ArrayList<>();
    private String nomJoueur;

    
    public DataOutputStream get_out(){
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
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());
    
            // player number 
            playerNumero = in.readInt();                 
            this.setPlayerNumero(playerNumero);
            System.out.println("CLIENT - numero = "+ playerNumero);

            
            for(int i = 1; i <= playerNumero;i++){
                nomJoueur = in.readUTF();
                System.out.println("Client LOOP player " + nomJoueur);
                playerNames.add(nomJoueur);
                System.out.println("Ajoute le nom?");
            }          
            app.updateNames(playerNames);
            //gui.setconfigLeftPanel(playerNames);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    public void sendPlayerName() {
        System.out.println("Client = Sending player name to server: " + this.getPlayerName());
        try {
            this.out.writeUTF(this.getPlayerName());
            this.out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
