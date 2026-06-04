package theknife.serverTK;

import theknife.GestioneUtenti;
import theknife.Ristorante;
import theknife.RistoranteDAO;
import theknife.PreferitoDAO;
import theknife.RecensioneDAO;
import theknife.Recensione;

import theknife.Utente;
import theknife.UtenteDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final UtenteDAO utenteDAO;
    private final RistoranteDAO ristoranteDAO;
    private final PreferitoDAO preferitoDAO;
    private final RecensioneDAO recensioneDAO;
    private final GestioneUtenti gestioneUtenti;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.utenteDAO = new UtenteDAO();
        this.ristoranteDAO = new RistoranteDAO();
        this.preferitoDAO = new PreferitoDAO();
        this.recensioneDAO = new RecensioneDAO();
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
            case "REGISTRA":
                return gestisciRegistrazione(parti);
            case "CERCA_RISTORANTI":
                return gestisciRicercaRistoranti(parti);
            case "AGGIUNGI_PREFERITO":
                return gestisciAggiungiPreferito(parti);

            case "VISUALIZZA_PREFERITI":
                return gestisciVisualizzaPreferiti(parti);
            case "AGGIUNGI_RECENSIONE":
                return gestisciAggiungiRecensione(parti);
            case "VISUALIZZA_RECENSIONI":
                return gestisciVisualizzaRecensioni(parti);
            case "MODIFICA_RECENSIONE":
                return gestisciModificaRecensione(parti);
            case "ELIMINA_RECENSIONE":
                return gestisciEliminaRecensione(parti);
                case "RIMUOVI_PREFERITO":
                return gestisciRimuoviPreferito(parti);
            case "RISPONDI_RECENSIONE":
                return gestisciRispondiRecensione(parti);
            case "AGGIUNGI_RISTORANTE":
                return gestisciAggiungiRistorante(parti);
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

    private String gestisciRegistrazione(String[] parti) {
        if (parti.length < 8) {
            return "ERRORE|Formato registrazione non valido";
        }

        String nome = parti[1];
        String cognome = parti[2];
        String username = parti[3];
        String password = parti[4];
        String ruolo = parti[5];
        String domicilio = parti[6];
        String dataNascitaString = parti[7];

        if (utenteDAO.cercaPerUsername(username) != null) {
            return "ERRORE|Username già esistente";
        }

        boolean isRistoratore = ruolo.equalsIgnoreCase("RISTORATORE");

        LocalDate dataNascita = null;
        try {
            if (!dataNascitaString.equalsIgnoreCase("null")) {
                dataNascita = LocalDate.parse(dataNascitaString);
            }
        } catch (Exception e) {
            return "ERRORE|Data di nascita non valida";
        }

        String passwordCifrata = gestioneUtenti.cifraPassword(password);

        Utente nuovoUtente = new Utente(
                nome,
                cognome,
                username,
                passwordCifrata,
                isRistoratore,
                domicilio,
                dataNascita
        );

        boolean inserito = utenteDAO.inserisciUtente(nuovoUtente);

        if (inserito) {
            return "OK|REGISTRAZIONE|Utente registrato";
        } else {
            return "ERRORE|Registrazione fallita";
        }
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
    private String gestisciAggiungiPreferito(String[] parti) {

        if (parti.length < 3) {
            return "ERRORE|Formato preferito non valido";
        }

        String username = parti[1];
        String nomeRistorante = parti[2];

        boolean ok = preferitoDAO.aggiungiPreferito(
                username,
                nomeRistorante
        );

        if (ok) {
            return "OK|PREFERITO_AGGIUNTO";
        }

        return "ERRORE|Impossibile aggiungere preferito";
    }

    private String gestisciVisualizzaPreferiti(String[] parti) {

        if (parti.length < 2) {
            return "ERRORE|Formato richiesta non valido";
        }

        String username = parti[1];

        List<String> preferiti =
                preferitoDAO.visualizzaPreferiti(username);

        if (preferiti.isEmpty()) {
            return "OK|PREFERITI|Nessuno";
        }

        return "OK|PREFERITI|" +
                String.join(",", preferiti);
    }
    private String gestisciAggiungiRecensione(String[] parti) {

        if (parti.length < 5) {
            return "ERRORE|Formato recensione non valido";
        }

        String username = parti[1];
        String nomeRistorante = parti[2];
        String testo = parti[3];
        int stelle = Integer.parseInt(parti[4]);

        Recensione recensione = new Recensione(
                username,
                nomeRistorante,
                testo,
                stelle,
                LocalDate.now()
        );

        boolean ok = recensioneDAO.inserisciRecensione(recensione);

        if (ok) {
            return "OK|RECENSIONE_AGGIUNTA";
        }

        return "ERRORE|Impossibile aggiungere recensione";
    }
    private String gestisciVisualizzaRecensioni(String[] parti) {

        if (parti.length < 2) {
            return "ERRORE|Formato richiesta non valido";
        }

        String nomeRistorante = parti[1];

        List<Recensione> recensioni =
                recensioneDAO.trovaPerRistorante(nomeRistorante);

        if (recensioni.isEmpty()) {
            return "OK|RECENSIONI|Nessuna recensione";
        }

        StringBuilder risposta = new StringBuilder("OK|RECENSIONI");

        for (Recensione r : recensioni) {
            risposta.append("|")
                    .append(r.getAutore())
                    .append(";")
                    .append(r.getStelle())
                    .append(";")
                    .append(r.getTesto().replace("|", " "));
        }

        return risposta.toString();
    }
    private String gestisciRimuoviPreferito(String[] parti) {

        if (parti.length < 3) {
            return "ERRORE|Formato rimozione preferito non valido";
        }

        String username = parti[1];
        String nomeRistorante = parti[2];

        boolean ok = preferitoDAO.rimuoviPreferito(username, nomeRistorante);

        if (ok) {
            return "OK|PREFERITO_RIMOSSO";
        }

        return "ERRORE|Preferito non trovato";
    }
    private String gestisciModificaRecensione(String[] parti) {

        if (parti.length < 5) {
            return "ERRORE|Formato modifica recensione non valido";
        }

        String username = parti[1];
        String nomeRistorante = parti[2];
        String nuovoTesto = parti[3];
        int nuoveStelle = Integer.parseInt(parti[4]);

        boolean ok = recensioneDAO.modificaRecensione(
                username,
                nomeRistorante,
                nuovoTesto,
                nuoveStelle
        );

        if (ok) {
            return "OK|RECENSIONE_MODIFICATA";
        }

        return "ERRORE|Recensione non trovata";
    }

    private String gestisciEliminaRecensione(String[] parti) {

        if (parti.length < 3) {
            return "ERRORE|Formato elimina recensione non valido";
        }

        String username = parti[1];
        String nomeRistorante = parti[2];

        boolean ok = recensioneDAO.eliminaRecensione(
                username,
                nomeRistorante
        );

        if (ok) {
            return "OK|RECENSIONE_ELIMINATA";
        }

        return "ERRORE|Recensione non trovata";
    }
    private String gestisciRispondiRecensione(String[] parti) {

        if (parti.length < 3) {
            return "ERRORE|Formato risposta recensione non valido";
        }

        int idRecensione = Integer.parseInt(parti[1]);
        String risposta = parti[2];

        boolean ok = recensioneDAO.rispondiRecensione(
                idRecensione,
                risposta
        );

        if (ok) {
            return "OK|RISPOSTA_INSERITA";
        }

        return "ERRORE|Recensione non trovata";
    }
    private String gestisciAggiungiRistorante(String[] parti) {

        if (parti.length < 13) {
            return "ERRORE|Formato aggiunta ristorante non valido";
        }

        String nome = parti[1];
        String citta = parti[2];
        String tipoCucina = parti[3];
        String fasciaPrezzo = parti[4];
        boolean delivery = Boolean.parseBoolean(parti[5]);
        boolean prenotazione = Boolean.parseBoolean(parti[6]);
        int prezzoMedio = Integer.parseInt(parti[7]);
        String nazione = parti[8];
        String indirizzo = parti[9];
        double latitudine = Double.parseDouble(parti[10]);
        double longitudine = Double.parseDouble(parti[11]);
        String gestore = parti[12];

        Ristorante ristorante = new Ristorante(
                nome,
                citta,
                0,
                tipoCucina,
                fasciaPrezzo,
                delivery,
                prenotazione,
                prezzoMedio,
                nazione,
                indirizzo,
                latitudine,
                longitudine,
                gestore
        );

        boolean ok = ristoranteDAO.inserisciRistorante(ristorante);

        if (ok) {
            return "OK|RISTORANTE_AGGIUNTO";
        }

        return "ERRORE|Impossibile aggiungere ristorante";
    }
}