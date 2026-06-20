package dao;

import model.Oggetto;
import java.util.List;
import java.util.Map;

public interface InventarioDao {


    List<Oggetto> caricaCatalogoNegozio();
    Map<Oggetto, Integer> caricaInventarioPersonaggio(int codPersonaggio);
    void acquistaOggetto(int codPersonaggio, int codOggetto, int costoOggetto);
    void vendiOggetto(int codPersonaggio, int codOggetto, int ricavoOggetto);
    void impostaEquipaggiamento(int codPersonaggio, int codOggetto, boolean equipaggiato);
    void consumaOggetto(int codPersonaggio, int codOggetto);

}