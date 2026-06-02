package theknife.serverTK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

                System.out.println("Client connesso: " + clientSocket.getInetAddress());

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );

                PrintWriter output = new PrintWriter(
                        clientSocket.getOutputStream(),
                        true
                );

                String richiesta = input.readLine();
                System.out.println("Richiesta ricevuta: " + richiesta);

                output.println("Risposta dal server: richiesta ricevuta correttamente");

                clientSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Errore server:");
            e.printStackTrace();
        }
    }
}