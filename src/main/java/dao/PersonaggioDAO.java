package dao;

import model.Statistica;

public interface PersonaggioDAO {
    void aggiornaStatistichePersonaggio (int idPersonaggio, Statistica modifiche);  //aggiorna gli attributi del personaggio nel database
}
