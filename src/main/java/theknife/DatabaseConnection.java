package theknife;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/dbTK";
    private static final String USER = "postgres";
    private static final String PASSWORD = "TheKnifeyh";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connessione a PostgreSQL riuscita!");
        } catch (SQLException e) {
            System.out.println("Errore connessione database:");
            e.printStackTrace();
        }
    }
}