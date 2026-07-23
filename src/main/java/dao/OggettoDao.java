package dao;

import model.OggettoConsumabile;
import model.OggettoEquipaggiabile;

public interface OggettoDao {
    int salvaConsumabile(OggettoConsumabile consumabile, int idCampagna);
    int salvaEquipaggiamento(OggettoEquipaggiabile equipaggiamento, int idCampagna);
}