package implementazionePostgresDAO;

import dao.CampagnaDAO;
import model.Campagna;
import model.Master;

import java.util.HashMap;

public class ImplementazionePostgresCampagna implements CampagnaDAO {
    @Override
    public void leggiCampagne(HashMap<Campagna, Master> listaCampagne) {
        // 1. Creiamo i Master finti
        Master master1 = new Master("o","Gandalf", "o");
        Master master2 = new Master("a","CyberDM","a");

        // 2. Creiamo le Campagne finte
        Campagna c1 = new Campagna("La Ruota del Destino", 5, master1);
        c1.setIniziata(true);
        // (Opzionale) Aggiungi qui eventuali partecipanti o PG se necessario

        Campagna c2 = new Campagna("Neon & Sangue", 3, master2);
        c2.setIniziata(false);

        // 3. Inseriamo la coppia (Campagna, Master) nella HashMap come richiesto dal tuo DAO
        listaCampagne.put(c1, master1);
        listaCampagne.put(c2, master2);
        //TODO: da implementare
    }

    @Override
    public void rimuoviCampagna(Campagna campagnaTarget) {
        //TODO: da implementare
    }
}
