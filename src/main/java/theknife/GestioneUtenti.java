/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */

package theknife;
import theknife.Utente;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;


/**
 * Gestisce la registrazione e il login degli utenti.
 */
/**
 * Classe che gestisce le operazioni sugli utenti.
 * Include funzionalità di registrazione, login, salvataggio su file,
 * caricamento da file o CSV, e cifratura password.
 */

public class GestioneUtenti {

    private List<Utente> utenti;



    /**
     * Costruttore base: inizializza la lista utenti vuota.
     */

    public GestioneUtenti() {
        this.utenti = new ArrayList<>();
    }

    /**

    /**
     * Registra un nuovo utente se lo username è disponibile.
     *
     * @param nome           nome dell'utente
     * @param cognome        cognome dell'utente
     * @param username       username univoco
     * @param password       password da cifrare
     * @param isRistoratore  true se l'utente è un ristoratore
     * @param domicilio      domicilio dell'utente
     * @param dataDiNascita  data di nascita (facoltativa)
     * @return true se la registrazione è avvenuta con successo
     */

    public boolean registraUtente(String nome, String cognome, String username, String password, boolean isRistoratore, String domicilio, LocalDate dataDiNascita) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) {
                return false; // già esiste
            }
        }

        String passwordCifrata = cifraPassword(password);

        Utente nuovo = new Utente(nome, cognome, username, passwordCifrata, isRistoratore, domicilio, dataDiNascita);
        utenti.add(nuovo);
        return true;
    }


    /**
     * Effettua il login dato username e password.
     *
     * @param username username inserito
     * @param password password in chiaro
     * @return oggetto Utente se credenziali corrette, null altrimenti
     */
    public Utente login(String username, String password) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username) && verificaPassword(password, u.getPasswordCifrata())) {
                return u;
            }
        }
        return null;
    }

    /**
     * Cerca un utente dato il suo username.
     *
     * @param username da cercare
     * @return oggetto Utente se trovato, null altrimenti
     */
    public Utente getUtenteDaUsername(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }


    /**
     * Verifica se la password in chiaro corrisponde alla cifrata salvata.
     *
     * @param password password in chiaro
     * @param cifrata password cifrata salvata
     * @return true se combaciano
     */
    private boolean verificaPassword(String password, String cifrata) {
        return cifraPassword(password).equals(cifrata);
    }

    /**
     * Ritorna la lista completa degli utenti registrati.
     *
     * @return lista utenti
     */

    public List<Utente> getTuttiGliUtenti() {
        return utenti;
    }


    /**
     * Carica utenti da un file CSV.
     * Ignora intestazioni e salta righe non valide.
     *
     * @param percorso path del file CSV
     */

    public void caricaDaCSV(String percorso) {
        try (BufferedReader reader = new BufferedReader(new FileReader(percorso))) {
            String riga = reader.readLine(); // salta intestazione
            while ((riga = reader.readLine()) != null) {
                String[] campi = riga.split(",");

                if (campi.length < 7) continue;

                String nome = campi[0].trim();
                String cognome = campi[1].trim();
                String username = campi[2].trim();
                String password = cifraPassword(campi[3].trim());
                // usa metodo già esistente
                String domicilio = campi[4].trim();
                LocalDate nascita = null;
                try {
                    nascita = LocalDate.parse(campi[5].trim());
                } catch (Exception e) {
                    nascita = null; // facoltativa
                }
                boolean isRistoratore = campi[6].trim().equalsIgnoreCase("ristoratore");

                Utente u = new Utente(nome, cognome, username, password, isRistoratore, domicilio, nascita);
                utenti.add(u); // <-- CAMBIATO da listaUtenti
            }
            System.out.println(" Utenti caricati da CSV.");
        } catch (IOException e) {
            System.err.println("" + e.getMessage());
        }
    }
    /**
     * Applica l'hash SHA-256 alla password.
     *
     * @param password password da cifrare
     * @return password cifrata in esadecimale
     */


    public String cifraPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return password; // fallback
        }
    }

    /**
     * Salva la lista degli utenti su file binario (.dat).
     *
     * @param path percorso del file
     */


public void salvaSuFile(String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(utenti); //  usa la variabile corretta
            System.out.println(" Utenti salvati su file.");
        } catch (IOException e) {
            System.err.println("Errore salvataggio utenti: " + e.getMessage());
        }
    }


    /**
     * Carica la lista utenti da un file binario (.dat).
     * Se il file non esiste, crea lista vuota.
     *
     * @param path percorso del file da caricare
     */

    @SuppressWarnings("unchecked")
    public void caricaDaFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println(" Nessun file utenti trovato, lista vuota creata.");
            utenti = new ArrayList<>(); //  assegna alla variabile corretta
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            utenti = (List<Utente>) in.readObject(); //  correggi qui
            System.out.println("Utenti caricati da file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore caricamento utenti: " + e.getMessage());
            utenti = new ArrayList<>();
        }
    }

    /**
     * Verifica se lo username è disponibile (non ancora registrato).
     *
     * @param username da controllare
     * @return true se disponibile, false se già esiste
     */
    public boolean usernameDisponibile(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }



}
