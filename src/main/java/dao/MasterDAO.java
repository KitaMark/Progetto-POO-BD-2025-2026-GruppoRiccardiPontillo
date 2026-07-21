package dao;

import model.Giocatore;
import model.Personaggio;

public interface MasterDAO{
    void rimuoviPersonaggio(Personaggio pg);
    void assegnaPuntiStatistica(Personaggio personaggio, int quantitaPunti);
    int creaPnG(Personaggio png);
    void rimuoviGiocatore(int idGiocatore, int idCampagna);
}
