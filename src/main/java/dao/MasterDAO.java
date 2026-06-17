package dao;

import model.Personaggio;

public interface MasterDAO{
    void rimuoviPersonaggio(Personaggio pg);

    void assegnaPuntiStatistica(Personaggio personaggio, int quantitaPunti);
    //void visualizzaCampagna();
    //per il momento
}
