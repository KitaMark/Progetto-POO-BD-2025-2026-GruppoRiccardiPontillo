package dao;

import exception.AutenticazioneException;
import model.Utente;
import java.util.List;

public interface UtenteDAO {
    void leggiUtenti(List<Utente> utenti) ;
    void aggiungiUtente(Utente utente) throws AutenticazioneException;
    //non credo serva, vedi commento nella classe di implementazione
    //Utente autenticaUtente(String username, String password)throws AutenticazioneException;
}