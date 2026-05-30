package theknife;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAO {

    public boolean inserisciRecensione(Recensione recensione) {
        String sql = """
                INSERT INTO recensioni
                (autore, nome_ristorante, testo, stelle, data_recensione, risposta_ristoratore)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recensione.getAutore());
            stmt.setString(2, recensione.getNomeRistorante());
            stmt.setString(3, recensione.getTesto());
            stmt.setInt(4, recensione.getStelle());

            if (recensione.getData() != null) {
                stmt.setDate(5, Date.valueOf(recensione.getData()));
            } else {
                stmt.setDate(5, null);
            }

            stmt.setString(6, recensione.getRispostaDelRistoratore());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Errore inserimento recensione:");
            e.printStackTrace();
            return false;
        }
    }

    public List<Recensione> trovaPerRistorante(String nomeRistorante) {
        List<Recensione> recensioni = new ArrayList<>();

        String sql = "SELECT * FROM recensioni WHERE nome_ristorante = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeRistorante);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione(
                            rs.getString("autore"),
                            rs.getString("nome_ristorante"),
                            rs.getString("testo"),
                            rs.getInt("stelle"),
                            rs.getDate("data_recensione").toLocalDate()
                    );

                    r.setRispostaDelRistoratore(rs.getString("risposta_ristoratore"));
                    recensioni.add(r);
                }
            }

        } catch (SQLException e) {
            System.out.println("Errore caricamento recensioni:");
            e.printStackTrace();
        }

        return recensioni;
    }

    public boolean rispondiRecensione(int idRecensione, String risposta) {
        String sql = "UPDATE recensioni SET risposta_ristoratore = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, risposta);
            stmt.setInt(2, idRecensione);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Errore risposta recensione:");
            e.printStackTrace();
            return false;
        }
    }
}