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

           Master testMaster = new Master("test", "TestUser", "password"); // per il momento
           this.utenteAttivo = testMaster;
           return  utenteAttivo;
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
    public List<Campagna> getCampagne() {
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

    public void creaNuovoPersonaggio(String nome, String razza, String classe, String nomeCampagna) throws NomePgNonValidoException {
        if (nome == null || nome.isEmpty()) {
            throw new NomePgNonValidoException("Nome non valido.");
        }

        // 1. Il DAO recupera l'oggetto 'Razza' e l'oggetto 'Classe' dal database basandosi sui nomi in stringa.
        // 2. Chiamerai giocatore.creaPersonaggio(classeTrovata, razzaTrovata, nome, campagnaTrovata);

        System.out.println("Creato nuovo PG: " + nome + " | Razza: " + razza + " | Classe: " + classe + " (Simulazione)");//per ora
    }

    public void creaPnGBase(String nome, String razza, String classe, String nomeCampagna) throws NomeMancantePngException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        // In futuro  costruttore base Master.creaPersonaggio(classe, razza, nome)
        System.out.println("Creato PnG BASE: " + nome + " | Razza: " + razza + " | Classe: " + classe);
    }

    public void creaPnGAvanzato(String nome, String razza, String classe, int oro, int punti, String nomeCampagna) throws NomeMancantePngException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        // In futuroil costruttore Master.creaPersonaggio(classe, razza, stat, nome, oro, punti)
        System.out.println("Creato PnG AVANZATO: " + nome + " | Oro: " + oro + " | Punti: " + punti);
    }

    public void salvaStatisticheModificate(String nomePersonaggio, int forza, int destrezza, int costituzione,
                                           int intelligenza, int fede, int carisma, int fortuna,
                                           int hpMax, int manaMax) throws PngNonSelezionatoException {

        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PngNonSelezionatoException("Nessun personaggio selezionato.");
        }


        //  Il DAO recupererà l'oggetto Personaggio dal database
        //  masterLoggato.modificaStatistichePersonaggio(pgTrovato, forza, destrezza, ...);

        System.out.println("Statistica aggiornate per il PG: " + nomePersonaggio + " (Simulazione)"); //per ora
    }



    public void equipaggiaOggetto(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da equipaggiare.");
        }
        // Dao fa:
        // boolean successo = pg.equipaggia((OggettoEquipaggiabile) oggetto);
        // se successo == false, lanceremo un'eccezione "Statistica insufficienti!"

        System.out.println("Equipaggiato l'oggetto: " + nomeOggetto + " (Simulazione)");//per ora
    }

    public void rimuoviEquipaggiamento(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da rimuovere.");
        }
        // In futuro: pg.rimuoviEquipaggiamento((OggettoEquipaggiabile) oggetto);

        System.out.println("Rimosso l'equipaggiamento: " + nomeOggetto + " (Simulazione)");//per ora
    }

    public void usaConsumabile(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un consumabile da usare.");
        }
        // In futuro: pg.usaConsumabile((OggettoConsumabile) oggetto);
        // Questo ripristinerà HP o Mana

        System.out.println("Hai utilizzato: " + nomeOggetto + " (Simulazione)");//per ora
    }

    public void vendiOggetto(String nomeOggetto, String nomeCampagna) throws Exception {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new Exception("Seleziona un oggetto da vendere.");
        }
        // In futuro: pg.vendiOggetto(oggetto);
        // Questo rimuoverà l'oggetto e aumenterà l'oro del PG della metà del costo originale.

        System.out.println("Venduto al mercante: " + nomeOggetto + " (Simulazione)");//per ora
    }

    public void imparaAbilita(String nomeAbilita, String nomeCampagna) {
           System.out.println("Appresa abilità: " + nomeAbilita);
       }




}
