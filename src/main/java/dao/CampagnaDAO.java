package dao;

import model.Campagna;
import model.Master;

import java.util.HashMap;

public interface CampagnaDAO {
    void leggiCampagne(HashMap<Campagna, Master> listaCampagne); //legge lista campagne da database e salva in memoria.

    void rimuoviCampagna(Campagna campagnaTarget);
    //per ora
}
