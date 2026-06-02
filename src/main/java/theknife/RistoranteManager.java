/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */
package theknife;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;


/**
 * Gestore dei ristoranti nell'app TheKnife.
 * Permette di caricare, cercare, aggiungere, visualizzare
 * e salvare ristoranti, con vari filtri e operazioni.
 */

public class RistoranteManager {

    private List<Ristorante> ristoranti = new ArrayList<>();

    /**
     * Costruttore: inizializza la lista ristoranti vuota.
     * Il caricamento dei dati avviene esplicitamente da main(),
     * leggendo dal file .dat o dal CSV solo quando necessario.
     */
    public RistoranteManager() {
        this.ristoranti = new ArrayList<>();
    }

    /**
     * Carica i ristoranti da un file CSV.
     *
     * @param pathCSV percorso del file
     * @return lista di ristoranti
     */
    public static List<Ristorante> caricaDaCSV(String pathCSV) {
        List<Ristorante> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(pathCSV))) {
            String linea;
            br.readLine(); // salta intestazione

            while ((linea = br.readLine()) != null) {
                String[] campi = linea.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (campi.length >= 12) {
                    String nome = campi[0].replace("\"", "").trim();
                    String citta = campi[2].replace("\"", "").trim();
                    String campoStelle = campi[11].replace("\"", "").trim();
                    String tipoCucina = campi[4].replace("\"", "").trim();
                    String fasciaPrezzo = campi[3].replace("\"", "").trim(); // esempio: €€, €€€, $$$
                    String servizi = campi[12].toLowerCase(); // oppure campi[13]

                    boolean deliveryDisponibile = servizi.contains("delivery");
                    boolean prenotazioneOnlineDisponibile = servizi.contains("reservation") || servizi.contains("prenotazione");

                    // Prende fasciaPrezzo direttamente dal CSV e assegna prezzoMedio
                    int prezzoMedio;
                    switch (fasciaPrezzo) {
                        case "$": prezzoMedio = 20; break;
                        case "$$": prezzoMedio = 40; break;
                        case "$$$": prezzoMedio = 70; break;
                        case "$$$$": prezzoMedio = 100; break;
                        case "$$$$$": prezzoMedio = 120; break;
                        case "$$$$$$": prezzoMedio = 150; break;
                        default: prezzoMedio = -1; // valore speciale "non riconosciuto"
                    }



                    int stelle = campoStelle.replaceAll("[^0-9]", "").isEmpty() ? 0 :
                            Integer.parseInt(campoStelle.replaceAll("[^0-9]", ""));
                    String location = campi[2].replace("\"", "").trim();
                    String nazione = location.substring(location.lastIndexOf(",") + 1).trim();
                    String indirizzo = campi[1].replace("\"", "").trim(); // Colonna "Address"

                    double longitudine = Double.parseDouble(campi[5]);
                    double latitudine = Double.parseDouble(campi[6]);



                    Ristorante r = new Ristorante(
                            nome, citta, stelle, tipoCucina, fasciaPrezzo, deliveryDisponibile,
                            prenotazioneOnlineDisponibile, prezzoMedio, nazione,
                            indirizzo, latitudine, longitudine,
                            "import_michelin"
                            // questo ristorante è stato importato automaticamente dal dataset” Questi ristoranti non appariranno nel menu dei ristoratori, perché non hanno un gestore reale.
                    );



                    System.out.println(" Caricato: " + r);
                    lista.add(r);
                }


            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Cerca ristoranti in base alla città (case-insensitive).
     *
     * @param citta città da cercare
     * @return lista filtrata
     */
    public List<Ristorante> cercaPerCitta(String citta) {
     List<Ristorante> risultati = new ArrayList<>();
     for (Ristorante r : ristoranti) {
     if (r.getCitta().toLowerCase().contains(citta.toLowerCase().trim())) {
     risultati.add(r);
     }
     }
     return risultati;
     }



    /**
     * Aggiunge un nuovo ristorante alla lista (per ristoratori).
     *
     * @param r ristorante da aggiungere
     */
    public void aggiungiRistorante(Ristorante r) {
        ristoranti.add(r);
    }

    /**
     * Restituisce tutti i ristoranti.
     *
     * @return lista completa
     */
    public List<Ristorante> getTuttiIRistoranti() {
        return ristoranti;
    }
    /**
     * Cerca ristoranti in base al tipo di cucina.
     *
     * @param tipo tipo di cucina (es. Italiana)
     * @return lista filtrata
     */

    public List<Ristorante> cercaPerTipoCucina(String tipo) {
        return ristoranti.stream()
                .filter(r -> r.getTipoCucina().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }
    /**
     * Cerca ristoranti per fascia di prezzo (€, €€, €€€, $$$).
     *
     * @param prezzo fascia scelta
     * @return lista filtrata
     */

    public List<Ristorante> cercaPerFasciaPrezzo(String prezzo) {
        return ristoranti.stream()
                .filter(r -> r.getFasciaPrezzo().equalsIgnoreCase(prezzo))
                .collect(Collectors.toList());
    }

    /**
     * Restituisce i ristoranti con servizio delivery.
     *
     * @return lista filtrata
     */

    public List<Ristorante> cercaConDelivery() {
        return ristoranti.stream()
                .filter(Ristorante::isDeliveryDisponibile)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce i ristoranti con prenotazione online disponibile.
     *
     * @return lista filtrata
     */

    public List<Ristorante> cercaConPrenotazioneOnline() {
        return ristoranti.stream()
                .filter(Ristorante::isPrenotazioneOnlineDisponibile)
                .collect(Collectors.toList());
    }
    /**
     * Filtra i ristoranti con una media recensioni superiore o uguale a una soglia.
     *
     * @param minimo soglia minima
     * @return lista filtrata
     */

    public List<Ristorante> cercaPerMediaStelle(double minimo) {
        return ristoranti.stream()
                .filter(r -> r.getMediaStelle() >= minimo)
                .collect(Collectors.toList());
    }

    /**
     * Ricerca avanzata che combina più criteri.
     *
     * @param citta città da cercare
     * @param tipoCucina tipo cucina desiderata
     * @param prezzoMax prezzo massimo accettato
     * @param requireDelivery true se si vuole delivery
     * @param requirePrenotazioneOnline true se si vuole prenotazione online
     * @param minStelle soglia minima di media recensioni
     * @return lista filtrata
     */

    public List<Ristorante> cercaCombinata(
            String citta,
            String tipoCucina,
            int prezzoMax,
            boolean requireDelivery,
            boolean requirePrenotazioneOnline,
            double minStelle
    ) {
        return ristoranti.stream()
                .filter(r -> r.getCitta().equalsIgnoreCase(citta))
                .filter(r -> r.getTipoCucina().toLowerCase().contains(tipoCucina.toLowerCase()))
                .filter(r -> convertiPrezzo(r.getPrezzoMedio()) <= prezzoMax)

                .filter(r -> !requireDelivery || r.isDeliveryDisponibile())
                .filter(r -> !requirePrenotazioneOnline || r.isPrenotazioneOnlineDisponibile())
                .filter(r -> r.getMediaStelle() >= minStelle)
                .collect(Collectors.toList());
    }

    /**
     * Cerca un ristorante esatto per nome.
     *
     * @param nome nome del ristorante
     * @return ristorante trovato oppure null
     */

    public Ristorante cercaPerNome(String nome) {
        return ristoranti.stream()
                .filter(r -> r.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    /**
     * Stampa tutti i dettagli di un ristorante.
     *
     * @param r ristorante da visualizzare
     */

    public void visualizzaDettagli(Ristorante r) {
        System.out.println(" Nome: " + r.getNome());
        System.out.println(" Città: " + r.getCitta() + ", " + r.getNazione());
        System.out.println(" Indirizzo: " + r.getIndirizzo());
        System.out.printf(" Coordinate: %.5f, %.5f\n", r.getLatitudine(), r.getLongitudine());
        System.out.println(" Prezzo medio: " + r.getPrezzoMedio() + " (" + r.getFasciaPrezzo() + ")");
        System.out.println(" Cucina: " + r.getTipoCucina());
        System.out.println(" Delivery: " + (r.isDeliveryDisponibile() ? " sì" : " no"));
        System.out.println(" Prenotazione online: " + (r.isPrenotazioneOnlineDisponibile() ? " sì" : " no"));
        System.out.println(" Stelle Michelin: " + r.getStelle());
        System.out.printf(" Media recensioni: %.2f stelle\n", r.getMediaStelle());
        System.out.println(" Descrizione: " + (r.getDescrizione() != null ? r.getDescrizione() : "Nessuna"));
    }

    /**
     * Restituisce la lista di ristoranti recensiti da un utente.
     *
     * @param username autore delle recensioni
     * @return lista di ristoranti
     */

    public List<Ristorante> getRecensitiDa(String username) {
        List<Ristorante> recensiti = new ArrayList<>();
        for (Ristorante r : ristoranti) {
            for (Recensione rec : r.getRecensioni()) {
                if (rec.getAutore().equalsIgnoreCase(username)) {
                    recensiti.add(r);
                    break; // evita duplicati
                }
            }
        }
        return recensiti;
    }



    /**
     * Salva tutti i ristoranti su file binario (.dat).
     *
     * @param path percorso file di destinazione
     */


    public void salvaSuFile(String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(ristoranti);
            System.out.println(" Ristoranti salvati su file.");
        } catch (IOException e) {
            System.err.println(" Errore salvataggio ristoranti: " + e.getMessage());
        }
    }

    public List<Ristorante> getRistorantiGestitiDa(String username) {
        return ristoranti.stream()
                .filter(r -> username.equalsIgnoreCase(r.getGestore()))
                .collect(Collectors.toList());
    }




    /**
     * Carica la lista dei ristoranti da file binario (.dat).
     * Se il file non esiste, inizializza la lista come vuota.
     *
     * @param path percorso file
     */

    @SuppressWarnings("unchecked")
    public void caricaDaFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println(" Nessun file ristoranti trovato, lista vuota creata.");
            ristoranti = new ArrayList<>();
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ristoranti = (List<Ristorante>) in.readObject();
            System.out.println(" Ristoranti caricati da file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(" Errore caricamento ristoranti: " + e.getMessage());
            ristoranti = new ArrayList<>();
        }
    }

    public int convertiPrezzo(String simboli) {
        simboli = simboli.trim().replace("â‚¬", "€"); // corregge encoding se serve

        if (simboli.contains("€")) return simboli.length();     // "€€€" → 3
        if (simboli.contains("$")) return simboli.length();     // "$$$$" → 4
        return Integer.MAX_VALUE; // se non riconosciuto
    }





}

