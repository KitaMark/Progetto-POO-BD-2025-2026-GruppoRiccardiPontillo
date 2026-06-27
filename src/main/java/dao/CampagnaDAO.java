package dao;

import exception.NomeCampagnaInUsoException;
import model.*;
import exception.DatiMancantiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface CampagnaDAO {
    void leggiCampagne(HashMap<Campagna, Master> listaCampagne); //legge lista campagne da database e salva in memoria.
    int creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException;
    void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException;
    void leggiListaPersonaggi(List<Personaggio> lista, boolean isPg, String nomeCampagna) throws DatiMancantiException; //legge lista PG o PnG relativa alla Campagna.
    void leggiGiocatori(List<Giocatore> partecipanti, String nomeCampagna) throws DatiMancantiException;
    //per ora
}
