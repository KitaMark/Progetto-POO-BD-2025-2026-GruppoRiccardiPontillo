package dao;

import model.Campagna;
import exception.DatiMancantiException;
import exception.NomeCampagnaInUsoException;
public interface MasterDAO{
    void creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException;
    void eliminaCampagna(String nomeCampagna) throws DatiMancantiException;
    //void visualizzaCampagna();
    //per il momento
}
