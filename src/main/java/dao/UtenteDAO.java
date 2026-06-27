package dao;

import exception.AutenticazioneException;
import model.Utente;
import java.util.List;

public interface UtenteDAO {
    void leggiUtenti(List<Utente> utenti) ;
    int aggiungiUtente(Utente utente) throws AutenticazioneException;
}