package theknife.clientTK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTK {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 12345);

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            output.println("LOGIN|mario.rossi|password");

            String risposta = input.readLine();

            System.out.println("Risposta ricevuta dal server:");
            System.out.println(risposta);

            socket.close();

        } catch (IOException e) {
            System.out.println("Errore connessione:");
            e.printStackTrace();
        }
    }
}