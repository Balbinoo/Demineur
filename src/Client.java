import java.net.*;  
import java.io.*; 
import java.util.Scanner;

public class Client {

    private Socket sock;
    private DataOutputStream out;
    private DataInputStream in;
    private String playerName;
    Scanner scanner = new Scanner(System.in);

    Client() {
        System.out.println("Client created");
    }

    public void connectServerBackground(){
        new Thread(() -> initConnexion()).start();
    }

    public void initConnexion(){
        try {
            sock = new Socket("localhost", 10000);  
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());

            System.out.print("Ecrit ton prenom: ");
            playerName = scanner.nextLine();  

            out.writeUTF(playerName);  

            int numJoueur = in.readInt();  
            System.out.println("Joueur n°: " + numJoueur);

            new Thread(() -> sendMessages()).start();

            receiveMessages();

        } catch (UnknownHostException e) {
            System.out.println("Host desconhecido");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages() {
        Scanner scanner = new Scanner(System.in);
        try {
            String message;
            while (true) {
                System.out.print("Digite uma mensagem (ou 'exit' para sair): ");
                message = scanner.nextLine();  
                out.writeUTF(message); 

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Encerrando a conexão...");
                    sock.close();
                    break;
                }
            }
            scanner.close();  
        } catch (IOException e) {
            System.out.println("Erro ao enviar mensagem");
            e.printStackTrace();
        }
    }

    public void receiveMessages() {
        try {
            while (!sock.isClosed()) {
                String message = in.readUTF();  
                System.out.println("Mensagem do servidor: " + message); 
            }
        } catch (IOException e) {
            if (!sock.isClosed()) {
                System.out.println("Conexão encerrada pelo servidor.");
            }
        }
    }

    public static void main(String[] args) {
        new Client();  
    }
}
