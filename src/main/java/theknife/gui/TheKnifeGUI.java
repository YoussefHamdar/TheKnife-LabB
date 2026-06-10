package theknife.gui;

import theknife.clientTK.ClientTK;

import javax.swing.*;
import java.awt.*;

public class TheKnifeGUI extends JFrame {

    private JTextArea areaOutput;
    private String usernameLoggato;
    private String ruoloLoggato;

    private JButton aggiungiPreferitoButton;
    private JButton visualizzaPreferitiButton;
    private JButton rimuoviPreferitoButton;
    private JButton aggiungiRecensioneButton;
    private JButton visualizzaRecensioniButton;
    private JButton eliminaRecensioneButton;
    private JButton modificaRecensioneButton;
    private JButton aggiungiRistoranteButton;
    private JButton rispondiRecensioneButton;
    private JButton riepilogoButton;

    public TheKnifeGUI() {

        setTitle("TheKnife");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel titolo = new JLabel("TheKnife", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel pulsanti = new JPanel(new GridLayout(13, 1, 5, 5));

        JButton loginButton = new JButton("Login");
        JButton registrazioneButton = new JButton("Registrazione");
        JButton ricercaButton = new JButton("Ricerca Ristoranti");

        aggiungiPreferitoButton = new JButton("Aggiungi Preferito");
        visualizzaPreferitiButton = new JButton("Visualizza Preferiti");
        rimuoviPreferitoButton = new JButton("Rimuovi Preferito");
        aggiungiRecensioneButton = new JButton("Aggiungi Recensione");
        visualizzaRecensioniButton = new JButton("Visualizza Recensioni");
        eliminaRecensioneButton = new JButton("Elimina Recensione");
        modificaRecensioneButton = new JButton("Modifica Recensione");
        aggiungiRistoranteButton = new JButton("Aggiungi Ristorante");
        rispondiRecensioneButton = new JButton("Rispondi Recensione");
        riepilogoButton = new JButton("Visualizza Riepilogo");

        pulsanti.add(loginButton);
        pulsanti.add(registrazioneButton);
        pulsanti.add(ricercaButton);
        pulsanti.add(aggiungiPreferitoButton);
        pulsanti.add(visualizzaPreferitiButton);
        pulsanti.add(rimuoviPreferitoButton);
        pulsanti.add(aggiungiRecensioneButton);
        pulsanti.add(visualizzaRecensioniButton);
        pulsanti.add(eliminaRecensioneButton);
        pulsanti.add(modificaRecensioneButton);
        pulsanti.add(aggiungiRistoranteButton);
        pulsanti.add(rispondiRecensioneButton);
        pulsanti.add(riepilogoButton);

        areaOutput = new JTextArea();
        areaOutput.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(areaOutput);

        panel.add(titolo, BorderLayout.NORTH);
        panel.add(pulsanti, BorderLayout.WEST);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        loginButton.addActionListener(e -> eseguiLogin());
        registrazioneButton.addActionListener(e -> eseguiRegistrazione());
        ricercaButton.addActionListener(e -> eseguiRicerca());
        aggiungiPreferitoButton.addActionListener(e -> aggiungiPreferito());
        visualizzaPreferitiButton.addActionListener(e -> visualizzaPreferiti());
        rimuoviPreferitoButton.addActionListener(e -> rimuoviPreferito());
        aggiungiRecensioneButton.addActionListener(e -> aggiungiRecensione());
        visualizzaRecensioniButton.addActionListener(e -> visualizzaRecensioni());
        eliminaRecensioneButton.addActionListener(e -> eliminaRecensione());
        modificaRecensioneButton.addActionListener(e -> modificaRecensione());
        aggiungiRistoranteButton.addActionListener(e -> aggiungiRistorante());
        rispondiRecensioneButton.addActionListener(e -> rispondiRecensione());
        riepilogoButton.addActionListener(e -> visualizzaRiepilogo());

        aggiornaPulsantiPerRuolo();
    }

    private void eseguiLogin() {
        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.isBlank()) return;

        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null || password.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "LOGIN|" + username + "|" + password
        );

