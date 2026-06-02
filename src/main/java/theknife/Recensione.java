/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */
package theknife;

import java.time.LocalDate;
import java.io.Serializable;



/**
 * Rappresenta una recensione lasciata da un utente su un ristorante.
 */
public class Recensione implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Restituisce il nome o username dell'autore.
     * @return autore della recensione
     */

    private String autore;
    /**
     * Restituisce il nome del ristorante recensito.
     * @return nome ristorante
     */

    private String nomeRistorante;
    private String testo;
    /**
     * Restituisce il contenuto testuale della recensione.
     * @return testo della recensione
     */

    private int stelle;
    /**
     * Restituisce il numero di stelle assegnate.
     * @return valore da 1 a 5
     */

    private LocalDate data;
    /**
     * Restituisce la data in cui è stata scritta la recensione.
     * @return data della recensione
     */

    private String rispostaDelRistoratore;
    /**
     * Restituisce la risposta data dal ristoratore, se presente.
     * @return testo della risposta o null se assente
     */


    /**
     * Costruttore base della recensione.
     *
     * @param autore autore della recensione (username o nome)
     * @param testo contenuto della recensione
     * @param stelle numero di stelle (1–5)
     * @param data data in cui è stata scritta
     */
    public Recensione(String autore, String nomeRistorante, String testo, int stelle, LocalDate data) {
        this.autore = autore;
        this.nomeRistorante = nomeRistorante;
        this.testo = testo;
        this.stelle = stelle;
        this.data = data;
        this.rispostaDelRistoratore = null; // inizialmente nessuna risposta
    }

    public String getAutore() { return autore; }
    public String getNomeRistorante() { return nomeRistorante; }
    public String getTesto() { return testo; }
    public int getStelle() { return stelle; }
    public LocalDate getData() { return data; }
    public String getRispostaDelRistoratore() { return rispostaDelRistoratore; }

    /**
     * Modifica il testo della recensione.
     * @param nuovoTesto testo aggiornato
     */

        public void setTesto(String nuovoTesto) {
        this.testo = nuovoTesto;
    }
    /**
     * Modifica il numero di stelle assegnato.
     * @param nuoveStelle valore aggiornato
     */

    public void setStelle(int nuoveStelle) {
        this.stelle = nuoveStelle;
    }
    /**
     * Aggiunge o modifica la risposta del ristoratore.
     * @param risposta testo della risposta
     */

    public void setRispostaDelRistoratore(String risposta) {
        this.rispostaDelRistoratore = risposta;
    }
    /**
     * Restituisce la recensione in formato testuale leggibile.
     * Include eventuale risposta del ristoratore.
     * @return stringa descrittiva
     */

    @Override
    public String toString() {
        String output = "Recensione di " + autore +
                " sul ristorante '" + nomeRistorante +
                "' (" + stelle + ") [" + data + "]\n" + testo;
        if (rispostaDelRistoratore != null) {
            output += "\n Risposta del ristoratore: " + rispostaDelRistoratore;
        }
        return output;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Recensione other = (Recensione) obj;
        return autore.equals(other.autore) &&
                nomeRistorante.equals(other.nomeRistorante) &&
                testo.equals(other.testo) &&
                stelle == other.stelle &&
                data.equals(other.data);
    }

    @Override
    public int hashCode() {
        return (autore + nomeRistorante + testo + stelle + data).hashCode();
    }

}