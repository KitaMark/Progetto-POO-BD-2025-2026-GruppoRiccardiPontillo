package controller;
import dao.*;
import exception.*;
import implementazionePostgresDAO.*;
import model.*;

import java.util.*;


/**
 * Il suo compito è ricevere le richieste provenienti dalle interfacce grafiche (Boundary),
 * orchestrare i Casi d'Uso manipolando le classi del dominio di gioco (Entity/model) e,
 * infine, delegare il salvataggio o il recupero dei dati al livello di persistenza (DAO).
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class Controller {
    /**
     * L'utente (Giocatore o Master) attualmente autenticato nel sistema.
     * Mantiene la sessione attiva durante l'utilizzo dell'applicazione.
     */
    private Utente utenteAttivo; //tenere traccia utente nel sistema
    private ArrayList<Utente> listaUtenti; //qui vengono copiati i dati ad ogni avvio dal db, per poter elaborarli più velocemente
    private UtenteDAO utenteDAO;
    private HashMap<Campagna, Master> listaCampagne;
    private MasterDAO masterDAO;
    private CampagnaDAO campagnaDAO;
    private Campagna campagnaAttiva; //la campagna con cui si sta interagendo.

    public Controller() {
        utenteAttivo = null;
        listaUtenti = new ArrayList<>();
        utenteDAO = new ImplementazionePostgresUtente();
        utenteDAO.leggiUtenti(listaUtenti);
        listaCampagne = new HashMap<>();
        masterDAO = new ImplementazionePostgresMaster();
        campagnaDAO = new ImplementazionePostgresCampagna();
        campagnaDAO.leggiCampagne(listaCampagne);
        //eventualmente da inserire altro in seguito
    }

    /**
     * Autentica un utente nel sistema verificandone le credenziali.
     *
     * @param username L'username dell'utente.
     * @param email    L'email associata all'account.
     * @param password La password di accesso.
     * @return L'istanza dell'{@link Utente} (strutturata come Master o Giocatore) recuperata dal sistema.
     * @throws DatiMancantiException Se uno dei campi di testo risulta vuoto o se le credenziali non sono valide.
     */
    public Utente faiLogin(String username, String email, String password, boolean isMaster) throws DatiMancantiException {
        if (username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            throw new DatiMancantiException("Per favore, inserisci le tue credenziali (username, email e password) per accedere");
        }
        Utente utenteTrovato = cercaUtente(username, email, password);
        if (utenteTrovato == null) throw new DatiMancantiException("Credenziali non valide.");
        if(isMaster && utenteTrovato instanceof Giocatore) throw new AutenticazioneException("L'account è registrato come Giocatore!");
        if(!isMaster && utenteTrovato instanceof Master) throw new AutenticazioneException("L'account è registrato come Master!");
        this.utenteAttivo = utenteTrovato;
        return this.utenteAttivo;
    }

    /**
     * Registra un nuovo account utente all'interno del sistema, definendone il ruolo.
     *
     * @param username Lo pseudonimo scelto dall'utente.
     * @param password La password per l'autenticazione.
     * @param email    L'indirizzo email di contatto.
     * @param isMaster {@code true} se l'utente si sta registrando come Master, {@code false} come Giocatore.
     * @throws DatiMancantiException Se uno dei parametri di registrazione è vuoto.
     */
    public void registraUtente(String username, String password, String email, boolean isMaster) throws DatiMancantiException, AutenticazioneException {
        if (username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            throw new DatiMancantiException("Tutti i campi sono obbligatori.");
        }

        for(Utente utente : listaUtenti) {
            if (Objects.equals(utente.getUsername(), username)){
                throw new AutenticazioneException("Username già in uso.");
            } else if(Objects.equals(utente.getEmail(), email)){
                throw new AutenticazioneException("Email già in uso.");
            }
        }

        Utente nuovoUtente;
        if (isMaster) {
            nuovoUtente = new Master(email, username, password);
        } else {
            nuovoUtente = new Giocatore(email, username, password);
        }
        utenteDAO.aggiungiUtente(nuovoUtente); //salva il dato, considerare rimuovere quest'istruzione e salvare alla fine.
        listaUtenti.add(nuovoUtente); //dato transiente per accesso rapido
    }

    /**
     * Restituisce l'utente attualmente loggato e attivo nella sessione.
     *
     * @return L'oggetto {@link Utente} attivo.
     */
    public Utente getUtenteAttivo() {
        return utenteAttivo;
    }

    /**
     * Permette a un Master di creare una nuova campagna di gioco.
     *
     * @param nomeCampagna Il nome identificativo della campagna.
     * @param maxGiocatori Il limite massimo di giocatori ammessi.
     * @throws CampagnaAttivaEsistenteException Se il Master ha già una campagna in corso.
     * @throws NomeMancanteCampagnaException    Se il nome della campagna è nullo o vuoto.
     */
    public void creaCampagna(String nomeCampagna, int maxGiocatori) throws CampagnaAttivaEsistenteException, NomeMancanteCampagnaException,NomeCampagnaInUsoException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Il nome della campagna non può essere vuoto.");
        }
        if(listaCampagne.containsValue((Master) utenteAttivo)) throw new CampagnaAttivaEsistenteException("Hai già una campagna attiva. Concludila prima di crearne una nuova.");
        Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, (Master) utenteAttivo);
        listaCampagne.put(campagna, (Master) utenteAttivo);
        //per ora salviamo subito nel db. Rivedere in futuro.
        campagnaDAO.creaCampagna(campagna);
    }


    /**
     * Elimina definitivamente una campagna dal sistema.
     *
     * @param nomeCampagna Il nome della campagna da rimuovere.
     * @throws Exception Se si verifica un errore durante l'eliminazione dal database.
     */
    public boolean eliminaCampagna(String nomeCampagna) throws DatiMancantiException {
        Campagna campagnaTarget = cercaCampagna(nomeCampagna);
        if(campagnaTarget == null) throw new DatiMancantiException("Campagna non esistente.");
        listaCampagne.remove(campagnaTarget);
        campagnaDAO.eliminaCampagna(campagnaTarget);
        return true;
    }

    /**
     * Disconnette l'utente attivo, azzerando la sessione corrente.
     */
    public void logout() {
        this.utenteAttivo = null;
        System.out.println("Logout effettuato. Utente scollegato.");
    }

    /**
     * Entra nell'unica campagna da lui gestita (Master), visualizza le campagne a cui è iscritto (Giocatore).
     *
     * @param nomeCampagna Il nome della campagna in cui entrare.
     * @throws DatiMancantiException Se il nome fornito non è valido.
     * @throws RuntimeException Se non è possibile accedere alla campagna.
     */
    public void entraNellaCampagna(String nomeCampagna) throws DatiMancantiException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new DatiMancantiException("Nome della campagna non valido.");
        }
        this.campagnaAttiva = cercaCampagna(nomeCampagna);
        if(campagnaAttiva == null) throw new RuntimeException("Campagna non esistente.");
    }

    /**
     * Recupera l'elenco delle campagne associate all'utente attualmente loggato.
     *
     * @return Una lista di oggetti {@link Campagna}. Ritorna una lista vuota se nessuna campagna è trovata.
     */
    public List<Campagna> getCampagne() {
        // DAO cerca solo le campagne associate al giocatore attualmente loggato.
        // return campagnaDAO.trovaCampagnePerMaster(utenteAttivo.getUsername());
        return new ArrayList<>();  // per il momento
    }


    /**
     * Permette a un Giocatore di iscriversi a una campagna non ancora iniziata e ci sono posti sufficienti.
     *
     * @param nomeCampagna Il nome della campagna a cui partecipare.
     * @throws NomeMancanteCampagnaException Se il nome fornito non è valido.
     */
    public void iscrivitiCampagna(String nomeCampagna) throws NomeMancanteCampagnaException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Nome della campagna non valido.");
        }

        //tramite dao controlliamo se campagna esiste controlla se essa e iniziata e ci sono posti sufficienti
        //salva nel DB l'iscrizione

        System.out.println("Iscrizione effettuata alla campagna: " + nomeCampagna + " (Simulazione)");
    }


    /**
     * Rimuove un Personaggio Giocante (PG) dalla Campagna gestita dal Master.
     *
     * @param nomePersonaggio Il nome del PG da espellere o rimuovere.
     * @throws PgNonSelezionatoException Se non viene specificato alcun personaggio.
     */
    public void rimuoviPGdaCampagna(String nomePersonaggio) throws PgNonSelezionatoException {
        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PgNonSelezionatoException("Seleziona un personaggio da rimuovere.");
        }
        // in futuro diremo al DAO di eliminarlo
        System.out.println("Personaggio rimosso: " + nomePersonaggio + " (Simulazione)"); //per ora
    }

    /**
     * Apre il flusso di modifica per le statistiche di un PG specifico.
     *
     * @param nomePersonaggio Il nome del PG da modificare.
     * @throws Exception In caso di errori di comunicazione.
     */
    public void modificaStatistichePG(String nomePersonaggio) throws Exception {
        // Qui passeremo anche i nuovi valori delle statistiche
        System.out.println("Apertura finestra di modifica statistiche per: " + nomePersonaggio);
    }

    /**
     * Assegna punti statistica spendibili a un Personaggio Giocante.
     *
     * @param nomePersonaggio Il nome del personaggio destinatario.
     * @param quantitaPunti   La quantità di punti da assegnare.
     * @throws Exception Se si verifica un errore.
     */
    public void assegnaPuntiStatistica(String nomePersonaggio, int quantitaPunti) throws Exception {
        // In futuro: aggiorneremo il saldo dei punti spendibili del PG nel DB
        System.out.println("Assegnati " + quantitaPunti + " punti a " + nomePersonaggio + " (Simulazione)"); //per ora
    }


    /**
     * Crea un nuovo Personaggio Non Giocante (PnG) assegnandogli statistiche.
     *
     * @param nomePnG Il nome del PnG.
     * @param razza   Il nome testuale della razza.
     * @throws NomeMancantePngException Se il nome del PnG è vuoto.
     */
    public void creaPnG(String nomePnG, String razza) throws NomeMancantePngException {
        if (nomePnG == null || nomePnG.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        // in futuro lo salveremo nel DB
        System.out.println("Nuovo PnG creato: " + nomePnG + " (Simulazione)");//per ora
    }

    /**
     * Rimuove un Personaggio Non Giocante (PnG) dal sistema.
     *
     * @param nomePnG Il nome del PnG da eliminare.
     * @throws PngNonSelezionatoException Se il nome specificato è nullo o vuoto.
     */
    public void rimuoviPnG(String nomePnG) throws PngNonSelezionatoException {
        if (nomePnG == null || nomePnG.trim().isEmpty()) {
            throw new PngNonSelezionatoException("Seleziona un PnG da rimuovere.");
        }
        System.out.println("PnG rimosso: " + nomePnG + " (Simulazione)"); //per ora
    }


    /**
     * Modifica lo stato interno di una campagna (es. da "In Corso" a "Conclusa").
     *
     * @param nomeCampagna Il nome della campagna.
     * @param nuovoStato   La stringa o flag del nuovo stato operativo.
     * @throws Exception In caso di errori.
     */
    public void cambiaStatoCampagna(String nomeCampagna, String nuovoStato) throws Exception {
        // Dao cambia o in Corso o Finita
        // Una volta "In Corso", i giocatori non potranno più iscriversi.
        System.out.println("Lo stato della campagna '" + nomeCampagna + "' è ora: " + nuovoStato + " (Simulazione)");// per ora
    }


    /**
     * Consente a un giocatore di spendere i propri punti per aumentare le statistiche.
     *
     * @param statistica Il nome dell'attributo da incrementare.
     * @throws StatisticaNonSelezionataException Se non viene specificato l'attributo.
     */
    public void aumentaStatistica(String statistica) throws StatisticaNonSelezionataException {
        if (statistica == null || statistica.trim().isEmpty()) {
            throw new StatisticaNonSelezionataException("Inserisci una statistica valida.");
        }
        // Dao fa controllo per vedere se ha i punti da spendere
        System.out.println("Statistica '" + statistica + "' aumentata con successo! (Simulazione)");//per ora
    }

    /**
     * Esegue la transazione  per far acquistare un oggetto a un personaggio.
     *
     * @param nomeOggetto Il nome dell'oggetto da acquistare.
     * @throws OggettoNonSelezionatoException Se l'oggetto specificato non è valido.
     */
    public void compraOggetto(String nomeOggetto) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Inserisci un oggetto valido.");
        }
        // Dao controlla se PG esiste e ha abbastanza oro
        System.out.println("Oggetto '" + nomeOggetto + "' acquistato! (Simulazione)");//per ora
    }

    /**
     * Inizializza un nuovo Personaggio Giocante (PG) associandolo alla campagna e al giocatore.
     *
     * @param nome         Il nome del personaggio.
     * @param razza        La razza scelta.
     * @param classe       La classe scelta (determina abilità ed equipaggiamento base).
     * @param nomeCampagna La campagna in cui il PG opererà.
     * @throws NomePgNonValidoException Se il nome del PG non è valido.
     */
    public void creaNuovoPersonaggio(String nome, String razza, String classe, String nomeCampagna) throws NomePgNonValidoException {
        if (nome == null || nome.isEmpty()) {
            throw new NomePgNonValidoException("Nome non valido.");
        }

        // 1. Il DAO recupera l'oggetto 'Razza' e l'oggetto 'Classe' dal database basandosi sui nomi in stringa.
        // controller fa new personaggio e passa al dao

        System.out.println("Creato nuovo PG: " + nome + " | Razza: " + razza + " | Classe: " + classe + " (Simulazione)");//per ora
    }

    /**
     * Crea un Personaggio Non Giocante (PnG) standard gestito dal Master.
     *
     * @param nome         Il nome del PnG.
     * @param razza        La razza del PnG.
     * @param classe       La classe del PnG.
     * @param nomeCampagna La campagna di appartenenza.
     * @throws NomeMancantePngException Se il nome è mancante.
     */
    public void creaPnGBase(String nome, String razza, String classe, String nomeCampagna) throws NomeMancantePngException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        // In futuro  costruttore base Master.creaPersonaggio(classe, razza, nome)
        System.out.println("Creato PnG BASE: " + nome + " | Razza: " + razza + " | Classe: " + classe);
    }

    /**
     * Crea un Personaggio Non Giocante (PnG) avanzato andando a definire i campi  oro e punti statistica.
     *
     * @param nome         Il nome del PnG.
     * @param razza        La razza del PnG.
     * @param classe       La classe del PnG.
     * @param oro          La quantità di oro iniziale.
     * @param punti        I punti statistica disponibili fin dall'inizio.
     * @param nomeCampagna La campagna di appartenenza.
     * @throws NomeMancantePngException Se il nome è mancante.
     */
    public void creaPnGAvanzato(String nome, String razza, String classe, int oro, int punti, String nomeCampagna) throws NomeMancantePngException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        // In futuroil costruttore Master.creaPersonaggio(classe, razza, stat, nome, oro, punti)
        System.out.println("Creato PnG AVANZATO: " + nome + " | Oro: " + oro + " | Punti: " + punti);
    }

    /**
     * Aggiorna e salva le nuove statistiche di un PG o PnG nel database.
     *
     * @param nomePersonaggio Il nome dell'entità da aggiornare.
     * @param forza           Il nuovo valore della statistica Forza.
     * @param destrezza       Il nuovo valore della statistica Destrezza.
     * @param costituzione    Il nuovo valore della statistica Costituzione.
     * @param intelligenza    Il nuovo valore della statistica Intelligenza.
     * @param fede            Il nuovo valore della statistica Fede.
     * @param carisma         Il nuovo valore della statistica Carisma.
     * @param fortuna         Il nuovo valore della statistica Fortuna.
     * @param hpMax           I nuovi Punti Vita (HP) massimi.
     * @param manaMax         I nuovi punti Mana massimi.
     * @throws PngNonSelezionatoException Se il personaggio non è stato selezionato correttamente.
     */
    public void salvaStatisticheModificate(String nomePersonaggio, int forza, int destrezza, int costituzione,
                                           int intelligenza, int fede, int carisma, int fortuna,
                                           int hpMax, int manaMax) throws PngNonSelezionatoException {

        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PngNonSelezionatoException("Nessun personaggio selezionato.");
        }


        //  Il DAO recupererà l'oggetto Personaggio dal database
        //  Controller prende il PG dal DAO, userà i setter e DAO fa l'UPDATE nel database.

        System.out.println("Statistica aggiornate per il PG: " + nomePersonaggio + " (Simulazione)"); //per ora
    }


    /**
     * Gestisce la logica per far equipaggiare un oggetto a un personaggio,
     * validandone i requisiti minimi di statistiche.
     *
     * @param nomeOggetto  Il nome dell'oggetto da indossare/impugnare.
     * @param nomeCampagna La campagna per la ricerca dell'entità.
     * @throws OggettoNonSelezionatoException Se l'oggetto indicato non è valido.
     */
    public void equipaggiaOggetto(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da equipaggiare.");
        }
        // Dao fa:
        // boolean successo = pg.equipaggia((OggettoEquipaggiabile) oggetto);
        // se successo == false, lanceremo un'eccezione "Statistica insufficienti!"

        System.out.println("Equipaggiato l'oggetto: " + nomeOggetto + " (Simulazione)");//per ora
    }

    /**
     * /**
     * Rimuove un oggetto dall'equipaggiamento attivo del personaggio,
     * riponendolo nell'inventario.
     *
     * @param nomeOggetto  L'oggetto da disequipaggiare.
     * @param nomeCampagna La campagna di riferimento.
     * @throws OggettoNonSelezionatoException Se non è stato selezionato un oggetto valido.
     */
    public void rimuoviEquipaggiamento(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da rimuovere.");
        }
        // In futuro: pg.rimuoviEquipaggiamento((OggettoEquipaggiabile) oggetto);

        System.out.println("Rimosso l'equipaggiamento: " + nomeOggetto + " (Simulazione)");//per ora
    }

    /**
     * Consuma un oggetto dall'inventario per applicarne i benefici eliminandolo al termine dell'uso.
     *
     * @param nomeOggetto  Il consumabile da utilizzare.
     * @param nomeCampagna La campagna di riferimento.
     * @throws OggettoNonSelezionatoException Se l'oggetto non è stato selezionato.
     */
    public void usaConsumabile(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un consumabile da usare.");
        }
        // In futuro: pg.usaConsumabile((OggettoConsumabile) oggetto);
        // Questo ripristinerà HP o Mana

        System.out.println("Hai utilizzato: " + nomeOggetto + " (Simulazione)");//per ora
    }

    /**
     * Gestisce la vendita di un oggetto posseduto, restituendo l'oro rimanente dalla vendita.
     *
     * @param nomeOggetto  L'oggetto da vendere.
     * @param nomeCampagna La campagna di riferimento.
     * @throws Exception Se l'oggetto selezionato è nullo o vuoto.
     */
    public void vendiOggetto(String nomeOggetto, String nomeCampagna) throws Exception {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new Exception("Seleziona un oggetto da vendere.");
        }
        // In futuro: pg.vendiOggetto(oggetto);

        System.out.println("Venduto al mercante: " + nomeOggetto + " (Simulazione)");//per ora
    }

    /**
     * Sblocca una nuova abilità per il personaggio, vincolata alla sua classe d'appartenenza.
     *
     * @param nomeAbilita  L'abilità da far apprendere.
     * @param nomeCampagna La campagna in cui avviene l'azione.
     */
    public void imparaAbilita(String nomeAbilita, String nomeCampagna) {
        System.out.println("Appresa abilità: " + nomeAbilita);
    }

    public Utente cercaUtente(String username, String email, String password){
        Utente utenteTrovato = null;
        for(Utente utente : listaUtenti) {
            if (Objects.equals(utente.getUsername(), username) &&
                    Objects.equals(utente.getEmail(), email) &&
                    Objects.equals(utente.getPassword(), password)) {
                utenteTrovato = utente;
            }
        }
        return utenteTrovato;
    }

    public Map<Campagna, Master> getListaCampagne() {
        return Collections.unmodifiableMap(listaCampagne);
    }

    public void leggiListaCampagne() {
        campagnaDAO.leggiCampagne(listaCampagne);
    }

    public Campagna cercaCampagna(String nomeCampagna){
        Campagna campagnaTrovata = null;
        for(Campagna campagna : listaCampagne.keySet()){
            if(Objects.equals(nomeCampagna, campagna.getNome())){
                campagnaTrovata = campagna;
                break;
            }
        }
        return campagnaTrovata;
    }

    public Campagna getCampagnaAttiva() {
        return campagnaAttiva;
    }
}




