package controller;
import exception.*;
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



    public void iscrivitiCampagna(String nomeCampagna) throws NomeMancanteCampagnaException{
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Nome della campagna non valido.");
        }

        //tramite dao controlliamo se campagna esiste controlla se essa e iniziata e ci sono posti sufficienti
        //salva nel DB l'iscrizione

        System.out.println("Iscrizione effettuata alla campagna: " + nomeCampagna + " (Simulazione)");
    }


    public void rimuoviPGdaCampagna(String nomePersonaggio) throws PgNonSelezionatoException {
        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PgNonSelezionatoException("Seleziona un personaggio da rimuovere.");
        }
        // in futuro diremo al DAO di eliminarlo
        System.out.println("Personaggio rimosso: " + nomePersonaggio + " (Simulazione)"); //per ora
    }

    public void modificaStatistichePG(String nomePersonaggio) throws Exception {
        // Qui passeremo anche i nuovi valori delle statistiche
        System.out.println("Apertura finestra di modifica statistiche per: " + nomePersonaggio);
    }

    public void assegnaPuntiStatistica(String nomePersonaggio, int quantitaPunti) throws Exception {
        // In futuro: aggiorneremo il saldo dei punti spendibili del PG nel DB
        System.out.println("Assegnati " + quantitaPunti + " punti a " + nomePersonaggio + " (Simulazione)"); //per ora
    }



    public void creaPnG(String nomePnG, String razza, int livello) throws NomeMancantePngException {
        if (nomePnG == null || nomePnG.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        // in futuro lo salveremo nel DB
        System.out.println("Nuovo PnG creato: " + nomePnG + " (Simulazione)");//per ora
    }

    public void rimuoviPnG(String nomePnG) throws PngNonSelezionatoException {
        if (nomePnG == null || nomePnG.trim().isEmpty()) {
            throw new PngNonSelezionatoException("Seleziona un PnG da rimuovere.");
        }
        System.out.println("PnG rimosso: " + nomePnG + " (Simulazione)"); //per ora
    }

    // --- TAB 3: IMPOSTAZIONI CAMPAGNA ---

    public void cambiaStatoCampagna(String nomeCampagna, String nuovoStato) throws Exception {
        // Dao cambia o in Corso o Finita
        // Una volta "In Corso", i giocatori non potranno più iscriversi.
        System.out.println("Lo stato della campagna '" + nomeCampagna + "' è ora: " + nuovoStato + " (Simulazione)");// per ora
    }


    public void aumentaStatistica(String statistica) throws StatisticaNonSelezionataException {
        if (statistica == null || statistica.trim().isEmpty()) {
            throw new StatisticaNonSelezionataException("Inserisci una statistica valida.");
        }
        // Dao fa controllo per vedere se ha i punti da spendere
        System.out.println("Statistica '" + statistica + "' aumentata con successo! (Simulazione)");//per ora
    }

    public void compraOggetto(String nomeOggetto) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Inserisci un oggetto valido.");
        }
        // Dao controlla se PG esiste e ha abbastanza oro
        System.out.println("Oggetto '" + nomeOggetto + "' acquistato! (Simulazione)");//per ora
    }




}
