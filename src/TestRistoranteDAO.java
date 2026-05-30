package theknife;

import java.util.List;

public class TestRistoranteDAO {

    public static void main(String[] args) {
        RistoranteDAO dao = new RistoranteDAO();

        Ristorante r = new Ristorante(
                "Ristorante Test",
                "Como",
                4,
                "Italiana",
                "€€",
                true,
                true,
                35,
                "Italia",
                "Via Roma 1",
                45.8081,
                9.0852,
                "mario.rossi"
        );

        boolean inserito = dao.inserisciRistorante(r);

        if (inserito) {
            System.out.println("Ristorante inserito nel database!");
        } else {
            System.out.println("Inserimento ristorante fallito.");
        }

        List<Ristorante> ristoranti = dao.trovaTutti();

        for (Ristorante rist : ristoranti) {
            System.out.println(rist);
        }
    }
}