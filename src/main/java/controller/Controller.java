package controller;
import exception.CampagnaAttivaEsistenteException;
import exception.DatiMancantiException;
import exception.NomeMancanteCampagnaException;
import model.*;
import java.util.List;
import java.util.ArrayList;


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

    public void creaCampagna(String nomeCampagna, int maxGiocatori) throws CampagnaAttivaEsistenteException, NomeMancanteCampagnaException {
        if(nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Il nome della campagna non può essere vuoto.");
        }

        // chiedere Dao se master ha campagna attiva
        // Se sì  throw new CampagnaAttivaEsistenteException("Hai già una campagna attiva. Concludila prima di crearne una nuova.");

        // Creazione e salvataggio nel DB (da implementare col DAO)
        System.out.println("Campagna '" + nomeCampagna + "' creata con successo! (Simulazione)");
    }


    public void eliminaCampagna(String nomeCampagna) throws Exception {
        // Dao elimina campagna selezionata
        System.out.println("Campagna '" + nomeCampagna + "' eliminata! (Simulazione)"); // per il momento
    }

    public void logout() {
        this.utenteAttivo = null;
        System.out.println("Logout effettuato. Utente scollegato.");
    }

    public void entraNellaCampagna(String nomeCampagna) throws NomeMancanteCampagnaException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Nome della campagna non valido.");
        }

        // In futuro: this.campagnaAttiva = campagnaDAO.trovaCampagna(nomeCampagna);
        System.out.println("Accesso in corso alla campagna: '" + nomeCampagna + "' (Simulazione)"); // per il momento
    }

    //per popolare la jtable
    public List<Campagna> getCampagneDelMaster() {
        // DAO cerca solo le campagne associate all'utente attualmente loggato.
        // return campagnaDAO.trovaCampagnePerMaster(utenteAttivo.getUsername());
        return new ArrayList<>();  // per il momento
    }


}
