package theknife;

import java.time.LocalDate;

public class TestDatabase {

    public static void main(String[] args) {
        UtenteDAO utenteDAO = new UtenteDAO();

        Utente u = new Utente(
                "Mario",
                "Rossi",
                "mario.rossi",
                "password_cifrata_test",
                false,
                "Como",
                LocalDate.of(2000, 1, 1)
        );

        boolean inserito = utenteDAO.inserisciUtente(u);

        if (inserito) {
            System.out.println("Utente inserito nel database!");
        } else {
            System.out.println("Inserimento utente fallito.");
        }
    }
}