package theknife.serverTK;

import theknife.GestioneUtenti;
import theknife.Ristorante;
import theknife.RistoranteDAO;
import theknife.Utente;
import theknife.UtenteDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final UtenteDAO utenteDAO;
    private final RistoranteDAO ristoranteDAO;
    private final GestioneUtenti gestioneUtenti;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.utenteDAO = new UtenteDAO();
        this.ristoranteDAO = new RistoranteDAO();
        this.gestioneUtenti = new GestioneUtenti();
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

            String risposta = gestisciRichiesta(richiesta);

            output.println(risposta);

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

    private String gestisciRichiesta(String richiesta) {
        if (richiesta == null || richiesta.isBlank()) {
            return "ERRORE|Richiesta vuota";
        }

        String[] parti = richiesta.split("\\|");
        String comando = parti[0];

        switch (comando) {
            case "PING":
                return "OK|PONG";

            case "LOGIN":
                return gestisciLogin(parti);

            case "CERCA_RISTORANTI":
                return gestisciRicercaRistoranti(parti);

            default:
                return "ERRORE|Comando non riconosciuto";
        }
    }

    private String gestisciLogin(String[] parti) {
        if (parti.length < 3) {
            return "ERRORE|Formato login non valido";
        }

        String username = parti[1];
        String password = parti[2];

        Utente utente = utenteDAO.cercaPerUsername(username);

        if (utente == null) {
            return "ERRORE|Utente non trovato";
        }

        String passwordCifrata = gestioneUtenti.cifraPassword(password);

        if (!utente.getPasswordCifrata().equals(passwordCifrata)) {
            return "ERRORE|Password errata";
        }

        String ruolo = utente.isRistoratore() ? "RISTORATORE" : "CLIENTE";

        return "OK|LOGIN|" + utente.getUsername() + "|" + ruolo;
    }

    private String gestisciRicercaRistoranti(String[] parti) {
        if (parti.length < 2) {
            return "ERRORE|Formato ricerca non valido";
        }

        String citta = parti[1];

        List<Ristorante> risultati = ristoranteDAO.cercaPerCitta(citta);

        if (risultati.isEmpty()) {
            return "OK|RISTORANTI|Nessun ristorante trovato";
        }

        StringBuilder risposta = new StringBuilder("OK|RISTORANTI");

        for (Ristorante r : risultati) {
            risposta.append("|")
                    .append(r.getNome().replace("|", " "))
                    .append(";")
                    .append(r.getCitta().replace("|", " "))
                    .append(";")
                    .append(r.getTipoCucina().replace("|", " "))
                    .append(";")
                    .append(r.getFasciaPrezzo().replace("|", " "));
        }

        return risposta.toString();
    }
}