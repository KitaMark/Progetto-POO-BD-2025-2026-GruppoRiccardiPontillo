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
 * * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class Controller {
    /**
     * L'utente (Giocatore o Master) attualmente autenticato nel sistema.
     * Mantiene la sessione attiva durante l'utilizzo dell'applicazione.
     */
    private Utente utenteAttivo;
    private ArrayList<Utente> listaUtenti;
    private UtenteDAO utenteDAO;
    private HashMap<Campagna, Master> listaCampagne;
    private MasterDAO masterDAO;
    private CampagnaDAO campagnaDAO;
    private Campagna campagnaAttiva;
    private GiocatoreDao giocatoreDAO;
    private InventarioDao inventarioDAO;
    private AbilitaDao abilitaDao;
    private StatisticaDAO personaggioDAO;

    public Controller() {
        utenteAttivo = null;
        listaUtenti = new ArrayList<>();
        utenteDAO = new ImplementazionePostgresUtente();
        utenteDAO.leggiUtenti(listaUtenti);
        listaCampagne = new HashMap<>();
        masterDAO = new ImplementazionePostgresMaster();
        campagnaDAO = new ImplementazionePostgresCampagna();
        campagnaDAO.leggiCampagne(listaCampagne);
        giocatoreDAO = new ImplementazionePostgresGiocatore();
        inventarioDAO = new ImplementazionePostgresInventario();
        abilitaDao= new ImplementazionePostgresAbilita();
        personaggioDAO = new ImplementazionePostgresStatistica();
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
        if(isMaster && utenteTrovato instanceof Giocatore) throw new AutenticazioneException("L'account è registered come Giocatore!");
        if(!isMaster && utenteTrovato instanceof Master) throw new AutenticazioneException("L'account è registered come Master!");

        if (utenteTrovato instanceof Giocatore) {
            Giocatore giocatore = (Giocatore) utenteTrovato;
            System.out.println("DEBUG: L'ID del giocatore " + giocatore.getUsername() + " è: " + giocatore.getId());
            giocatore.setListaPartecipazioni(giocatoreDAO.caricaTutteLePartecipazioni(giocatore.getId()));
        } else if (utenteTrovato instanceof Master) {
            Master m = (Master) utenteTrovato;
        }

        this.utenteAttivo = utenteTrovato;
        return this.utenteAttivo;
    }

    /**
     * Registra un nuovo account utente all'interno del sistema, definendone il ruolo.
     * <p>
     * Effettua la sincronizzazione atomica catturando l'ID generato dal database
     * e assegnandolo all'oggetto transiente prima dell'inserimento in lista.
     * </p>
     *
     * @param username Lo pseudonimo scelto dall'utente.
     * @param password La password per l'autenticazione.
     * @param email    L'indirizzo email di contatto.
     * @param isMaster {@code true} se l'utente si sta registrando come Master, {@code false} come Giocatore.
     * @throws DatiMancantiException Se uno dei parametri di registrazione è vuoto.
     * @throws AutenticazioneException Se l'username o l'email sono già in uso.
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

        // AGGIUNTA: Sincronizzazione immediata dell'ID Utente generato dal database
        int idUtente = utenteDAO.aggiungiUtente(nuovoUtente);
        nuovoUtente.setId(idUtente);

        listaUtenti.add(nuovoUtente);
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
     * <p>
     * Sincronizza l'identificativo della campagna restituito dal DAO per evitare
     * incoerenze di Entity Identity all'interno della HashMap delle campagne.
     * </p>
     *
     * @param nomeCampagna Il nome identificativo della campagna.
     * @param maxGiocatori Il limite massimo di giocatori ammessi.
     * @throws CampagnaAttivaEsistenteException Se il Master ha già una campagna in corso.
     * @throws NomeMancanteCampagnaException    Se il nome della campagna è nullo o vuoto.
     * @throws NomeCampagnaInUsoException       Se il nome scelto è già registrato nel DB.
     */
    public void creaCampagna(String nomeCampagna, int maxGiocatori) throws CampagnaAttivaEsistenteException, NomeMancanteCampagnaException, NomeCampagnaInUsoException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Il nome della campagna non può essere vuoto.");
        }
        if(listaCampagne.containsValue((Master) utenteAttivo)) throw new CampagnaAttivaEsistenteException("Hai già una campagna attiva. Concludila prima di crearne una nuova.");
        if(listaCampagne.containsKey(cercaCampagna(nomeCampagna))) throw new NomeCampagnaInUsoException("Nome già in uso.");

        Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, (Master) utenteAttivo);

        // AGGIUNTA: Ricezione dell'ID autogenerato e abbinamento sicuro nella mappa
        int idCampagna = campagnaDAO.creaCampagna(campagna);
        campagna.setId(idCampagna);

        listaCampagne.put(campagna, (Master) utenteAttivo);
    }

    /**
     * Elimina definitivamente una campagna dal sistema.
     *
     * @param nomeCampagna Il nome della campagna da rimuovere.
     * @throws DatiMancantiException Se si verifica un errore durante l'eliminazione dal database.
     * @return {@code true} se viene eliminata correttamente.
     */
    public boolean eliminaCampagna(String nomeCampagna) throws DatiMancantiException, Exception {
        Campagna campagnaTarget = cercaCampagna(nomeCampagna);
        if(campagnaTarget == null) throw new DatiMancantiException("Campagna non esistente.");
        if(!controllaPrivilegiMaster(campagnaTarget)) throw new Exception("Operazione non autorizzata: non sei il proprietario di questa campagna.");
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
     * @return {@code true} se l'accesso alla scheda del personaggio è immediato, {@code false} se il personaggio deve essere creato.
     * @throws DatiMancantiException Se il nome fornito non è valido.
     * @throws RuntimeException Se non è possibile accedere alla campagna.
     */
    public boolean visualizzaCampagna(String nomeCampagna) throws DatiMancantiException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new DatiMancantiException("Nome della campagna non valido.");
        }
        this.campagnaAttiva = cercaCampagna(nomeCampagna);
        if (campagnaAttiva == null) throw new RuntimeException("Campagna non esistente.");
        campagnaDAO.leggiListaPersonaggi(campagnaAttiva.getListaPG(), true, campagnaAttiva.getNome());
        campagnaDAO.leggiListaPersonaggi(campagnaAttiva.getListaPnG(), false, campagnaAttiva.getNome());
        campagnaDAO.leggiCatalogoOggetti(campagnaAttiva.getCatalogoOggetti(), campagnaAttiva.getId());
        campagnaDAO.leggiListaClassi(campagnaAttiva.getListaClassi(), campagnaAttiva.getId());
        campagnaDAO.leggiListaRazze(campagnaAttiva.getListaRazze(), campagnaAttiva.getId());
        campagnaDAO.leggiGiocatori(campagnaAttiva.getPartecipanti(), campagnaAttiva.getNome());

        if (utenteAttivo instanceof Giocatore) {
            try {
                Giocatore giocatore = (Giocatore) utenteAttivo;
                Personaggio pg = null;

                if (giocatore.getListaPartecipazioni() != null) {
                    for (Campagna c : giocatore.getListaPartecipazioni().keySet()) {
                        if (c.getNome().equalsIgnoreCase(campagnaAttiva.getNome())) {
                            pg = giocatore.getListaPartecipazioni().get(c);
                            break;
                        }
                    }
                }

                if (pg != null) {
                    if (pg.getInventarioConsumabili() == null || pg.getInventarioEquipaggiabili() == null) {
                        System.err.println("ATTENZIONE: Le HashMap in Personaggio sono rimaste a null!");
                    } else {
                        aggiornaZainoInMemoria(pg);
                        abilitaDao.caricaAbilitaSbloccabili(pg.getClasse());
                        abilitaDao.caricaAbilitaApprese(pg);
                    }
                    return true; // AGGIUNTA: Personaggio esistente, plancia sbloccata
                } else {
                    return false; // AGGIUNTA: Personaggio assente, reindirizzamento al form di creazione
                }
            } catch (Exception e) {
                System.err.println("==================================================");
                System.err.println("ERRORE DURANTE IL CARICAMENTO DELLO ZAINO DA DB:");
                e.printStackTrace();
                System.err.println("==================================================");
            }
        }
        return true;
    }

    /**
     * Recupera l'elenco delle campagne associate all'utente attualmente loggato.
     *
     * @return Una lista di oggetti {@link Campagna}.
     */
    public List<Campagna> getCampagne() {
        return new ArrayList<>();
    }

    /**
     * Permette a un Giocatore di iscriversi a una campagna non ancora iniziata.
     * <p>
     * Esegue l'inserimento a livello di database e aggiorna immediatamente lo stato
     * in RAM del giocatore immettendo la chiave con riferimento personaggio a {@code null}.
     * </p>
     *
     * @param nomeCampagna Il nome della campagna a cui partecipare.
     * @throws NomeMancanteCampagnaException Se il nome fornito non è valido.
     */
    public void iscrivitiCampagna(String nomeCampagna) throws NomeMancanteCampagnaException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new NomeMancanteCampagnaException("Nome della campagna non valido.");
        }

        Campagna campagnaIscrizione = cercaCampagna(nomeCampagna);
        if (campagnaIscrizione == null) {
            throw new NomeMancanteCampagnaException("La campagna inserita non esiste.");
        }

        try {
            giocatoreDAO.iscrivitiACampagna(utenteAttivo.getId(), campagnaIscrizione.getId());

            // AGGIUNTA: Sincronizzazione strutturale della memoria RAM senza ricorrere al logout
            Giocatore giocatore = (Giocatore) utenteAttivo;
            giocatore.addPartecipazioneDati(campagnaIscrizione, null);

            if (campagnaIscrizione.getPartecipanti() != null) {
                campagnaIscrizione.getPartecipanti().add(giocatore);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String messaggioErrore = (e.getMessage() == null) ? "Errore imprevisto nella gestione della memoria." : e.getMessage();
            throw new RuntimeException("Impossibile completare l'iscrizione: " + messaggioErrore);
        }
    }

    /**
     * Rimuove un Personaggio Giocante (PG) dalla Campagna gestita dal Master.
     *
     * @param nomePersonaggio Il nome del PG da rimuovere.
     * @param nomeProprietario Il nome del Giocatore che interpreta il PG.
     * @throws DatiMancantiException Se non viene specificato alcun personaggio o proprietario.
     * @throws PersonaggioNonTrovatoException Se la ricerca del PG fallisce.
     */
    public void rimuoviPGdaCampagna(String nomePersonaggio, String nomeProprietario) throws PersonaggioNonTrovatoException, DatiMancantiException {
        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new DatiMancantiException("Seleziona un personaggio da rimuovere.");
        }
        if(nomeProprietario == null || nomeProprietario.trim().isEmpty() || Objects.equals(nomeProprietario, "Sconosciuto"))
            throw new DatiMancantiException("ATTENZIONE: personaggio non associato a nessun giocatore.");

        Personaggio daRimuovere = cercaPg(nomePersonaggio, nomeProprietario);

        masterDAO.rimuoviPersonaggio(daRimuovere);
        campagnaAttiva.getListaPG().remove(daRimuovere);
    }

    private Personaggio cercaPg(String nomePersonaggio, String nomeProprietario) throws PersonaggioNonTrovatoException {
        Personaggio personaggio = null;
        for (Giocatore giocatore : campagnaAttiva.getPartecipanti()) {
            if (giocatore.getUsername().equals(nomeProprietario)) {
                personaggio = giocatore.getPersonaggioInCampagna(campagnaAttiva);
                break;
            }
        }
        if (personaggio == null) throw new PersonaggioNonTrovatoException("personaggio non esistente.");
        return personaggio;
    }

    public void modificaStatistichePG(String nomePersonaggio) throws Exception {
        System.out.println("Apertura finestra di modifica statistiche per: " + nomePersonaggio);
    }

    public void assegnaPuntiStatistica(String nomePersonaggio, String nomeProprietario, int quantitaPunti) throws PersonaggioNonTrovatoException, RuntimeException {
        if(quantitaPunti < 0) throw new RuntimeException("Non è possibile assegnare valori negativi.");
        Personaggio personaggio = cercaPg(nomePersonaggio, nomeProprietario);
        personaggio.addPuntiStatistica(quantitaPunti);
        masterDAO.assegnaPuntiStatistica(personaggio, quantitaPunti);
    }

    public void creaPnG(String nomePnG, String razza) throws NomeMancantePngException {
        if (nomePnG == null || nomePnG.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        System.out.println("Nuovo PnG creato: " + nomePnG + " (Simulazione)");
    }

    /**
     * Rimuove un Personaggio Non Giocante (PnG) dal sistema.
     *
     * @param id L'identificativo del PnG da eliminare.
     * @throws DatiMancantiException Se l'id non esiste nel database.
     */
    public void rimuoviPnG(int id) throws DatiMancantiException {
        Personaggio daTrovare = null;
        for(Personaggio png : campagnaAttiva.getListaPnG()){
            if(png.getId() == id){
                daTrovare = png;
                break;
            }
        }
        if(daTrovare == null) throw new DatiMancantiException("Id non esistente, impossibile trovare il png.");
        masterDAO.rimuoviPersonaggio(daTrovare);
        campagnaAttiva.getListaPnG().remove(daTrovare);
    }

    public void cambiaStatoCampagna(String nomeCampagna, String nuovoStato) throws Exception {
        System.out.println("Lo stato della campagna '" + nomeCampagna + "' è ora: " + nuovoStato + " (Simulazione)");
    }

    public void aumentaStatistica(String statistica) throws StatisticaNonSelezionataException {
        if (statistica == null || statistica.trim().isEmpty()) {
            throw new StatisticaNonSelezionataException("Inserisci una statistica valida.");
        }
        System.out.println("Statistica '" + statistica + "' aumentata con successo! (Simulazione)");
    }

    /**
     * Esegue la transazione per far acquistare un oggetto a un personaggio.
     *
     * @param nomeOggetto Il nome dell'oggetto da acquistare.
     * @throws OggettoNonSelezionatoException Se l'oggetto specificato non è valido.
     */
    public void compraOggetto(String nomeOggetto) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Inserisci un oggetto valido.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        List<Oggetto> catalogo = inventarioDAO.caricaCatalogoNegozio();
        Oggetto oggettoScelto = null;

        for (Oggetto oggetto : catalogo) {
            if (oggetto.getNome().equalsIgnoreCase(nomeOggetto)) {
                oggettoScelto = oggetto;
                break;
            }
        }

        if (oggettoScelto == null) throw new OggettoNonSelezionatoException("Oggetto non trovato in negozio.");
        if (pg.getOro() < oggettoScelto.getCosto()) throw new OggettoNonSelezionatoException("Oro insufficiente.");

        inventarioDAO.acquistaOggetto(pg.getId(), oggettoScelto.getId(), oggettoScelto.getCosto());
        pg.setOro(pg.getOro() - oggettoScelto.getCosto());

        if (oggettoScelto instanceof OggettoConsumabile) {
            pg.addConsumabile((OggettoConsumabile) oggettoScelto, 1);
        } else if (oggettoScelto instanceof OggettoEquipaggiabile) {
            pg.addEquipaggiabile((OggettoEquipaggiabile) oggettoScelto);
        }
    }

    /**
     * Inizializza un nuovo Personaggio Giocante (PG) associandolo alla campagna e al giocatore.
     * <p>
     * Cattura l'ID univoco autogenerato restituito dall'operazione di inserimento del DAO
     * e lo assegna all'entità per salvaguardare la logica relazionale delle mappe in RAM.
     * </p>
     *
     * @param nome         Il nome del personaggio.
     * @param razza        La razza scelta.
     * @param classe       La classe scelta.
     * @param nomeCampagna La campagna in cui il PG opererà.
     * @throws NomePgNonValidoException Se il nome del PG non è valido.
     */
    public void creaNuovoPersonaggio(String nome, String razza, String classe, String nomeCampagna) throws NomePgNonValidoException {
        if (nome == null || nome.isEmpty()) {
            throw new NomePgNonValidoException("Nome non valido.");
        }
        Campagna campagnaIscrizione = cercaCampagna(nomeCampagna);

        Classe classeScelta = new Classe(classe);
        Razza razzaScelta = new Razza(razza);

        Personaggio nuovoPg = new Personaggio(classeScelta, razzaScelta, nome);

        try {
            // AGGIUNTA: Cattura dell'ID generato dal DB e assegnazione immediata all'oggetto in RAM
            int idGenerato = giocatoreDAO.salvaPersonaggio(nuovoPg, utenteAttivo.getId(), campagnaIscrizione.getId());
            nuovoPg.setId(idGenerato);

            Giocatore giocatore = (Giocatore) utenteAttivo;
            giocatore.addPartecipazioneDati(campagnaIscrizione, nuovoPg);
            campagnaIscrizione.getListaPG().add(nuovoPg);

            System.out.println("Personaggio '" + nome + "' creato con successo! ID DB: " + nuovoPg.getId());

        } catch (RuntimeException e) {
            throw new RuntimeException("Errore durante la creazione del personaggio: " + e.getMessage());
        }
    }

    /**
     * Crea un Personaggio Non Giocante (PnG) standard gestito dal Master.
     *
     * @param nome         Il nome del PnG.
     * @param razza        La razza del PnG.
     * @param classe       La classe del PnG.
     * @throws DatiMancantiException Se ci sono parametri null.
     */
    public void creaPnG(String nome, Razza razza, Classe classe) throws DatiMancantiException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DatiMancantiException("nome PnG non valido.");
        }
        if(razza == null || classe == null) throw new DatiMancantiException("Devi selezionare razza e classe prima di procedere.");
        Personaggio png = new Personaggio(classe, razza, nome);
        png.setId(masterDAO.creaPnG(png));
        campagnaAttiva.getListaPnG().add(png);
    }

    /**
     * Crea un Personaggio Non Giocante (PnG) andando a definire anche i campi  oro e punti statistica.
     *
     * @param nome         Il nome del PnG.
     * @param razza        La razza del PnG.
     * @param classe       La classe del PnG.
     * @param oro          La quantità di oro iniziale.
     * @param punti        I punti statistica disponibili fin dall'inizio.
     * @throws DatiMancantiException se i dati non sono validi.
     */
    public void creaPnG(String nome, Razza razza, Classe classe, int oro, int punti, Statistica statBase) throws DatiMancantiException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DatiMancantiException("nome PnG non valido.");
        }
        if(razza == null || classe == null) throw new DatiMancantiException("Devi selezionare razza e classe prima di procedere.");
        Personaggio png = new Personaggio(classe, razza, statBase, nome, oro, punti);
        png.setId(masterDAO.creaPnG(png));
        campagnaAttiva.getListaPnG().add(png);
    }

    /**
     * Aggiorna e salva le nuove statistiche di un PG o PnG nel database.
     * @param idPersonaggio   L'identificativo univoco del personaggio.
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
    public void salvaStatisticheModificate(String nomePersonaggio, int idPersonaggio, int forza, int destrezza, int costituzione,
                                           int intelligenza, int fede, int carisma, int fortuna,
                                           int hpMax, int manaMax, boolean isPg) throws PngNonSelezionatoException {

        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PngNonSelezionatoException("Nessun personaggio selezionato.");
        }
        Personaggio daModificare = null;
        ArrayList<Personaggio> listaPersonaggi = isPg? campagnaAttiva.getListaPG() : campagnaAttiva.getListaPnG();
        for(Personaggio pg : listaPersonaggi){
            if(pg.getId() == idPersonaggio){
                daModificare = pg;
                break;
            }
        }
        if(daModificare == null) throw new PersonaggioNonTrovatoException("Impossibile trovare il personaggio selezionato.");
        Statistica modifiche = new Statistica(costituzione, forza, destrezza, intelligenza, fede, carisma, fortuna, hpMax, manaMax);
        daModificare.setHpCorrenti(hpMax);
        daModificare.setManaCorrente(manaMax);
        personaggioDAO.aggiornaStatistichePersonaggio(daModificare.getId(), modifiche);
        daModificare.setStatisticaBase(modifiche);
    }

    public void equipaggiaOggetto(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da equipaggiare.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoEquipaggiabile target = null;
        for (OggettoEquipaggiabile e : pg.getInventarioEquipaggiabili().keySet()) {
            if (e.getNome().equalsIgnoreCase(nomeOggetto)) {
                target = e;
                break;
            }
        }

        if (target == null) throw new OggettoNonSelezionatoException("Non possiedi questo equipaggiamento.");

        try {
            inventarioDAO.impostaEquipaggiamento(pg.getId(), target.getId(), true);
            pg.impostaStatoEquipaggiabile(target, true);
        } catch (RuntimeException e) {
            throw new OggettoNonSelezionatoException("Requisiti insufficienti: " + e.getMessage());
        }
    }

    public void rimuoviEquipaggiamento(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da rimuovere.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoEquipaggiabile target = null;
        for (OggettoEquipaggiabile e : pg.getInventarioEquipaggiabili().keySet()) {
            if (e.getNome().equalsIgnoreCase(nomeOggetto)) {
                target = e;
                break;
            }
        }

        if (target != null && pg.getInventarioEquipaggiabili().get(target)) {
            inventarioDAO.impostaEquipaggiamento(pg.getId(), target.getId(), false);
            pg.rimuoviEquipaggiamento(target);
        }
    }

    public void usaConsumabile(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona una pozione da usare.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoConsumabile target = null;
        for (OggettoConsumabile c : pg.getInventarioConsumabili().keySet()) {
            if (c.getNome().equalsIgnoreCase(nomeOggetto)) {
                target = c;
                break;
            }
        }

        if (target == null) {
            throw new OggettoNonSelezionatoException("Non possiedi questo consumabile nel tuo inventario.");
        }

        inventarioDAO.consumaOggetto(pg.getId(), target.getId());
        pg.ripristinaHP(target.getRipristinoHP());
        pg.ripristinaMana(target.getRipristinoMana());
        giocatoreDAO.aggiornaRisorse(pg);
        pg.rimuoviConsumabile(target, 1);
    }

    public void vendiOggetto(String nomeOggetto, String nomeCampagna) throws Exception {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new Exception("Seleziona un oggetto da vendere.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoConsumabile targetConsumabile = null;
        OggettoEquipaggiabile targetEquipaggiabile = null;

        for (OggettoConsumabile c : pg.getInventarioConsumabili().keySet()) {
            if (c.getNome().equalsIgnoreCase(nomeOggetto)) {
                targetConsumabile = c;
                break;
            }
        }

        if (targetConsumabile == null) {
            for (OggettoEquipaggiabile e : pg.getInventarioEquipaggiabili().keySet()) {
                if (e.getNome().equalsIgnoreCase(nomeOggetto)) {
                    if (pg.getInventarioEquipaggiabili().get(e)) {
                        throw new Exception("Non puoi vendere un oggetto attualmente equipaggiato!");
                    }
                    targetEquipaggiabile = e;
                    break;
                }
            }
        }

        if (targetConsumabile == null && targetEquipaggiabile == null) {
            throw new Exception("Non possiedi questo oggetto.");
        }

        Oggetto target = (targetConsumabile != null) ? targetConsumabile : targetEquipaggiabile;
        int ricavo = target.getCosto() / 2;

        inventarioDAO.vendiOggetto(pg.getId(), target.getId(), ricavo);
        pg.setOro(pg.getOro() + ricavo);

        if (targetConsumabile != null) {
            pg.rimuoviConsumabile(targetConsumabile, 1);
        } else {
            pg.rimuoviEquipaggiabile(targetEquipaggiabile);
        }
    }

    public void imparaAbilita(String nomeAbilita, String nomeCampagna) throws AbilitaNonSelezionataException, AbilitaGiaAppresaException, AbilitaNonSbloccabileException {
        if (nomeAbilita == null || nomeAbilita.trim().isEmpty()) {
            throw new AbilitaNonSelezionataException("Seleziona un'abilità valida dalla tabella.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        Abilita target = null;
        for (Abilita abilita : pg.getClasse().getAbilitaSbloccabili()) {
            if (abilita.getNome().equalsIgnoreCase(nomeAbilita)) {
                target = abilita;
                break;
            }
        }

        if (target == null) {
            throw new AbilitaNonSbloccabileException("Questa abilità non è prevista per la tua classe.");
        }
        if (pg.getListaAbilita().contains(target)) {
            throw new AbilitaGiaAppresaException("Hai già appreso questa abilità in precedenza!");
        }

        abilitaDao.imparaAbilita(pg.getId(), target.getNome());
        pg.addAbilita(target);
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

    /**
     * Recupera il catalogo del negozio filtrato per la campagna attiva.
     * @return Una lista di Oggetti acquistabili in questa specifica campagna.
     */
    public List<Oggetto> getCatalogoNegozio() {
        List<Oggetto> catalogo = new ArrayList<>();
        if (campagnaAttiva != null) {
            campagnaDAO.leggiCatalogoOggetti(catalogo, campagnaAttiva.getId());
        }
        return catalogo;
    }

    public void leggiListaCampagne() {
        campagnaDAO.leggiCampagne(listaCampagne);
    }

    private void aggiornaZainoInMemoria(Personaggio pg) {
        pg.svuotaInventari();
        java.util.Map<Oggetto, Integer> zainoDalDB = inventarioDAO.caricaInventarioPersonaggio(pg.getId());

        for (java.util.Map.Entry<Oggetto, Integer> entry : zainoDalDB.entrySet()) {
            Oggetto oggetto = entry.getKey();
            int quantita = entry.getValue();

            if (oggetto instanceof OggettoConsumabile) {
                pg.addConsumabile((OggettoConsumabile) oggetto, quantita);
            } else if (oggetto instanceof OggettoEquipaggiabile) {
                pg.impostaStatoEquipaggiabile((OggettoEquipaggiabile) oggetto, oggetto.isEquipaggiato());
            }
        }
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

    /**
     * Recupera l'elenco delle Razze abilitate dal Master per una specifica campagna.
     *
     * @param nomeCampagna Il nome della campagna.
     * @return La lista di Razze disponibili.
     */
    public List<Razza> getRazzePerCampagna(String nomeCampagna) {
        Campagna c = cercaCampagna(nomeCampagna);
        List<Razza> razzeDisponibili = new ArrayList<>();
        if (c != null) {
            campagnaDAO.leggiListaRazze(razzeDisponibili, c.getId());
        }
        return razzeDisponibili;
    }

    /**
     * Recupera l'elenco delle Classi create dal Master per una specifica campagna.
     *
     * @param nomeCampagna Il nome della campagna.
     * @return La lista di Classi disponibili.
     */
    public List<Classe> getClassiPerCampagna(String nomeCampagna) {
        Campagna c = cercaCampagna(nomeCampagna);
        List<Classe> classiDisponibili = new ArrayList<>();
        if (c != null) {
            campagnaDAO.leggiListaClassi(classiDisponibili, c.getId());
        }
        return classiDisponibili;
    }



    public boolean controllaPrivilegiMaster(Campagna campagna){
        return utenteAttivo.equals((listaCampagne.get(campagna)));
    }
}