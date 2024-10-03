import java.net.*; 
import java.io.*;

public class Serveur {

    private int nbJoeur = 0;

    Serveur(){
        System.out.println("Démarrage du serveur"); 
    }

    public void startServerInBackground() {
        new Thread(() -> initServer()).start();
    }

    public void initServer() {
        try {
            ServerSocket gestSock = new ServerSocket(10000);  

            while (true) {
                System.out.println("En attente de connexion...");

                Socket socket = gestSock.accept(); 
                System.out.println("connecté");

                new Thread(() -> handleClient(socket, ++nbJoeur)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClient(Socket socket, int nbJoeur) {
        try {
            DataInputStream entree = new DataInputStream(socket.getInputStream()); 
            DataOutputStream sortie = new DataOutputStream(socket.getOutputStream()); 

            String nomJoueur = entree.readUTF();  
            System.out.println(nomJoueur + " connecté"); 
            
            sortie.writeInt(nbJoeur);   

            while (!socket.isClosed()) {
                try {
                    String message = entree.readUTF();
                    System.out.println("Message reçu de " + nomJoueur + ": " + message);

                    sortie.writeUTF("Réponse du serveur: " + message);

                    if (message.equalsIgnoreCase("exit")) {
                        System.out.println(nomJoueur + " a fermé la connexion.");
                        break;
                    }

                } catch (EOFException e) {
                    System.out.println("Connexion terminée avec " + nomJoueur);
                    break;
                }
            }

            sortie.close(); 
            entree.close(); 
            socket.close(); 
            
            System.out.println("Connexion fermée pour " + nomJoueur);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { 
        new Serveur().startServerInBackground();  
    }
}
