package dao;

import exception.NomeCampagnaInUsoException;
import model.Campagna;
import model.Master;
import exception.DatiMancantiException;
import java.util.HashMap;

public interface CampagnaDAO {
    void leggiCampagne(HashMap<Campagna, Master> listaCampagne); //legge lista campagne da database e salva in memoria.

    void creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException;

    void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException;
    //per ora
}
