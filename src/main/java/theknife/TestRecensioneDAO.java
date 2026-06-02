package theknife;

import java.time.LocalDate;
import java.util.List;

public class TestRecensioneDAO {

    public static void main(String[] args) {
        RecensioneDAO dao = new RecensioneDAO();

        Recensione r = new Recensione(
                "mario.rossi",
                "Ristorante Test",
                "Ottimo ristorante di prova!",
                5,
                LocalDate.now()
        );

        boolean inserita = dao.inserisciRecensione(r);

        if (inserita) {
            System.out.println("Recensione inserita!");
        } else {
            System.out.println("Inserimento recensione fallito.");
        }

        List<Recensione> recensioni = dao.trovaPerRistorante("Ristorante Test");

        for (Recensione rec : recensioni) {
            System.out.println(rec);
        }
    }
}