package dao;

import model.Statistica;

public interface StatisticaDAO {
    void aggiornaStatistichePersonaggio (int idPersonaggio, Statistica modifiche);  //aggiorna gli attributi del personaggio nel database
}
