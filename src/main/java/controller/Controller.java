package controller;
import exception.DatiMancantiException;
import model.*;

public class Controller {
    private Utente utenteAttivo; //tenere traccia utente nel sistema


       public Utente faiLogin(String identificativo, String password) throws DatiMancantiException {
           if(identificativo.isEmpty() || password.isEmpty()){
               throw new DatiMancantiException("Per favore, inserisci le tue credenziali (username/email e password) per accedere");
           }

           // chiedere DAO di cercare l'utente nel Database.


           // Utente utenteTrovato = utenteDAO.autentica(identificativo, password);

           // 3. Simuliamo il controllo sui risultati del DB
            /*
              if (utenteTrovato == null) {
            // Lanciamo un'eccezione generica
        */

           // 4. Salviamo l'utente attivo nel sistema
           // this.utenteAttivo = utenteTrovato;

           // return utenteTrovato;
           return null; // per il momento
       }

       public void registraUtente(String username, String password, String email, boolean isMaster) throws DatiMancantiException{
           if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
               throw new DatiMancantiException("Tutti i campi sono obbligatori.");
           }

           //inserire poi un controllo che dal dao veda se l'utente è gia registrato e presente, se lo è lancia eccezione

           Utente nuovoUtente;
           if (isMaster) {
               nuovoUtente = new Master(email, username, password);
           } else {
               nuovoUtente = new Giocatore(email, username, password);
           }

           //Diciamo al DAO di salvarlo nel database
           //utenteDAO.salvaUtente(nuovoUtente);


       }

    public Utente getUtenteAttivo() {
        return utenteAttivo;
    }


}
