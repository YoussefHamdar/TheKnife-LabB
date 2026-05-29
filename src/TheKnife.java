/**
 * Autori:
 * - Hamdar Youssef (Matricola: 753832) – Sede: Como
 * - Dellatorre Federico (Matricola: 755856) – Sede: Como
 */
package theknife;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.io.File;
import java.util.ArrayList;






/**
 * Classe principale dell'app TheKnife.
 * Gestisce registrazione, login e i menù utente e ristoratore.
 */
public class TheKnife {
    /**
     * Metodo principale di avvio dell'applicazione.
     * Inizializza gestori, carica dati, mostra il menù iniziale.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestioneUtenti gestioneUtenti = new GestioneUtenti();
        gestioneUtenti.caricaDaFile("data/utenti.dat");
        gestioneUtenti.salvaSuFile("data/utenti.dat"); // <-- li salva nel file binario


        RistoranteManager ristoranteManager = new RistoranteManager(); // da CSV

        File fileRisto = new File("data/ristoranti_backup.dat");

        if (fileRisto.exists()) {
            // Carica direttamente il .dat se esiste
            ristoranteManager.caricaDaFile("data/ristoranti_backup.dat");

            // Caso raro: file presente ma vuoto → rigeneriamo da CSV
            if (ristoranteManager.getTuttiIRistoranti().isEmpty()) {
                List<Ristorante> iniziali = RistoranteManager.caricaDaCSV("data/michelin_my_maps.csv");
                for (Ristorante r : iniziali) {
                    ristoranteManager.aggiungiRistorante(r);
                }
                ristoranteManager.salvaSuFile("data/ristoranti_backup.dat");
            }

        } else {
            // Prima esecuzione: creo il .dat dal CSV
            List<Ristorante> iniziali = RistoranteManager.caricaDaCSV("data/michelin_my_maps.csv");
            for (Ristorante r : iniziali) {
                ristoranteManager.aggiungiRistorante(r);
            }
            ristoranteManager.salvaSuFile("data/ristoranti_backup.dat");
        }






        RecensioneManager recensioneManager = new RecensioneManager();
        recensioneManager.caricaDaFile("data/recensioni.dat");

        // Associa le recensioni ai ristoranti caricati
        recensioneManager.associaRecensioni(ristoranteManager.getTuttiIRistoranti());
        // Rimuove recensioni duplicate dal file e dalla memoria
        recensioneManager.rimuoviDuplicati();
        // Rimuove duplicati anche dentro ogni ristorante
        for (Ristorante r : ristoranteManager.getTuttiIRistoranti()) {
            r.rimuoviRecensioniDuplicate();
        }
        // Salva i ristoranti aggiornati
        ristoranteManager.salvaSuFile("data/ristoranti_backup.dat");
        // Feedback visivo
        System.out.println(" Recensioni duplicate eliminate e ristoranti aggiornati.");




        Utente utenteLoggato = null;
        boolean esci = false;

        while (!esci) {

            System.out.println("\n BENVENUTO SU THEKNIFE ");
            System.out.println("1. Registrati");
            System.out.println("2. Login");
            System.out.println("3. Accedi come ospite");
            System.out.println("4. Esci");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();

                    System.out.print("Cognome: ");
                    String cognome = scanner.nextLine();

                    System.out.print("Domicilio: ");
                    String domicilio = scanner.nextLine();

                    System.out.print("Data di nascita (aaaa-mm-gg): ");
                    String dataStr = scanner.nextLine();
                    LocalDate dataDiNascita = null;
                    try {
                        dataDiNascita = LocalDate.parse(dataStr);
                    } catch (Exception e) {
                        System.out.println("Formato data non valido. Continuiamo senza.");
                    }

                    String username = null;
                    while (username == null) {
                        System.out.print("Username: ");
                        String input = scanner.nextLine().trim();

                        if (gestioneUtenti.usernameDisponibile(input)) {
                            username = input;
                        } else {
                            System.out.println(" Username non disponibile. Scegli un altro.");
                        }
                    }


                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    System.out.println("Sei un:\n1. Utente normale\n2. Ristoratore");
                    System.out.print("Scelta: ");
                    String sceltaRuolo = scanner.nextLine();
                    boolean isRistoratore = sceltaRuolo.equals("2");

                    boolean ok = gestioneUtenti.registraUtente(
                            nome, cognome, username, password,
                            isRistoratore, domicilio, dataDiNascita



                    );
                    gestioneUtenti.salvaSuFile("data/utenti.dat");

                    break;


                case "2":
                    System.out.print("Username: ");
                    String user = scanner.nextLine();
                    System.out.print("Password: ");
                    String pass = scanner.nextLine();

                    Utente u = gestioneUtenti.login(user, pass);
                    if (u != null) {
                        utenteLoggato = u;
                        System.out.println("Login riuscito. Ciao, " + u.getNome() + "!");
                        if (u != null) {
                            System.out.println("Login riuscito. Ciao, " + u.getNome() + "!");

                            if (u.isRistoratore()) {
                                menuRistoratore(u, scanner, recensioneManager, ristoranteManager, gestioneUtenti);
                            } else {
                                menuUtente(u, scanner, recensioneManager, ristoranteManager, gestioneUtenti);

                            }
                        }

                    } else {
                        System.out.println("Credenziali errate.");
                    }
                    break;
                case "3":
                    menuGuest(scanner, ristoranteManager, recensioneManager);
                    break;


                case "4":
                    gestioneUtenti.salvaSuFile("data/utenti.dat");
                    esci = true;
                    System.out.println("Grazie per aver usato TheKnife!");
                    break;

                default:
                    System.out.println("Scelta non valida.");
            }
        }

        scanner.close();
    }





    /**
     * Menù interattivo per utenti ospiti (non registrati).
     * Permette ricerche su ristoranti e visualizzazione recensioni pubbliche.
     *
     * @param scanner Scanner per input utente
     * @param ristoranteManager Gestore dei ristoranti
     * @param recensioneManager Gestore delle recensioni
     */


