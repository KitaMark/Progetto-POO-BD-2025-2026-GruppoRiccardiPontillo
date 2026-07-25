package dao;

import model.Giocatore;
import model.Personaggio;

public interface MasterDAO{
    void rimuoviPersonaggio(Personaggio pg);
    void assegnaPuntiStatistica(int idPersonaggio, int quantitaPunti);
    void creaPnG(Personaggio png, int codCampagna);
    void rimuoviGiocatore(int idGiocatore, int idCampagna);
}
