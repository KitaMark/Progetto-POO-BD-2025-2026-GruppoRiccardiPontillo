package dao;

import model.Utente;
import java.util.List;

public interface UtenteDAO {
    void leggiUtenti(List<Utente> utenti);
    void aggiungiUtente(Utente utente);
}