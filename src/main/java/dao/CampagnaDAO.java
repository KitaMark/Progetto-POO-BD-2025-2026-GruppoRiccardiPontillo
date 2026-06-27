package dao;

import exception.NomeCampagnaInUsoException;
import model.*;
import exception.DatiMancantiException;

import java.util.HashMap;
import java.util.List;

public interface CampagnaDAO {
    void leggiCampagne(HashMap<Campagna, Master> listaCampagne); //legge lista campagne da database e salva in memoria.
    void creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException;
    void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException;
    void leggiListaPersonaggi(List<Personaggio> lista, boolean isPg, String nomeCampagna) throws DatiMancantiException; //legge lista PG o PnG relativa alla Campagna.
    void leggiGiocatori(List<Giocatore> partecipanti, String nomeCampagna) throws DatiMancantiException;
    void leggiCatalogoOggetti(List<Oggetto> catalogo, int idCampagna);
    void leggiListaRazze(List<Razza> lista, int idCampagna);
    void leggiListaClassi(List<Classe> lista, int idCampagna);
    //per ora
}
