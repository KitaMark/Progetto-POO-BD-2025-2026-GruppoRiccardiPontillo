package dao;
import model.Classe;

public interface ClasseDao {
    int salvaClasse(Classe classe, String descrizione, int idCampagna);
}