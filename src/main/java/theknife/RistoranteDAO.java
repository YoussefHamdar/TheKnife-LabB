package theknife;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RistoranteDAO {

    public boolean inserisciRistorante(Ristorante ristorante) {
        String sql = """
                INSERT INTO ristoranti
                (nome, citta, stelle, tipo_cucina, fascia_prezzo,
                 delivery_disponibile, prenotazione_online_disponibile,
                 indirizzo, nazione, latitudine, longitudine,
                 prezzo_medio, descrizione, gestore)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ristorante.getNome());
            stmt.setString(2, ristorante.getCitta());
            stmt.setInt(3, ristorante.getStelle());
            stmt.setString(4, ristorante.getTipoCucina());
            stmt.setString(5, ristorante.getFasciaPrezzo());
            stmt.setBoolean(6, ristorante.isDeliveryDisponibile());
            stmt.setBoolean(7, ristorante.isPrenotazioneOnlineDisponibile());
            stmt.setString(8, ristorante.getIndirizzo());
            stmt.setString(9, ristorante.getNazione());
            stmt.setDouble(10, ristorante.getLatitudine());
            stmt.setDouble(11, ristorante.getLongitudine());
            stmt.setInt(12, 0);
            stmt.setString(13, ristorante.getDescrizione());
            stmt.setString(14, ristorante.getGestore());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Errore inserimento ristorante:");
            e.printStackTrace();
            return false;
        }
    }

    public List<Ristorante> trovaTutti() {
        List<Ristorante> ristoranti = new ArrayList<>();

        String sql = "SELECT * FROM ristoranti";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ristorante r = new Ristorante(
                        rs.getString("nome"),
                        rs.getString("citta"),
                        rs.getInt("stelle"),
                        rs.getString("tipo_cucina"),
                        rs.getString("fascia_prezzo"),
                        rs.getBoolean("delivery_disponibile"),
                        rs.getBoolean("prenotazione_online_disponibile"),
                        rs.getInt("prezzo_medio"),
                        rs.getString("nazione"),
                        rs.getString("indirizzo"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"),
                        rs.getString("gestore")
                );

                ristoranti.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Errore caricamento ristoranti:");
            e.printStackTrace();
        }

        return ristoranti;
    }

    public List<Ristorante> cercaPerCitta(String citta) {
        List<Ristorante> ristoranti = new ArrayList<>();

        String sql = "SELECT * FROM ristoranti WHERE LOWER(citta) LIKE LOWER(?) LIMIT 20";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + citta + "%");

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Ristorante r = new Ristorante(
                            rs.getString("nome"),
                            rs.getString("citta"),
                            rs.getInt("stelle"),
                            rs.getString("tipo_cucina"),
                            rs.getString("fascia_prezzo"),
                            rs.getBoolean("delivery_disponibile"),
                            rs.getBoolean("prenotazione_online_disponibile"),
                            rs.getInt("prezzo_medio"),
                            rs.getString("nazione"),
                            rs.getString("indirizzo"),
                            rs.getDouble("latitudine"),
                            rs.getDouble("longitudine"),
                            rs.getString("gestore")
                    );

                    ristoranti.add(r);
                }
            }

        } catch (SQLException e) {
            System.out.println("Errore ricerca ristoranti:");
            e.printStackTrace();
        }

        return ristoranti;
    }
    public String visualizzaRiepilogo(String nomeRistorante) {
        String sql = """
            SELECT COUNT(*) AS numero_recensioni,
                   COALESCE(AVG(stelle), 0) AS media_stelle
            FROM recensioni
            WHERE nome_ristorante = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeRistorante);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int numero = rs.getInt("numero_recensioni");
                    double media = rs.getDouble("media_stelle");

                    return "numero recensioni: " + numero +
                            ", media stelle: " + String.format("%.2f", media);
                }
            }

        } catch (SQLException e) {
            System.out.println("Errore riepilogo recensioni:");
            e.printStackTrace();
        }

        return "riepilogo non disponibile";
    }
}