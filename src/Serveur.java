import java.net.*; 
import java.io.*;

public class Serveur implements Serializable {
    private static final long serialVersionUID = 1L; 
    private int nbJoeur = 0;
    private Thread listenConexions, listenClient;
    private boolean listening, receiving;
    private  Champ champ;
    private  Gui gui;
    private  Case ca;

    Serveur(Champ champ, Gui gui, Case ca) {
        System.out.println("Démarrage du serveur"); 
        this.listening = false;
        this.receiving = false;
        this.champ = champ;
        this.gui = gui;
        this.ca = ca;
        startServerInBackground(); 
    }

    public void startServerInBackground() {
        if (!listening) {
            listening = true;
            listenConexions = new Thread(() -> initServer());
            listenConexions.start();
        }
    }

    public void initServer() {
        ServerSocket serverSocket = null; // Declare the serverSocket here for later use
        try {
            serverSocket = new ServerSocket(10000);
            while (listening) { // Wait for client connections
                System.out.println("En attente de connexion...");
                Socket socket = serverSocket.accept(); 
                System.out.println("connecté");

                champ.init(0, 0, 0);
                champ.display();

                System.out.println("CHEGOU ATE AQUI!!");
                listenClient = new Thread(() -> handleClient(socket, ++nbJoeur));
                System.out.println("CHegou entre as treads aqui?");
                listenClient.start();
                System.out.println("CRIOU A TREAD?");
            }
        } catch (java.net.BindException e) {
            System.out.println("C'est deja ouvert!!");
            stopListening();
            System.exit(0);
        } catch (IOException e1) {
            System.out.println("Exception different!!");
            stopListening();
            System.exit(0);
        } finally {
            // Ensure that the server socket is closed
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }

    public void handleClient(Socket socket, int nbJoeur) {
        try {
            DataInputStream entree = new DataInputStream(socket.getInputStream());
            DataOutputStream sortie = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
    
            String nomJoueur = entree.readUTF();  
            System.out.println(nomJoueur + " connecté"); 
                
            // Send player number
            sortie.writeInt(nbJoeur);  
            sortie.flush();  
    
            // Send the serialized Champ object to the client
            objectOut.writeObject(champ);
            objectOut.flush();  
            
        } catch (IOException e) {
            e.printStackTrace();
        }
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
