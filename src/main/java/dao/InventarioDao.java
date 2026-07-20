package dao;

import model.Oggetto;
import model.OggettoConsumabile;
import model.OggettoEquipaggiabile;

import java.util.List;
import java.util.Map;

public interface InventarioDao {

    void caricaInventarioPersonaggio(int codPersonaggio, Map<OggettoConsumabile, Integer> inventarioConsumabili,
                                     Map<OggettoEquipaggiabile, Boolean> inventarioEquipaggiabili);
    void acquistaOggetto(int codPersonaggio, int codOggetto, int costoOggetto);
    void vendiOggetto(int codPersonaggio, int codOggetto, int ricavoOggetto);
    void impostaEquipaggiamento(int codPersonaggio, int codOggetto, boolean equipaggiato);
    void consumaOggetto(int codPersonaggio, int codOggetto);

}