package theknife.clientTK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTK {

    public static String inviaRichiesta(String richiesta) {

        try {
            Socket socket = new Socket("localhost", 12345);

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            output.println(richiesta);

            String risposta = input.readLine();

            socket.close();

            return risposta;

        } catch (IOException e) {
            e.printStackTrace();
            return "ERRORE|Connessione fallita";
        }
    }
}