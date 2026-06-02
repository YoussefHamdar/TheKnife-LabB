package theknife.serverTK;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTK {

    public static final int PORT = 12345;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("ServerTK avviato sulla porta " + PORT);
            System.out.println("In attesa di connessioni...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Errore server:");
            e.printStackTrace();
        }
    }
}