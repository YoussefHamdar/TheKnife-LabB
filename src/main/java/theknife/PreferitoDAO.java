package theknife;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreferitoDAO {

    public boolean aggiungiPreferito(String username, String nomeRistorante) {
        String sql = """
                INSERT INTO preferiti (username, nome_ristorante)
                VALUES (?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, nomeRistorante);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Errore aggiunta preferito:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean rimuoviPreferito(String username, String nomeRistorante) {
        String sql = """
                DELETE FROM preferiti
                WHERE username = ? AND nome_ristorante = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, nomeRistorante);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Errore rimozione preferito:");
            e.printStackTrace();
            return false;
        }
    }

    public List<String> visualizzaPreferiti(String username) {
        List<String> preferiti = new ArrayList<>();

        String sql = """
                SELECT nome_ristorante
                FROM preferiti
                WHERE username = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    preferiti.add(rs.getString("nome_ristorante"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Errore visualizzazione preferiti:");
            e.printStackTrace();
        }

        return preferiti;
    }
}