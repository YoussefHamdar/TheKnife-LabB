package theknife;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ImportaRistorantiCSV {

    public static void main(String[] args) {
        String percorso = "data/michelin_my_maps.csv";

        String sql = """
                INSERT INTO ristoranti
                (nome, citta, stelle, tipo_cucina, fascia_prezzo,
                 delivery_disponibile, prenotazione_online_disponibile,
                 indirizzo, nazione, latitudine, longitudine,
                 prezzo_medio, descrizione, gestore)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int importati = 0;
        int saltati = 0;
        int righeTotali = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(percorso))) {

            conn.setAutoCommit(false);

            br.readLine(); // salta intestazione

            String riga;
            while ((riga = br.readLine()) != null) {
                righeTotali++;

                try {
                    String[] campi = riga.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    if (campi.length < 7) {
                        saltati++;
                        continue;
                    }

                    String nome = pulisci(campi[0]);
                    String indirizzo = pulisci(campi[1]);
                    String location = pulisci(campi[2]);
                    String fasciaPrezzo = pulisci(campi[3]);
                    String cucina = pulisci(campi[4]);

                    double longitudine = parseDoubleSicuro(pulisci(campi[5]));
                    double latitudine = parseDoubleSicuro(pulisci(campi[6]));

                    String descrizione = "";
                    if (campi.length > 7) {
                        descrizione = pulisci(campi[7]);
                    }

                    String citta = location;
                    String nazione = "";

                    if (location.contains(",")) {
                        String[] parti = location.split(",");
                        citta = parti[0].trim();
                        nazione = parti[parti.length - 1].trim();
                    }

                    int prezzoMedio = calcolaPrezzoMedio(fasciaPrezzo);

                    stmt.setString(1, nome);
                    stmt.setString(2, citta);
                    stmt.setInt(3, 0);
                    stmt.setString(4, cucina);
                    stmt.setString(5, fasciaPrezzo);
                    stmt.setBoolean(6, false);
                    stmt.setBoolean(7, true);
                    stmt.setString(8, indirizzo);
                    stmt.setString(9, nazione);
                    stmt.setDouble(10, latitudine);
                    stmt.setDouble(11, longitudine);
                    stmt.setInt(12, prezzoMedio);
                    stmt.setString(13, descrizione);
                    stmt.setString(14, "dataset");

                    stmt.addBatch();

                    importati++;

                    if (importati % 500 == 0) {
                        stmt.executeBatch();
                        conn.commit();
                        System.out.println("Importati finora: " + importati);
                    }

                } catch (Exception e) {
                    saltati++;
                }
            }

            stmt.executeBatch();
            conn.commit();

            System.out.println("Importazione completata.");
            System.out.println("Righe totali lette: " + righeTotali);
            System.out.println("Ristoranti importati: " + importati);
            System.out.println("Righe saltate: " + saltati);

        } catch (Exception e) {
            System.out.println("Errore importazione CSV:");
            e.printStackTrace();
        }
    }

    private static String pulisci(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("^\"|\"$", "");
    }

    private static double parseDoubleSicuro(String s) {
        try {
            if (s == null || s.isBlank()) return 0.0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int calcolaPrezzoMedio(String fasciaPrezzo) {
        return switch (fasciaPrezzo) {
            case "€", "$" -> 20;
            case "€€", "$$" -> 40;
            case "€€€", "$$$" -> 70;
            case "€€€€", "$$$$" -> 100;
            default -> 0;
        };
    }
}