package theknife;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    public boolean inserisciUtente(Utente utente) {
        String sql = """
                INSERT INTO utenti 
                (nome, cognome, username, password_cifrata, is_ristoratore, domicilio, data_nascita)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getUsername());
            stmt.setString(4, utente.getPasswordCifrata());
            stmt.setBoolean(5, utente.isRistoratore());
            stmt.setString(6, utente.getDomicilio());

            if (utente.getDataDiNascita() != null) {
                stmt.setDate(7, Date.valueOf(utente.getDataDiNascita()));
            } else {
                stmt.setDate(7, null);
            }

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Errore inserimento utente:");
            e.printStackTrace();
            return false;
        }
    }

    public Utente cercaPerUsername(String username) {
        String sql = "SELECT * FROM utenti WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate dataNascita = null;
                    Date dataSql = rs.getDate("data_nascita");

                    if (dataSql != null) {
                        dataNascita = dataSql.toLocalDate();
                    }

                    return new Utente(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("username"),
                            rs.getString("password_cifrata"),
                            rs.getBoolean("is_ristoratore"),
                            rs.getString("domicilio"),
                            dataNascita
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Errore ricerca utente:");
            e.printStackTrace();
        }

        return null;
    }

    public List<Utente> trovaTutti() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utenti";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDate dataNascita = null;
                Date dataSql = rs.getDate("data_nascita");

                if (dataSql != null) {
                    dataNascita = dataSql.toLocalDate();
                }

                Utente u = new Utente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("password_cifrata"),
                        rs.getBoolean("is_ristoratore"),
                        rs.getString("domicilio"),
                        dataNascita
                );

                utenti.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Errore caricamento utenti:");
            e.printStackTrace();
        }

        return utenti;
    }
}