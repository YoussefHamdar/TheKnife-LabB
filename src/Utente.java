/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */
package theknife;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.io.Serializable;
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
/**
 * Rappresenta un utente registrato nell'app.
 * Contiene nome, username, password cifrata e preferiti.
 */


    private String nome;
    private String username;
    private String passwordCifrata;
    private List<Ristorante> preferiti;
    private boolean isRistoratore;
    private String cognome;
    private String domicilio;
    private LocalDate dataDiNascita;

    /**
     * Costruttore completo per creare un utente.
     *
     * @param nome           nome dell'utente
     * @param cognome        cognome dell'utente
     * @param username       identificativo utente
     * @param password       password cifrata
     * @param isRistoratore  true se utente è ristoratore
     * @param domicilio      indirizzo o città dell'utente
     * @param dataDiNascita  data di nascita (facoltativa)
     */

    public Utente(String nome, String cognome, String username, String password, boolean isRistoratore, String domicilio, LocalDate dataDiNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordCifrata = password;
        this.isRistoratore = isRistoratore;
        this.domicilio = domicilio;
        this.dataDiNascita = dataDiNascita;
        this.preferiti = new ArrayList<>();
    }


    /**
     * Ritorna il nome dell'utente.
     * @return nome
     */
    public String getNome() { return nome; }

    /**
     * Ritorna lo username dell'utente.
     * @return username
     */
    public String getUsername() { return username; }
    /**
     * Ritorna la password cifrata.
     * @return stringa cifrata
     */
    public String getPasswordCifrata() { return passwordCifrata; }

    /**
     * Ritorna la lista dei ristoranti preferiti.
     * @return lista preferiti
     */
    public List<Ristorante> getPreferiti() { return preferiti; }
    /**
     * Verifica se l'utente è un ristoratore.
     * @return true se è ristoratore
     */
    public boolean isRistoratore() {
        return isRistoratore;
    }
    /**
     * Ritorna il cognome dell'utente.
     * @return cognome
     */
    public String getCognome() {
        return cognome;
    }
    /**
     * Ritorna il domicilio dell'utente.
     * @return domicilio
     */
    public String getDomicilio() {
        return domicilio;
    }
    /**
     * Ritorna la data di nascita dell'utente.
     * @return data di nascita
     */
    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }


    /**
     * Aggiunge un ristorante alla lista dei preferiti,
     * solo se non è già presente.
     *
     * @param r ristorante da aggiungere
     */

    public void aggiungiPreferito(Ristorante r) {
        if (!preferiti.contains(r)) {
            preferiti.add(r);
        }
    }
    /**
     * Rimuove un ristorante dai preferiti.
     *
     * @param r ristorante da rimuovere
     */

    public void rimuoviPreferito(Ristorante r) {
        preferiti.remove(r);
    }


    /**
     * Restituisce una descrizione testuale dell'utente,
     * con i dati anagrafici e domicilio.
     *
     * @return stringa formattata
     */

    @Override
    public String toString() {
        return "Utente: " + nome + " " + cognome + " (" + username + "), domicilio: " + domicilio + ", nato il: " + dataDiNascita;
    }

}



