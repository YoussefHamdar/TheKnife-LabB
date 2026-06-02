/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */
package theknife;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.HashSet;
/**
 * Rappresenta un ristorante registrato nell'app TheKnife.
 * Contiene dati anagrafici, tipo cucina, coordinate geografiche,
 * recensioni associate e servizi disponibili.
 */


public class Ristorante implements Serializable {
    private static final long serialVersionUID = 1L;



    private String nome;
    private String citta;
    private int stelle;
    private String tipoCucina;
    private String fasciaPrezzo;
    private boolean deliveryDisponibile;
    private boolean prenotazioneOnlineDisponibile;
    private String indirizzo;
    private String nazione;
    private double latitudine;
    private double longitudine;
    private int prezzoMedio;
    private String descrizione;
    private String gestore; // username del ristoratore che lo ha inserito




    private List<Recensione> recensioni = new ArrayList<>();

    /**
     * Costruttore completo per creare un ristorante.
     *
     * @param nome nome del ristorante
     * @param citta città in cui si trova
     * @param stelle valutazione interna
     * @param tipoCucina tipo di cucina offerta
     * @param fasciaPrezzo indicatore economico (€–$$$)
     * @param deliveryDisponibile true se offre consegna
     * @param prenotazioneOnlineDisponibile true se prenotabile online
     * @param prezzoMedio costo medio
     * @param nazione paese del ristorante
     * @param indirizzo via e numero civico
     * @param latitudine coordinata GPS
     * @param longitudine coordinata GPS
     */



    public Ristorante(String nome, String citta, int stelle, String tipoCucina, String fasciaPrezzo, boolean deliveryDisponibile, boolean prenotazioneOnlineDisponibile, int prezzoMedio, String nazione, String indirizzo, double latitudine, double longitudine, String gestore) {
        this.nome = nome;
        this.citta = citta;
        this.stelle = stelle;
        this.tipoCucina = tipoCucina;
        this.fasciaPrezzo = fasciaPrezzo;
        this.deliveryDisponibile = deliveryDisponibile;
        this.prenotazioneOnlineDisponibile = prenotazioneOnlineDisponibile;
        this.prezzoMedio = prezzoMedio;
        this.nazione = nazione;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.gestore=gestore;
    }





    public String getNome() {
        return nome;
    }

    public String getCitta() {
        return citta;
    }

    public int getStelle() {
        return stelle;
    }

    public String getTipoCucina() {
        return tipoCucina;
    }
    public String getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    public boolean isDeliveryDisponibile() {
        return deliveryDisponibile;
    }

    public boolean isPrenotazioneOnlineDisponibile() {
        return prenotazioneOnlineDisponibile;
    }
    /**
     * Restituisce la lista delle recensioni ricevute.
     *
     * @return lista di oggetti Recensione
     */


    public List<Recensione> getRecensioni() {
        return recensioni;
    }
    /**
     * Aggiunge una nuova recensione al ristorante.
     *
     * @param r recensione da aggiungere
     */


    public void aggiungiRecensione(Recensione r) {
        recensioni.add(r);
    }



    /**
     * Rimuove le recensioni duplicate basandosi su equals().
     */
    public void rimuoviRecensioniDuplicate() {
        recensioni = new ArrayList<>(new HashSet<>(recensioni));
    }

    /**
     * Verifica se l'utente ha già recensito questo ristorante.
     *
     * @param username autore della recensione
     * @return true se ha già recensito
     */


    public boolean haRecensioneDellUtente(String username) {
        for (Recensione r : recensioni) {
            if (r.getAutore().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcola la media delle stelle ricevute da tutte le recensioni.
     *
     * @return valore medio (0 se nessuna recensione)
     */


    public double getMediaStelle() {
        if (recensioni.isEmpty()) return 0;
        double somma = 0;
        for (Recensione r : recensioni) {
            somma += r.getStelle();
        }
        return somma / recensioni.size();
    }
    public String getIndirizzo() {
        return indirizzo;
    }

    public String getNazione() {
        return nazione;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public String getPrezzoMedio() {
        return fasciaPrezzo; //
    }


    public String getDescrizione() {
        return descrizione;
    }

    public String getGestore() {
        return gestore;
    }






    /**
     * Ritorna una rappresentazione testuale del ristorante.
     * Include nome, città, stelle, media recensioni e servizi.
     *
     * @return stringa descrittiva
     */


    @Override
    public String toString() {
        String stelleStr = "★".repeat(this.stelle);
        String mediaStr = recensioni.isEmpty() ? "– nessuna recensione" : String.format("– media %.1f★", getMediaStelle());
        String deliveryStr = deliveryDisponibile ? " | Delivery " : "";
        String prenotazioneStr = prenotazioneOnlineDisponibile ? " | Prenotazione Online " : "";

        return nome + " – " + citta + " (" + stelleStr + ") " + mediaStr + deliveryStr + prenotazioneStr;
    }
}