import java.net.*;  
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

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

    public void close_out(){
        try {
            get_out().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectInputStream get_in(){
        return in;
    }

    public void close_in(){
        try {
            get_in().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket get_socket(){
        return sock;
    }

    public void close_socket(){
        try {
            get_socket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> get_playersNames(){
        return playerNames;
    }

    Client(App app, Gui gui) {
        this.app = app;
        this.gui = gui;
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
            try {
                Object message = in.readObject();
                
                if (message instanceof MessageType) {
                    MessageType messageType = (MessageType) message;
                    switch (messageType) {
                        case PLAYER_LIST:
                            playerNames = (ArrayList<String>) in.readObject();
                            app.updateNames(playerNames);
                            break;
                        case PLAYER_NUMBER:
                            playerNumero = (Integer) in.readObject();
                            this.setPlayerNumero(playerNumero);
                            break;
                        case PLAYER_CHAMP:
                            boolean [][] mines = new boolean[5][5];
                            mines = (boolean[][]) in.readObject();
                            app.setClientMines(mines);
                            break;
                        case PLAYER_REVEALED:
                            boolean [][] revealed = new boolean[5][5];
                            revealed = (boolean[][]) in.readObject();
                            app.setClientRevealed(revealed);
                        break;
                        case PLAYER_XY:
                            List<Integer>xy =  (List<Integer>)in.readObject();
                            app.setUpdateClientXY(xy.get(0), xy.get(1));
                        break;
                        case GAME_OVER:
                            int[] scorePlayersLost = (int[]) in.readObject();
                            showScores("Game Over", scorePlayersLost);
                        break;
                        case GAME_WON:
                            int[] scorePlayersWon = (int[]) in.readObject();
                            showScores("Game Won", scorePlayersWon);
                        break;
                        default:
                            System.out.println("CLIENT - Unknown message type: " + messageType);
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

    private void showScores(String title, int[] scores) {
        // Build the message string for the JOptionPane
        int j = 1;
        StringBuilder infoMessage = new StringBuilder("Scores:\n");
        for (int i = 0; i < playerNames.size(); i++) {
            String name = playerNames.get(i);
            infoMessage.append(name).append(" : ").append(scores[j]).append("\n");
            j++;
        }
    
        // Show the scores in a JOptionPane
        JOptionPane.showMessageDialog(null, infoMessage.toString(), title, JOptionPane.INFORMATION_MESSAGE);
        app.quit();
    }

    public void sendPlayerName() {
        try {
            this.out.writeObject(this.getPlayerName());
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendClickXY(int row, int col){
        List<Integer>xy = new ArrayList<Integer>();
        xy.add(row);
        xy.add(col);

        try {
            this.out.writeObject(xy);
            this.out.flush();
        } catch (IOException e) {
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
    }
}
