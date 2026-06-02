package theknife;

import java.util.List;

public class TestLetturaUtenti {

    public static void main(String[] args) {
        UtenteDAO dao = new UtenteDAO();

        List<Utente> utenti = dao.trovaTutti();

        for (Utente u : utenti) {
            System.out.println(u);
        }

        Utente mario = dao.cercaPerUsername("mario.rossi");

        if (mario != null) {
            System.out.println("Trovato: " + mario.getUsername());
        } else {
            System.out.println("Utente non trovato.");
        }
    }
}