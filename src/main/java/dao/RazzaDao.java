package dao;

import model.Razza;


public interface RazzaDao {
    int salvaRazza(Razza razza, String descrizione, int idCampagna) throws Exception;
}