        if (risposta.startsWith("OK|LOGIN")) {
            String[] parti = risposta.split("\\|");
            usernameLoggato = parti[2];
            ruoloLoggato = parti[3];

            aggiornaPulsantiPerRuolo();

            areaOutput.setText(
                    "Login riuscito.\n" +
                            "Benvenuto: " + usernameLoggato + "\n" +
                            "Ruolo: " + ruoloLoggato
            );
        } else {
            areaOutput.setText(risposta);
        }
    }

    private void eseguiRegistrazione() {
        String nome = JOptionPane.showInputDialog(this, "Nome:");
        if (nome == null || nome.isBlank()) return;

        String cognome = JOptionPane.showInputDialog(this, "Cognome:");
        if (cognome == null || cognome.isBlank()) return;

        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.isBlank()) return;

        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null || password.isBlank()) return;

        String ruolo = JOptionPane.showInputDialog(this, "Ruolo: CLIENTE oppure RISTORATORE");
        if (ruolo == null || ruolo.isBlank()) return;

        String domicilio = JOptionPane.showInputDialog(this, "Domicilio:");
        if (domicilio == null || domicilio.isBlank()) return;

        String dataNascita = JOptionPane.showInputDialog(this, "Data nascita YYYY-MM-DD oppure null:");
        if (dataNascita == null || dataNascita.isBlank()) return;

        String richiesta = "REGISTRA|" + nome + "|" + cognome + "|" + username + "|" +
                password + "|" + ruolo + "|" + domicilio + "|" + dataNascita;

        String risposta = ClientTK.inviaRichiesta(richiesta);
        areaOutput.setText(risposta);
    }

    private void eseguiRicerca() {
        String citta = JOptionPane.showInputDialog(this, "Inserisci città:");
        if (citta == null || citta.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "CERCA_RISTORANTI|" + citta
        );

        areaOutput.setText(formattaRispostaRistoranti(risposta));
    }

    private void aggiungiPreferito() {
        if (!utenteLoggato()) return;

        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "AGGIUNGI_PREFERITO|" + usernameLoggato + "|" + nomeRistorante
        );

        areaOutput.setText(risposta);
    }

    private void rimuoviPreferito() {
        if (!utenteLoggato()) return;

        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante da rimuovere:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "RIMUOVI_PREFERITO|" + usernameLoggato + "|" + nomeRistorante
        );

        areaOutput.setText(risposta);
    }

    private void visualizzaPreferiti() {
        if (!utenteLoggato()) return;

        String risposta = ClientTK.inviaRichiesta(
                "VISUALIZZA_PREFERITI|" + usernameLoggato
        );

        areaOutput.setText(risposta);
    }

    private void aggiungiRecensione() {
        if (!utenteLoggato()) return;

        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String testo = JOptionPane.showInputDialog(this, "Testo recensione:");
        if (testo == null || testo.isBlank()) return;

        String stelle = JOptionPane.showInputDialog(this, "Stelle da 1 a 5:");
        if (stelle == null || stelle.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "AGGIUNGI_RECENSIONE|" + usernameLoggato + "|" + nomeRistorante + "|" + testo + "|" + stelle
        );

        areaOutput.setText(risposta);
    }

    private void eliminaRecensione() {
        if (!utenteLoggato()) return;

        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "ELIMINA_RECENSIONE|" + usernameLoggato + "|" + nomeRistorante
        );

        areaOutput.setText(risposta);
    }

    private void modificaRecensione() {
        if (!utenteLoggato()) return;

        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String nuovoTesto = JOptionPane.showInputDialog(this, "Nuovo testo recensione:");
        if (nuovoTesto == null || nuovoTesto.isBlank()) return;

        String nuoveStelle = JOptionPane.showInputDialog(this, "Nuove stelle da 1 a 5:");
        if (nuoveStelle == null || nuoveStelle.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "MODIFICA_RECENSIONE|" + usernameLoggato + "|" + nomeRistorante + "|" + nuovoTesto + "|" + nuoveStelle
        );

        areaOutput.setText(risposta);
    }

    private void visualizzaRecensioni() {
        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "VISUALIZZA_RECENSIONI|" + nomeRistorante
        );

        areaOutput.setText(formattaRispostaRecensioni(risposta));
    }

    private void aggiungiRistorante() {
        if (!utenteLoggato()) return;

        String nome = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nome == null || nome.isBlank()) return;

        String citta = JOptionPane.showInputDialog(this, "Città:");
        if (citta == null || citta.isBlank()) return;

        String tipoCucina = JOptionPane.showInputDialog(this, "Tipo cucina:");
        if (tipoCucina == null || tipoCucina.isBlank()) return;

        String fasciaPrezzo = JOptionPane.showInputDialog(this, "Fascia prezzo (€ / €€ / €€€):");
        if (fasciaPrezzo == null || fasciaPrezzo.isBlank()) return;

        String delivery = JOptionPane.showInputDialog(this, "Delivery true/false:");
        if (delivery == null || delivery.isBlank()) return;

        String prenotazione = JOptionPane.showInputDialog(this, "Prenotazione online true/false:");
        if (prenotazione == null || prenotazione.isBlank()) return;

        String prezzoMedio = JOptionPane.showInputDialog(this, "Prezzo medio:");
        if (prezzoMedio == null || prezzoMedio.isBlank()) return;

        String nazione = JOptionPane.showInputDialog(this, "Nazione:");
        if (nazione == null || nazione.isBlank()) return;

        String indirizzo = JOptionPane.showInputDialog(this, "Indirizzo:");
        if (indirizzo == null || indirizzo.isBlank()) return;

        String latitudine = JOptionPane.showInputDialog(this, "Latitudine:");
        if (latitudine == null || latitudine.isBlank()) return;

        String longitudine = JOptionPane.showInputDialog(this, "Longitudine:");
        if (longitudine == null || longitudine.isBlank()) return;

        String richiesta =
                "AGGIUNGI_RISTORANTE|"
                        + nome + "|"
                        + citta + "|"
                        + tipoCucina + "|"
                        + fasciaPrezzo + "|"
                        + delivery + "|"
                        + prenotazione + "|"
                        + prezzoMedio + "|"
                        + nazione + "|"
                        + indirizzo + "|"
                        + latitudine + "|"
                        + longitudine + "|"
                        + usernameLoggato;

        String risposta = ClientTK.inviaRichiesta(richiesta);
        areaOutput.setText(risposta);
    }

    private void rispondiRecensione() {
        if (!utenteLoggato()) return;

        String id = JOptionPane.showInputDialog(this, "ID recensione:");
        if (id == null || id.isBlank()) return;

        String rispostaTesto = JOptionPane.showInputDialog(this, "Risposta del ristoratore:");
        if (rispostaTesto == null || rispostaTesto.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "RISPONDI_RECENSIONE|" + id + "|" + rispostaTesto
        );

        areaOutput.setText(risposta);
    }

    private void visualizzaRiepilogo() {
        if (!utenteLoggato()) return;

        String nomeRistorante = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (nomeRistorante == null || nomeRistorante.isBlank()) return;

        String risposta = ClientTK.inviaRichiesta(
                "VISUALIZZA_RIEPILOGO|" + nomeRistorante
        );

        areaOutput.setText(risposta);
    }

    private boolean utenteLoggato() {
        if (usernameLoggato == null) {
            areaOutput.setText("Devi prima effettuare il login.");
            return false;
        }
        return true;
    }

    private void aggiornaPulsantiPerRuolo() {
        boolean loggato = usernameLoggato != null;
        boolean cliente = "CLIENTE".equalsIgnoreCase(ruoloLoggato);
        boolean ristoratore = "RISTORATORE".equalsIgnoreCase(ruoloLoggato);

        aggiungiPreferitoButton.setVisible(loggato && cliente);
        visualizzaPreferitiButton.setVisible(loggato && cliente);
        rimuoviPreferitoButton.setVisible(loggato && cliente);
        aggiungiRecensioneButton.setVisible(loggato && cliente);
        eliminaRecensioneButton.setVisible(loggato && cliente);
        modificaRecensioneButton.setVisible(loggato && cliente);

        visualizzaRecensioniButton.setVisible(true);

        aggiungiRistoranteButton.setVisible(loggato && ristoratore);
        rispondiRecensioneButton.setVisible(loggato && ristoratore);
        riepilogoButton.setVisible(loggato && ristoratore);
    }

    private String formattaRispostaRistoranti(String risposta) {
        if (risposta == null) return "Nessuna risposta dal server.";
        if (risposta.startsWith("ERRORE")) return risposta;

        String[] parti = risposta.split("\\|");
        if (parti.length < 3) return risposta;

        StringBuilder testo = new StringBuilder();
        testo.append("Ristoranti trovati:\n\n");

        for (int i = 2; i < parti.length; i++) {
            String[] dati = parti[i].split(";");

            if (dati.length >= 4) {
                testo.append("Nome: ").append(dati[0]).append("\n");
                testo.append("Città: ").append(dati[1]).append("\n");
                testo.append("Cucina: ").append(dati[2]).append("\n");
                testo.append("Prezzo: ").append(dati[3]).append("\n");
                testo.append("----------------------\n");
            }
        }

        return testo.toString();
    }

    private String formattaRispostaRecensioni(String risposta) {
        if (risposta == null) return "Nessuna risposta dal server.";
        if (risposta.startsWith("ERRORE")) return risposta;

        String[] parti = risposta.split("\\|");
        if (parti.length < 3) return risposta;

        StringBuilder testo = new StringBuilder();
        testo.append("Recensioni:\n\n");

        for (int i = 2; i < parti.length; i++) {
            String[] dati = parti[i].split(";");

            if (dati.length >= 3) {
                testo.append("Autore: ").append(dati[0]).append("\n");
                testo.append("Stelle: ").append(dati[1]).append("\n");
                testo.append("Testo: ").append(dati[2]).append("\n");
                testo.append("----------------------\n");
            }
        }

        return testo.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TheKnifeGUI().setVisible(true)
        );
    }
}