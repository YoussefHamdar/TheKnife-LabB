package theknife.gui;

import theknife.clientTK.ClientTK;

import javax.swing.*;
import java.awt.*;

public class TheKnifeGUI extends JFrame {

    private JTextArea areaOutput;

    public TheKnifeGUI() {

        setTitle("TheKnife");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel titolo = new JLabel("TheKnife", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel pulsanti = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton loginButton = new JButton("Login");
        JButton registrazioneButton = new JButton("Registrazione");
        JButton ricercaButton = new JButton("Ricerca Ristoranti");

        pulsanti.add(loginButton);
        pulsanti.add(registrazioneButton);
        pulsanti.add(ricercaButton);

        areaOutput = new JTextArea();
        areaOutput.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(areaOutput);

        panel.add(titolo, BorderLayout.NORTH);
        panel.add(pulsanti, BorderLayout.WEST);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        loginButton.addActionListener(e -> {

            String username = JOptionPane.showInputDialog(
                    this,
                    "Username:"
            );

            if (username == null || username.isBlank()) {
                return;
            }

            String password = JOptionPane.showInputDialog(
                    this,
                    "Password:"
            );

            if (password == null || password.isBlank()) {
                return;
            }

            String risposta = ClientTK.inviaRichiesta(
                    "LOGIN|" + username + "|" + password
            );

            areaOutput.setText(risposta);
        });

        registrazioneButton.addActionListener(e -> {

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
        });

        ricercaButton.addActionListener(e -> {

            String citta = JOptionPane.showInputDialog(
                    this,
                    "Inserisci città:"
            );

            if (citta == null || citta.isBlank()) {
                return;
            }

            String risposta = ClientTK.inviaRichiesta(
                    "CERCA_RISTORANTI|" + citta
            );

            areaOutput.setText(formattaRispostaRistoranti(risposta));
        });
    }

    private String formattaRispostaRistoranti(String risposta) {

        if (risposta == null) {
            return "Nessuna risposta dal server.";
        }

        if (risposta.startsWith("ERRORE")) {
            return risposta;
        }

        String[] parti = risposta.split("\\|");

        if (parti.length < 3) {
            return risposta;
        }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TheKnifeGUI().setVisible(true)
        );
    }
}