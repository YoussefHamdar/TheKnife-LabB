package theknife.serverTK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                PrintWriter output = new PrintWriter(
                        clientSocket.getOutputStream(),
                        true
                )
        ) {
            String richiesta = input.readLine();

            System.out.println("Richiesta ricevuta dal client: " + richiesta);

            output.println("Risposta dal server: richiesta ricevuta correttamente");

        } catch (IOException e) {
            System.out.println("Errore gestione client:");
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Errore chiusura socket client.");
            }
        }
    }
}