package dao;

import exception.NomeCampagnaInUsoException;
import model.Campagna;
import model.Giocatore;
import model.Master;
import exception.DatiMancantiException;
import model.Personaggio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface CampagnaDAO {
    void leggiCampagne(HashMap<Campagna, Master> listaCampagne); //legge lista campagne da database e salva in memoria.
    void creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException;
    void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException;
    void leggiListaPersonaggi(List<Personaggio> lista, boolean isPg) throws DatiMancantiException; //legge lista PG o PnG relativa alla Campagna.
    void leggiGiocatori(List<Giocatore> partecipanti) throws DatiMancantiException;
    //per ora
}
