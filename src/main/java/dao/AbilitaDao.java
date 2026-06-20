package dao;
import model.Classe;
import model.Personaggio;
import model.Classe;
public interface AbilitaDao {
    void imparaAbilita(int codPersonaggio, String nomeAbilita);
    void caricaAbilitaSbloccabili(Classe classe);
    void caricaAbilitaApprese(Personaggio pg);
}
