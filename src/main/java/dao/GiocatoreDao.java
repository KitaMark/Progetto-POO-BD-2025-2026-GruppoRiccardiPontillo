package dao;
import model.Giocatore;
import model.Personaggio;
import model.Campagna;
import java.util.ArrayList;
import java.util.HashMap;


public interface GiocatoreDao {
    void iscrivitiACampagna(int codUtente, int codCampagna);

    void salvaPersonaggio(Personaggio pg, int codUtente, int codCampagna);
    void aggiornaRisorse(Personaggio pg);
    HashMap<Campagna, Personaggio> caricaTutteLePartecipazioni(int codUtente);
    }