    public static void menuGuest(Scanner scanner, RistoranteManager ristoranteManager, RecensioneManager recensioneManager) {
        boolean esci = false;

        while (!esci) {
            System.out.println("\n Menu ospite");
            System.out.println("1. Cerca ristorante per nome");
            System.out.println("2. Visualizza recensioni di un ristorante");
            System.out.println("3. Filtra per città");
            System.out.println("4. Filtra per tipo di cucina");
            System.out.println("5. Filtra per fascia di prezzo");
            System.out.println("6. Solo con delivery");
            System.out.println("7. Solo con prenotazione online");
            System.out.println("8. Filtra per stelle minime");
            System.out.println("9. Ricerca combinata");
            System.out.println("10. Esci");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    System.out.print(" Inserisci nome ristorante: ");
                    String nome = scanner.nextLine();
                    Ristorante r = ristoranteManager.cercaPerNome(nome);
                    if (r != null) {
                        ristoranteManager.visualizzaDettagli(r);
                    } else {
                        System.out.println(" Ristorante non trovato.");
                    }
                    break;

                case "2":
                    System.out.print(" Inserisci nome ristorante: ");
                    String nomeRec = scanner.nextLine();
                    Ristorante risto = ristoranteManager.cercaPerNome(nomeRec);
                    if (risto != null) {
                        List<Recensione> recs = risto.getRecensioni();
                        if (recs.isEmpty()) {
                            System.out.println(" Nessuna recensione disponibile.");
                        } else {
                            System.out.println(" Recensioni anonime:");
                            for (Recensione rec : recs) {
                                System.out.printf("⭐ %.1f ★ — \"%s\"\n", (float) rec.getStelle(), rec.getTesto());
                                System.out.println("--------------------------------------------------");
                            }
                        }
                    } else {
                        System.out.println(" Ristorante non trovato.");
                    }
                    break;


                case "3":
                    System.out.print(" Inserisci città: ");
                    String citta = scanner.nextLine();
                    stampaLista(ristoranteManager.cercaPerCitta(citta));
                    break;

                case "4":
                    System.out.print("Inserisci tipo cucina: ");
                    String tipo = scanner.nextLine();
                    stampaLista(ristoranteManager.cercaPerTipoCucina(tipo));
                    break;

                case "5": {
                    String fascia = "";
                    while (true) {
                        System.out.println("Scegli fascia di prezzo:");
                        System.out.println("1 = $");
                        System.out.println("2 = $$");
                        System.out.println("3 = $$$");
                        System.out.println("4 = $$$$");
                        System.out.println("5 = $$$$$");
                        System.out.println("6 = $$$$$$");
                        System.out.print("Scelta (1–6): ");

                        String sceltaPrezzo = scanner.nextLine().trim();

                        switch (sceltaPrezzo) {
                            case "1": fascia = "$"; break;
                            case "2": fascia = "$$"; break;
                            case "3": fascia = "$$$"; break;
                            case "4": fascia = "$$$$"; break;
                            case "5": fascia = "$$$$$"; break;
                            case "6": fascia = "$$$$$$"; break;
                            default:
                                System.out.println(" Scelta non valida. Inserisci un numero da 1 a 6.");
                                continue; // torna a chiedere
                        }
                        break; // esce dal ciclo se la scelta è valida
                    }

                    List<Ristorante> filtrati = ristoranteManager.cercaPerFasciaPrezzo(fascia.trim());
                    if (filtrati.isEmpty()) {
                        System.out.println(" Nessun ristorante trovato con fascia '" + fascia + "'");
                    } else {
                        System.out.println(" Ristoranti trovati:");
                        stampaLista(filtrati);
                    }
                    break;
                }



                case "6":
                    stampaLista(ristoranteManager.cercaConDelivery());
                    break;

                case "7":
                    stampaLista(ristoranteManager.cercaConPrenotazioneOnline());
                    break;

                case "8":
                    System.out.print(" Inserisci numero minimo di stelle (es. 3.5): ");

                    double minStelle = 0; // PROTEZIONE INPUT
                    try {
                        minStelle = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(" Valore non valido, imposto 0.");
                        minStelle = 0;
                    }

                    stampaLista(ristoranteManager.cercaPerMediaStelle(minStelle));
                    break;

                case "9":
                    System.out.print("Città: ");
                     citta = scanner.nextLine();

                    System.out.print("Tipo cucina: ");
                     tipo = scanner.nextLine();

                    // Prezzo massimo con protezione
                    int prezzoMax = -1;
                    while (prezzoMax == -1) {
                        System.out.println("Prezzo massimo (scegli livello):");
                        System.out.println("1 = $");
                        System.out.println("2 = $$");
                        System.out.println("3 = $$$");
                        System.out.println("4 = $$$$");
                        System.out.println("5 = $$$$$");
                        System.out.println("6 = $$$$$$");
                        System.out.print("Scelta (1–6): ");

                        String input = scanner.nextLine().trim();
                        try {
                             int livelloPrezzo = Integer.parseInt(input);
                            if (livelloPrezzo >= 1 && livelloPrezzo <= 6) {
                                prezzoMax = livelloPrezzo;
                            } else {
                                System.out.println(" Valore fuori intervallo. Riprova.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(" Input non valido. Inserisci un numero da 1 a 6.");
                        }
                    }

                    // Delivery con protezione
                    Boolean delivery = null;
                    while (delivery == null) {
                        System.out.print("Richiedi delivery? (sì/no): ");
                        String risposta = scanner.nextLine().trim().toLowerCase();
                        if (risposta.equals("sì") || risposta.equals("si")) delivery = true;
                        else if (risposta.equals("no")) delivery = false;
                        else System.out.println(" Risposta non valida. Scrivi 'sì' o 'no'.");
                    }


                    // Prenotazione online con protezione
                    Boolean prenotazione = null;
                    while (prenotazione == null) {
                        System.out.print("Richiedi prenotazione online? (sì/no): ");
                        String risposta = scanner.nextLine().trim().toLowerCase();
                        if (risposta.equals("sì") || risposta.equals("si")) prenotazione = true;
                        else if (risposta.equals("no")) prenotazione = false;
                        else System.out.println(" Risposta non valida. Scrivi 'sì' o 'no'.");
                    }


                    // Media stelle minima con protezione
                    double minMediaStelle = -1;
                    while (minMediaStelle < 0) {
                        System.out.print("Media stelle minima: ");
                        String input = scanner.nextLine().trim();
                        try {
                            minMediaStelle = Double.parseDouble(input);
                            if (minMediaStelle < 0 || minMediaStelle > 5) {
                                System.out.println(" Valore fuori intervallo. Inserisci un numero tra 0 e 5.");
                                minMediaStelle = -1;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(" Input non valido. Inserisci un numero decimale.");
                        }
                    }

                    // Ricerca combinata
                    List<Ristorante> ristorantiFiltrati = ristoranteManager.cercaCombinata(
                            citta, tipo, prezzoMax, delivery, prenotazione, minMediaStelle
                    );

                    if (ristorantiFiltrati.isEmpty()) {
                        System.out.println(" Nessun ristorante trovato con i criteri specificati.");
                    } else {
                        System.out.println(" Ristoranti trovati:");
                        for (Ristorante ristoranteTrovato : ristorantiFiltrati) {
                            System.out.printf("- %s (%s, %.1f stelle)\n",
                                    ristoranteTrovato.getNome(),
                                    ristoranteTrovato.getFasciaPrezzo(),
                                    ristoranteTrovato.getMediaStelle());
                        }
                    }
                    break;


                case "10":
                    esci = true;
                    System.out.println(" Uscita dal menu ospite.");
                    break;

                default:
                    System.out.println(" Scelta non valida.");
            }
        }
    }


    /**
     * Menù interattivo per utenti registrati non ristoratori.
     * Include ricerche, gestione recensioni e preferiti, e logout.
     *
     * @param utente Utente loggato
     * @param scanner Scanner per input
     * @param recensioneManager Gestore delle recensioni
     * @param ristoranteManager Gestore dei ristoranti
     * @param gestioneUtenti Gestore degli utenti (usato per salvataggio al logout)
     */

    public static void menuUtente(Utente utente, Scanner scanner, RecensioneManager recensioneManager, RistoranteManager ristoranteManager, GestioneUtenti gestioneUtenti) {
        boolean esci = false;

        while (!esci) {
            System.out.println("\n Menù utente (" + utente.getUsername() + ")");
            System.out.println("1. Cerca ristorante per nome");
            System.out.println("2. Cerca ristoranti per città");
            System.out.println("3. Cerca ristoranti per tipo cucina");
            System.out.println("4. Cerca per fascia di prezzo");
            System.out.println("5. Cerca ristoranti con servizio delivery");
            System.out.println("6. Cerca ristoranti con prenotazione online");
            System.out.println("7. Cerca ristoranti con media stelle");
            System.out.println("8. Ricerca avanzata (combinata)");
            System.out.println("9. Visualizza dettagli ristorante");
            System.out.println("10. Aggiungi recensione");
            System.out.println("11. Visualizza recensioni");
            System.out.println("12. Gestisci preferiti");
            System.out.println("13. Modifica una tua recensione");
            System.out.println("14. Cancella una tua recensione");
            System.out.println("15. Visualizza ristoranti che hai recensito");
            System.out.println("16. Logout");

            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {

                case "1":
                    System.out.print("Inserisci il nome del ristorante: ");
                    String nomeDaCercare = scanner.nextLine();

                    Ristorante rNome = ristoranteManager.cercaPerNome(nomeDaCercare);

                    if (rNome != null) {
                        ristoranteManager.visualizzaDettagli(rNome);
                    } else {
                        System.out.println("Nessun ristorante trovato con quel nome.");
                    }
                    break;

                case "2":
                    System.out.print("Inserisci città: ");
                    String cittaInput = scanner.nextLine().trim(); // elimina spazi extra

                    List<Ristorante> trovati = ristoranteManager.cercaPerCitta(cittaInput);

                    if (trovati.isEmpty()) {
                        System.out.println("Nessun ristorante trovato per '" + cittaInput + "'");
                    } else {
                        System.out.println("Ristoranti trovati:");
                        for (Ristorante r : trovati) {
                            System.out.println("- " + r);
                        }
                    }

                    System.out.println(); // spazio per tornare al menù
                    break;
                case "3":
                    System.out.print("Inserisci tipo di cucina: ");
                    String tipoCucinaInput = scanner.nextLine().trim();
                    List<Ristorante> risultati = ristoranteManager.cercaPerTipoCucina(tipoCucinaInput);

                    if (risultati.isEmpty()) {
                        System.out.println(" Nessun ristorante trovato.");
                    } else {
                        System.out.println(" Ristoranti trovati:");
                        for (Ristorante r : risultati) {
                            System.out.println("- " + r);
                        }
                    }
                    break;
                case "4": {
                    String fascia = "";
                    while (true) {
                        System.out.println("Scegli fascia di prezzo:");
                        System.out.println("1 = $");
                        System.out.println("2 = $$");
                        System.out.println("3 = $$$");
                        System.out.println("4 = $$$$");
                        System.out.println("5 = $$$$$");
                        System.out.println("6 = $$$$$$");
                        System.out.print("Scelta (1–6): ");

                        String sceltaPrezzo = scanner.nextLine().trim();

                        switch (sceltaPrezzo) {
                            case "1": fascia = "$"; break;
                            case "2": fascia = "$$"; break;
                            case "3": fascia = "$$$"; break;
                            case "4": fascia = "$$$$"; break;
                            case "5": fascia = "$$$$$"; break;
                            case "6": fascia = "$$$$$$"; break;
                            default:
                                System.out.println("️ Scelta non valida. Inserisci un numero da 1 a 6.");
                                continue; // ripete la domanda finché non è valida
                        }
                        break; // esce dal while solo se la scelta è valida
                    }

                    List<Ristorante> filtrati = ristoranteManager.cercaPerFasciaPrezzo(fascia.trim());
                    if (filtrati.isEmpty()) {
                        System.out.println(" Nessun ristorante trovato con fascia '" + fascia + "'");
                    } else {
                        System.out.println(" Ristoranti trovati:");
                        for (Ristorante r : filtrati) {
                            System.out.println("- " + r);
                        }
                    }
                    break;
                }



                case "5":
                    List<Ristorante> conDelivery = ristoranteManager.cercaConDelivery();

                    if (conDelivery.isEmpty()) {
                        System.out.println(" Nessun ristorante con delivery trovato.");
                    } else {
                        System.out.println(" Ristoranti che offrono delivery:");
                        for (Ristorante r : conDelivery) {
                            System.out.println("- " + r);
                        }
                    }
                    break;

                case "6":
                    List<Ristorante> conPrenotazione = ristoranteManager.cercaConPrenotazioneOnline();

                    if (conPrenotazione.isEmpty()) {
                        System.out.println(" Nessun ristorante offre prenotazione online.");
                    } else {
                        System.out.println(" Ristoranti con prenotazione online:");
                        for (Ristorante r : conPrenotazione) {
                            System.out.println("- " + r);
                        }
                    }
                    break;

                case "7": {
                    double minStelle = -1;

                    while (minStelle < 0 || minStelle > 5) {
                        System.out.print("Inserisci minimo stelle (da 0 a 5): ");
                        String input = scanner.nextLine().trim();

                        try {
                            minStelle = Double.parseDouble(input);
                            if (minStelle < 0 || minStelle > 5) {
                                System.out.println(" Valore fuori intervallo. Inserisci un numero tra 0 e 5.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(" Input non valido. Inserisci un numero (es. 3.5).");
                        }
                    }

                    List<Ristorante> filtratiMedia = ristoranteManager.cercaPerMediaStelle(minStelle);

                    if (filtratiMedia.isEmpty()) {
                        System.out.println(" Nessun ristorante con media ≥ " + minStelle);
                    } else {
                        System.out.println(" Ristoranti con media stelle:");
                        for (Ristorante r : filtratiMedia) {
                            System.out.printf("- %s (Media: %.2f)\n", r.getNome(), r.getMediaStelle());
                        }
                    }
                    break;
                }


                case "8":
                    System.out.print("Città: ");
                    String citta = scanner.nextLine().trim();

                    System.out.print("Tipo cucina: ");
                    String tipo = scanner.nextLine().trim();

                    // Prezzo massimo con protezione
                    int prezzoMax = -1;
                    while (prezzoMax == -1) {
                        System.out.println("Prezzo massimo (scegli livello):");
                        System.out.println("1 = $");
                        System.out.println("2 = $$");
                        System.out.println("3 = $$$");
                        System.out.println("4 = $$$$");
                        System.out.println("5 = $$$$$");
                        System.out.println("6 = $$$$$$");
                        System.out.print("Scelta (1–6): ");

                        String inputPrezzo = scanner.nextLine().trim();
                        try {
                            int sceltaPrezzo = Integer.parseInt(inputPrezzo);
                            if (sceltaPrezzo >= 1 && sceltaPrezzo <= 6) {
                                prezzoMax = sceltaPrezzo;
                            } else {
                                System.out.println(" Valore fuori intervallo. Riprova.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(" Input non valido. Inserisci un numero da 1 a 6.");
                        }
                    }

                    // Delivery con protezione
                    Boolean delivery = null;
                    while (delivery == null) {
                        System.out.print("Richiedi delivery? (sì/no): ");
                        String risposta = scanner.nextLine().trim().toLowerCase();
                        if (risposta.equals("sì") || risposta.equals("si")) delivery = true;
                        else if (risposta.equals("no")) delivery = false;
                        else System.out.println(" Risposta non valida. Scrivi 'sì' o 'no'.");
                    }

                    // Prenotazione online con protezione
                    Boolean prenotazione = null;
                    while (prenotazione == null) {
                        System.out.print("Richiedi prenotazione online? (sì/no): ");
                        String risposta = scanner.nextLine().trim().toLowerCase();
                        if (risposta.equals("sì") || risposta.equals("si")) prenotazione = true;
                        else if (risposta.equals("no")) prenotazione = false;
                        else System.out.println(" Risposta non valida. Scrivi 'sì' o 'no'.");
                    }


                    // Media stelle minima con protezione
                    double minMediaStelle = -1;
                    while (minMediaStelle < 0) {
                        System.out.print("Media stelle minima (0–5): ");
                        String inputStelle = scanner.nextLine().trim();
                        try {
                            double valore = Double.parseDouble(inputStelle);
                            if (valore >= 0 && valore <= 5) {
                                minMediaStelle = valore;
                            } else {
                                System.out.println(" Valore fuori intervallo. Inserisci un numero tra 0 e 5.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(" Input non valido. Inserisci un numero decimale.");
                        }
                    }

                    // Ricerca combinata
                    List<Ristorante> ristorantiFiltrati = ristoranteManager.cercaCombinata(
                            citta, tipo, prezzoMax, delivery, prenotazione, minMediaStelle
                    );

                    if (ristorantiFiltrati.isEmpty()) {
                        System.out.println(" Nessun ristorante trovato con i criteri specificati.");
                    } else {
                        System.out.println(" Ristoranti trovati:");
                        for (Ristorante r : ristorantiFiltrati) {
                            System.out.printf("- %s (%s, %.1f stelle)\n",
                                    r.getNome(),
                                    r.getFasciaPrezzo(),
                                    r.getMediaStelle());
                        }
                    }
                    break;

                case "9":
                    System.out.print("Inserisci nome ristorante: ");
                    String nome = scanner.nextLine();

                    Ristorante r = ristoranteManager.cercaPerNome(nome);

                    if (r != null) {
                        ristoranteManager.visualizzaDettagli(r);
                    } else {
                        System.out.println(" Ristorante non trovato.");
                    }
                    break;




                case "10": {
                    System.out.print("Inserisci il nome esatto del ristorante da recensire: ");
                    String nomeRistorante = scanner.nextLine().trim();

                    Ristorante selezionato = ristoranteManager.cercaPerNome(nomeRistorante);
                    if (selezionato == null) {
                        System.out.println("️ Nessun ristorante trovato con quel nome.");
                        break;
                    }

                    System.out.println("Hai scelto: " + selezionato.getNome() + " (" + selezionato.getCitta() + ")");
                    System.out.print("Scrivi la tua recensione: ");
                    String testo = scanner.nextLine();

                    System.out.print("Quante stelle (1–5): ");
                    int stelle;
                    try {
                        stelle = Integer.parseInt(scanner.nextLine());
                        if (stelle < 1 || stelle > 5) {
                            System.out.println("️ Valore fuori intervallo. Uso 3.");
                            stelle = 3;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(" Valore non valido. Uso 3.");
                        stelle = 3;
                    }

                    //  Qui passo SOLO 4 argomenti, la data la mette già il manager
                    recensioneManager.aggiungiRecensione(
                            utente.getUsername(),
                            selezionato.getNome(),
                            testo,
                            stelle
                    );
                    recensioneManager.salvaSuFile("data/recensioni.dat");


                    System.out.println(" Recensione salvata per " + selezionato.getNome());
                    break;
                }


                case "11": {
                    System.out.print("Inserisci il nome del ristorante per vedere le recensioni: ");
                    String nomeRistorante = scanner.nextLine().trim();

                    // Recupera tutte le recensioni
                    List<Recensione> tutte = recensioneManager.getTutteLeRecensioni();

                    // Filtra solo quelle del ristorante scelto
                    List<Recensione> filtrate = new ArrayList<>();
                    for (Recensione rec : tutte) {
                        if (rec.getNomeRistorante().equalsIgnoreCase(nomeRistorante)) {
                            filtrate.add(rec);
                        }
                    }

                    if (filtrate.isEmpty()) {
                        System.out.println(" Nessuna recensione trovata per il ristorante '" + nomeRistorante + "'");
                    } else {
                        System.out.println(" Recensioni per '" + nomeRistorante + "':\n");
                        for (Recensione rec : filtrate) {
                            System.out.println(rec);
                            System.out.println("--------------------------------------------------");
                        }
                    }
                    break;
                }



                case "12":
                    boolean esciPreferiti = false;
                    while (!esciPreferiti) {
                        System.out.println("\n Gestione preferiti");
                        System.out.println("1. Aggiungi ristorante dai disponibili");
                        System.out.println("2. Rimuovi dai preferiti");
                        System.out.println("3. Visualizza preferiti");
                        System.out.println("4. Torna al menù utente");
                        System.out.print("Scelta: ");
                        String sottoScelta = scanner.nextLine();

                        switch (sottoScelta) {
                            case "1": {
                                System.out.print("Inserisci il nome del ristorante da aggiungere ai preferiti: ");
                                String nomeDaAggiungere = scanner.nextLine().trim();

                                Ristorante ristorantePreferito = ristoranteManager.cercaPerNome(nomeDaAggiungere);
                                if (ristorantePreferito == null) {
                                    System.out.println(" Nessun ristorante trovato con quel nome.");
                                } else {
                                    if (utente.getPreferiti().contains(ristorantePreferito)) {
                                        System.out.println(" Il ristorante è già nei tuoi preferiti.");
                                    } else {
                                        utente.aggiungiPreferito(ristorantePreferito);
                                        System.out.println(" Ristorante aggiunto ai preferiti.");
                                    }
                                }
                                break;
                            }



                            case "2": {
                                List<Ristorante> pref = utente.getPreferiti();
                                for (int i = 0; i < pref.size(); i++) {
                                    System.out.println(i + ". " + pref.get(i));
                                }
                                System.out.print("Numero da rimuovere: ");
                                // PROTEZIONE INPUT indice rimozione preferito
                                int indexRimuovi = -1;
                                try {
                                    indexRimuovi = Integer.parseInt(scanner.nextLine());
                                } catch (NumberFormatException e) {
                                    System.out.println(" Valore non valido.");
                                }

                                if (indexRimuovi >= 0 && indexRimuovi < pref.size()) {
                                    utente.rimuoviPreferito(pref.get(indexRimuovi));
                                    System.out.println("Ristorante rimosso dai preferiti.");
                                }
                                break;
                            }
                            case "3": {
                                System.out.println("I tuoi ristoranti preferiti:");
                                for (Ristorante preferito : utente.getPreferiti()) {
                                    System.out.println("- " + preferito);
                                }
                                break;
                            }
                            case "4": {
                                esciPreferiti = true;
                                break;
                            }
                            default:
                                System.out.println("Scelta non valida.");
                        }
                    }
                    break;



                        case "13": {
                            List<Recensione> mie = getRecensioniUtente(utente, recensioneManager);

                            if (mie.isEmpty()) {
                                System.out.println("Non hai ancora scritto recensioni.");
                                break;
                            }

                            System.out.println("Le tue recensioni:");
                            for (int i = 0; i < mie.size(); i++) {
                                System.out.println(i + ". " + mie.get(i));
                            }

                            System.out.print("Numero recensione da modificare: ");
                            try {
                                int sceltaModifica = Integer.parseInt(scanner.nextLine());

                                if (sceltaModifica >= 0 && sceltaModifica < mie.size()) {
                                    Recensione daModificare = mie.get(sceltaModifica);
                                    System.out.println("Recensione attuale:\n" + daModificare);

                                    System.out.print("Nuovo testo: ");
                                    String nuovoTesto = scanner.nextLine();

                                    System.out.print("Nuove stelle (1–5): ");
                                    int nuoveStelle = Integer.parseInt(scanner.nextLine());

                                    if (nuoveStelle >= 1 && nuoveStelle <= 5) {
                                        recensioneManager.modificaRecensione(daModificare, nuovoTesto, nuoveStelle);
                                        recensioneManager.salvaSuFile("data/recensioni.dat");

                                        System.out.println(" Recensione aggiornata.");
                                    } else {
                                        System.out.println(" Numero di stelle non valido.");
                                    }
                                } else {
                                    System.out.println(" Scelta non valida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(" Inserisci un numero valido.");
                            }
                            break;
                        }

                        case "14": {
                            List<Recensione> mie = getRecensioniUtente(utente, recensioneManager);

                            if (mie.isEmpty()) {
                                System.out.println("Non hai ancora scritto recensioni.");
                                break;
                            }

                            System.out.println("Le tue recensioni:");
                            for (int i = 0; i < mie.size(); i++) {
                                System.out.println(i + ". " + mie.get(i));
                            }

                            System.out.print("Numero recensione da eliminare: ");
                            try {
                                int sceltaElimina = Integer.parseInt(scanner.nextLine());

                                if (sceltaElimina >= 0 && sceltaElimina < mie.size()) {
                                    System.out.println("Recensione eliminata:\n" + mie.get(sceltaElimina));
                                    recensioneManager.rimuoviRecensione(mie.get(sceltaElimina));
                                    recensioneManager.salvaSuFile("data/recensioni.dat");

                                    System.out.println(" Recensione rimossa con successo.");
                                } else {
                                    System.out.println(" Scelta non valida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(" Inserisci un numero valido.");
                            }
                            break;
                        }

                        case "15":
                            List<Ristorante> recensiti = ristoranteManager.getRecensitiDa(utente.getUsername());
                            if (recensiti.isEmpty()) {
                                System.out.println(" Non hai ancora recensito alcun ristorante.");
                            } else {
                                System.out.println(" Ristoranti che hai recensito:");
                                for (Ristorante r2 : recensiti) {
                                    System.out.println("- " + r2.getNome() + " (" + r2.getCitta() + ")");
                                }
                            }
                            break;
                        case "16":
                            gestioneUtenti.salvaSuFile("data/utenti.dat");

                            esci = true;
                            System.out.println("Logout effettuato.");
                            break;
                        default:
                            System.out.println("Scelta non valida.");
                            break;


            }
        }
    }
    private static List<Recensione> getRecensioniUtente(Utente utente, RecensioneManager manager) {
        return manager.getTutteLeRecensioni().stream()
                .filter(r -> r.getAutore().equals(utente.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * Menù interattivo per utenti con ruolo ristoratore.
     * Permette di visualizzare e rispondere alle recensioni,
     * aggiungere un nuovo ristorante e salvare i dati al logout.
     *
     * @param ristoratore utente ristoratore loggato
     * @param scanner scanner per input
     * @param recensioneManager gestore delle recensioni
     * @param ristoranteManager gestore dei ristoranti
     * @param gestioneUtenti gestore utenti (per salvataggio al logout)
     */

    public static void menuRistoratore(Utente ristoratore, Scanner scanner, RecensioneManager recensioneManager, RistoranteManager ristoranteManager, GestioneUtenti gestioneUtenti)

    {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n Menù ristoratore (" + ristoratore.getUsername() + ")");
            System.out.println("1. Visualizza i miei ristoranti con dettagli");
            System.out.println("2. Visualizza recensioni");
            System.out.println("3. Rispondi a una recensione");
            System.out.println("4. Aggiungi ristorante");
            System.out.println("5. Visualizza valutazione media e numero di recensioni");
            System.out.println("6. Logout");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    List<Ristorante> mieiRistoranti = ristoranteManager.getRistorantiGestitiDa(ristoratore.getUsername());
                    if (mieiRistoranti.isEmpty()) {
                        System.out.println(" Non gestisci ancora nessun ristorante.");
                    } else {
                        for (Ristorante r : mieiRistoranti) {
                            System.out.println("─────────────────────────────");
                            System.out.println("  Nome: " + r.getNome());
                            System.out.println("  Città: " + r.getCitta());
                            System.out.println(" Stelle: " + r.getStelle());
                            System.out.println(" Tipo cucina: " + r.getTipoCucina());
                            System.out.println(" Fascia prezzo: " + r.getFasciaPrezzo() + " (Prezzo medio: " + r.getPrezzoMedio() + "€)");
                            System.out.println(" Delivery: " + (r.isDeliveryDisponibile() ? "Sì" : "No"));
                            System.out.println(" Prenotazione online: " + (r.isPrenotazioneOnlineDisponibile() ? "Sì" : "No"));
                            System.out.println("Nazione: " + r.getNazione());
                            System.out.println(" Indirizzo: " + r.getIndirizzo());
                            System.out.println(" Coordinate: " + r.getLatitudine() + ", " + r.getLongitudine());
                            System.out.println("─────────────────────────────\n");
                        }
                    }
                    break;

                case "2":
                    List<Ristorante> gestiti = ristoranteManager.getRistorantiGestitiDa(ristoratore.getUsername());

                    if (gestiti.isEmpty()) {
                        System.out.println("Non gestisci ancora alcun ristorante.");
                    } else {
                        for (Ristorante r : gestiti) {
                            System.out.println(" Ristorante: " + r.getNome());
                            System.out.printf(" Media stelle: %.2f\n", r.getMediaStelle());
                            System.out.println(" Numero recensioni: " + r.getRecensioni().size());
                            System.out.println("-----------------------------------");

                            List<Recensione> recs = r.getRecensioni();
                            if (recs.isEmpty()) {
                                System.out.println(" Nessuna recensione disponibile.\n");
                            } else {
                                for (Recensione rec : recs) {
                                    System.out.printf("- %s (%d) [%s]\n", rec.getAutore(), rec.getStelle(), rec.getData());
                                    System.out.println("  \"" + rec.getTesto() + "\"");
                                    if (rec.getRispostaDelRistoratore() != null) {
                                        System.out.println("  Risposta: " + rec.getRispostaDelRistoratore());
                                    }
                                    System.out.println();
                                }
                            }
                            System.out.println();
                        }
                    }

                    break;

                case "3": {
                    // Recupera tutte le recensioni dal manager
                    List<Recensione> tutte = recensioneManager.getTutteLeRecensioni();

                    // Filtra solo quelle dei ristoranti di cui sei gestore
                    List<Recensione> mie = new ArrayList<>();
                    for (Recensione rec : tutte) {
                        Ristorante r = ristoranteManager.cercaPerNome(rec.getNomeRistorante());
                        if (r != null && r.getGestore().equalsIgnoreCase(ristoratore.getUsername())) {
                            mie.add(rec);
                        }
                    }

                    if (mie.isEmpty()) {
                        System.out.println(" Nessuna recensione disponibile a cui rispondere.");
                        break;
                    }

                    // Mostra le recensioni filtrate
                    for (int i = 0; i < mie.size(); i++) {
                        System.out.println(i + ". " + mie.get(i));
                    }

                    System.out.print("Numero della recensione: ");
                    int index = -1;
                    try {
                        index = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println(" Numero non valido.");
                    }

                    if (index >= 0 && index < mie.size()) {
                        System.out.print("Scrivi la risposta: ");
                        String risposta = scanner.nextLine();
                        mie.get(index).setRispostaDelRistoratore(risposta);
                        System.out.println("Risposta salvata.");

                        // 🔹 Salva subito su file così la risposta resta memorizzata
                        recensioneManager.salvaSuFile("data/recensioni.dat");
                    } else {
                        System.out.println(" Numero non valido.");
                    }
                    break;
                }



                case "4":
                    System.out.print("Nome ristorante: ");
                    String nome = scanner.nextLine();

                    System.out.print("Città: ");
                    String citta = scanner.nextLine();

                    // Stelle protette
                    int stelle = 0;
                    while (true) {
                        System.out.print("Numero stelle (1–5): ");
                        try {
                            stelle = Integer.parseInt(scanner.nextLine());
                            if (stelle >= 1 && stelle <= 5) {
                                break;
                            } else {
                                System.out.println(" Inserisci un numero da 1 a 5.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(" Valore non valido. Riprova.");
                        }
                    }

                    System.out.print("Tipo cucina (es. Giapponese, Italiana, Messicana): ");
                    String tipoCucina = scanner.nextLine();

                    // Fascia di prezzo protetta
                    String fasciaPrezzo = "";
                    int prezzoMedio = 50;
                    while (true) {
                        System.out.println("Scegli fascia di prezzo:");
                        System.out.println("1 = $");
                        System.out.println("2 = $$");
                        System.out.println("3 = $$$");
                        System.out.println("4 = $$$$");
                        System.out.println("5 = $$$$$");
                        System.out.println("6 = $$$$$$");
                        System.out.print("Scelta (1–6): ");
                        String sceltaPrezzo = scanner.nextLine().trim();

                        switch (sceltaPrezzo) {
                            case "1": fasciaPrezzo = "$"; prezzoMedio = 20; break;
                            case "2": fasciaPrezzo = "$$"; prezzoMedio = 40; break;
                            case "3": fasciaPrezzo = "$$$"; prezzoMedio = 70; break;
                            case "4": fasciaPrezzo = "$$$$"; prezzoMedio = 100; break;
                            case "5": fasciaPrezzo = "$$$$$"; prezzoMedio = 120; break;
                            case "6": fasciaPrezzo = "$$$$$$"; prezzoMedio = 150; break;
                            default:
                                System.out.println(" Scelta non valida. Inserisci un numero da 1 a 6.");
                                continue;
                        }
                        break;
                    }

                    System.out.print("Inserisci nazione: ");
                    String nazione = scanner.nextLine();

                    System.out.print("Inserisci indirizzo: ");
                    String indirizzo = scanner.nextLine();

                    // Latitudine protetta
                    double latitudine = 0;
                    while (true) {
                        System.out.print("Inserisci latitudine (es. 45.95): ");
                        try {
                            latitudine = Double.parseDouble(scanner.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println(" Latitudine non valida, riprova.");
                        }
                    }

                    // Longitudine protetta
                    double longitudine = 0;
                    while (true) {
                        System.out.print("Inserisci longitudine (es. 8.43): ");
                        try {
                            longitudine = Double.parseDouble(scanner.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println(" Longitudine non valida, riprova.");
                        }
                    }

                    // Delivery si/no
                    boolean deliveryDisponibile = false;
                    while (true) {
                        System.out.print("Offre delivery? (si/no): ");
                        String rispostaDelivery = scanner.nextLine().trim().toLowerCase();
                        if (rispostaDelivery.equals("si") || rispostaDelivery.equals("sì")) {
                            deliveryDisponibile = true;
                            break;
                        } else if (rispostaDelivery.equals("no")) {
                            deliveryDisponibile = false;
                            break;
                        } else {
                            System.out.println(" Valore non valido. Scrivi solo 'si' o 'no'.");
                        }
                    }

                    // Prenotazione si/no
                    boolean prenotazioneOnlineDisponibile = false;
                    while (true) {
                        System.out.print("Offre prenotazione online? (si/no): ");
                        String rispostaPrenotazione = scanner.nextLine().trim().toLowerCase();
                        if (rispostaPrenotazione.equals("si") || rispostaPrenotazione.equals("sì")) {
                            prenotazioneOnlineDisponibile = true;
                            break;
                        } else if (rispostaPrenotazione.equals("no")) {
                            prenotazioneOnlineDisponibile = false;
                            break;
                        } else {
                            System.out.println(" Valore non valido. Scrivi solo 'si' o 'no'.");
                        }
                    }

                    Ristorante nuovo = new Ristorante(nome, citta, stelle, tipoCucina, fasciaPrezzo,
                            deliveryDisponibile, prenotazioneOnlineDisponibile, prezzoMedio,
                            nazione, indirizzo, latitudine, longitudine,
                            ristoratore.getUsername()
                    );

                    ristoranteManager.aggiungiRistorante(nuovo);
                    ristoranteManager.salvaSuFile("data/ristoranti_backup.dat");

                    System.out.println(" Ristorante aggiunto con successo.");
                    break;


                case "5":
                    List<Ristorante> miei = ristoranteManager.getRistorantiGestitiDa(ristoratore.getUsername());

                    for (Ristorante r : miei) {
                        System.out.println("" + r.getNome());
                        System.out.printf(" Media stelle: %.2f\n", r.getMediaStelle());
                        System.out.println(" Numero recensioni: " + r.getRecensioni().size());
                    }

                    break;


                case "6":
                    gestioneUtenti.salvaSuFile("data/utenti.dat");
                    ristoranteManager.salvaSuFile("data/ristoranti_backup.dat");
                    esci = true;

                    System.out.println("Logout effettuato.");
                    break;


                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }
    /**
     * Stampa a video un elenco di ristoranti.
     *
     * @param lista lista di ristoranti da mostrare
     */

    private static void stampaLista(List<Ristorante> lista) {
        if (lista.isEmpty()) {
            System.out.println(" Nessun ristorante trovato.");
        } else {
            System.out.println("Ristoranti trovati:");
            for (Ristorante r : lista) {
                System.out.println("- " + r);
            }
        }
    }

}
