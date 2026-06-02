/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */
package theknife;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Set;
import java.util.HashSet;


/**
 * Classe che gestisce le recensioni degli utenti.
 * Permette di aggiungere, modificare, rimuovere, calcolare medie,
 * e salvare/caricare recensioni su file.
 */

public class RecensioneManager {
    private List<Recensione> recensioni;

    /** * Costruttore: inizializza la lista di recensioni. */
    public RecensioneManager() {
        this.recensioni = new ArrayList<>();
    }

    /**
     * Aggiunge una nuova recensione all'elenco.
     *
     * @param username username dell'autore
     * @param nomeRistorante nome del ristorante recensito
     * @param testo testo della recensione
     * @param stelle numero di stelle (1-5)
     */
    public void aggiungiRecensione(String username, String nomeRistorante, String testo, int stelle) {
        Recensione r = new Recensione(username, nomeRistorante, testo, stelle, LocalDate.now());
        if (!recensioni.contains(r)) {
            recensioni.add(r);
        }
    }


    /**
     * Restituisce l'elenco di tutte le recensioni.
     *
     * @return lista di recensioni
     */
    public List<Recensione> getTutteLeRecensioni() {
        return recensioni;
    }

    /**
     * Cancella una recensione dall'elenco.
     *
     * @param r la recensione da rimuovere
     */
    public void rimuoviRecensione(Recensione r) {
        recensioni.remove(r);
    }

    /**
     * Modifica il testo e/o le stelle di una recensione esistente.
     *
     * @param r recensione da modificare
     * @param nuovoTesto nuovo contenuto
     * @param nuoveStelle nuovo voto in stelle
     */
    public void modificaRecensione(Recensione r, String nuovoTesto, int nuoveStelle) {
        r.setTesto(nuovoTesto);
        r.setStelle(nuoveStelle);
    }

    /**
     * Calcola la valutazione media di tutte le recensioni esistenti.
     *
     * @return media numerica o 0 se non ci sono recensioni
     */
    public double calcolaMediaStelle() {
        if (recensioni.isEmpty()) return 0;
        int somma = 0;
        for (Recensione r : recensioni) {
            somma += r.getStelle();
        }
        return (double) somma / recensioni.size();
    }
    /**
     * Salva tutte le recensioni su file binario (.dat).
     *
     * @param path percorso del file di destinazione
     */

    public void salvaSuFile(String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(recensioni);
            System.out.println("Recensioni salvate su file.");
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio recensioni: " + e.getMessage());
        }
    }

    /**
     * Carica le recensioni da un file binario (.dat).
     * In caso di errore, inizializza una lista vuota.
     *
     * @param path percorso del file da caricare
     */

    @SuppressWarnings("unchecked")
    public void caricaDaFile(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            recensioni = (List<Recensione>) in.readObject();
            System.out.println("Recensioni caricate da file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore nel caricamento recensioni: " + e.getMessage());
            recensioni = new ArrayList<>(); // fallback
        }
    }


    /**
     * Associa ogni recensione presente nel sistema al ristorante corrispondente.
     *
     * Per ogni {@link Recensione} nella lista interna, il metodo cerca un {@link Ristorante}
     * nella lista fornita il cui nome corrisponda (ignorando maiuscole/minuscole) al nome del ristorante
     * indicato nella recensione. Se trova una corrispondenza e la recensione non è già presente
     * nella lista del ristorante, la aggiunge.
     *
     * @param ristoranti lista di ristoranti a cui associare le recensioni
     */
    public void associaRecensioni(List<Ristorante> ristoranti) {
        for (Recensione recensione : recensioni) {
            for (Ristorante r : ristoranti) {
                if (r.getNome().equalsIgnoreCase(recensione.getNomeRistorante())) {
                    if (!r.getRecensioni().contains(recensione)) {
                        r.aggiungiRecensione(recensione);
                    }
                    break;
                }
            }
        }
    }


    /**
     * Rimuove le recensioni duplicate dalla lista interna.
     *
     * Una recensione è considerata duplicata se ha lo stesso autore, nome del ristorante,
     * testo, numero di stelle e data. Il metodo costruisce una chiave univoca per ciascuna
     * recensione e conserva solo la prima occorrenza di ogni chiave.
     *
     * Dopo la rimozione, la lista aggiornata viene salvata su file e viene stampato
     * un messaggio di conferma.
     */

    public void rimuoviDuplicati() {
        Set<String> chiaviUniche = new HashSet<>();
        List<Recensione> filtrate = new ArrayList<>();

        for (Recensione r : recensioni) {
            String chiave = r.getAutore() + "|" + r.getNomeRistorante() + "|" + r.getTesto() + "|" + r.getStelle() + "|" + r.getData();
            if (!chiaviUniche.contains(chiave)) {
                chiaviUniche.add(chiave);
                filtrate.add(r);
            }
        }

        recensioni = filtrate;
        salvaSuFile("data/recensioni.dat");
        System.out.println(" Recensioni duplicate rimosse.");
    }








}
