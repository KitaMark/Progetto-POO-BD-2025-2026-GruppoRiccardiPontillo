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
 *
 * @author Riccardi Carmine
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
    private PersonaggioDAO personaggioDAO;
    private RazzaDao razzaDao;
    private ClasseDao classeDao;
    private OggettoDao oggettoDao;

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
        abilitaDao = new ImplementazionePostgresAbilita();
        personaggioDAO = new ImplementazionePostgresPersonaggio();
        razzaDao = new ImplementazionePostgresRazza();
        classeDao = new ImplementazionePostgresClasse();
        oggettoDao = new ImplementazionePostgresOggetto();
    }


    // =========================================================================================
    // AUTENTICAZIONE E SESSIONE
    // =========================================================================================

    /**
     * Autentica un utente nel sistema verificandone le credenziali.
     *
     * @param username L'username dell'utente.
     * @param email    L'email associata all'account.
     * @param password La password di accesso.
     * @param isMaster Flag per determinare se il login richiesto è per il ruolo Master.
     * @return L'istanza dell'{@link Utente} (strutturata come Master o Giocatore) recuperata dal sistema.
     * @throws DatiMancantiException Se uno dei campi di testo risulta vuoto o se le credenziali non sono valide.
     * @throws AutenticazioneException Se si tenta di loggare con un ruolo errato rispetto a quello registrato.
     */
    public Utente faiLogin(String username, String email, String password, boolean isMaster) throws DatiMancantiException, AutenticazioneException {
        if (username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            throw new DatiMancantiException("Per favore, inserisci le tue credenziali (username, email e password) per accedere");
        }
        Utente utenteTrovato = cercaUtente(username, email, password);
        if (utenteTrovato == null) throw new DatiMancantiException("Credenziali non valide.");
        if (isMaster && utenteTrovato instanceof Giocatore) throw new AutenticazioneException("L'account è registrato come Giocatore!");
        if (!isMaster && utenteTrovato instanceof Master) throw new AutenticazioneException("L'account è registrato come Master!");

        if (utenteTrovato instanceof Giocatore) {
            Giocatore giocatore = (Giocatore) utenteTrovato;
            System.out.println("DEBUG: L'ID del giocatore " + giocatore.getUsername() + " è: " + giocatore.getId());
            giocatore.setListaPartecipazioni(giocatoreDAO.caricaTutteLePartecipazioni(giocatore.getId()));
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

        for (Utente utente : listaUtenti) {
            if (Objects.equals(utente.getUsername(), username)) {
                throw new AutenticazioneException("Username già in uso.");
            } else if (Objects.equals(utente.getEmail(), email)) {
                throw new AutenticazioneException("Email già in uso.");
            }
        }

        Utente nuovoUtente;
        if (isMaster) {
            nuovoUtente = new Master(email, username, password);
        } else {
            nuovoUtente = new Giocatore(email, username, password);
        }

        int idUtente = utenteDAO.aggiungiUtente(nuovoUtente);
        nuovoUtente.setId(idUtente);

        listaUtenti.add(nuovoUtente);
    }

    /**
     * Disconnette l'utente attivo, azzerando la sessione corrente.
     */
    public void logout() {
        this.utenteAttivo = null;
        System.out.println("Logout effettuato. Utente scollegato.");
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
     * Cerca un utente all'interno della lista caricata in memoria verificandone le credenziali.
     * Metodo di supporto privato alla fase di login.
     *
     * @param username Lo username dell'utente da cercare.
     * @param email L'email associata all'account.
     * @param password La password di autenticazione.
     * @return L'oggetto {@link Utente} se le credenziali corrispondono, altrimenti {@code null}.
     */
    private Utente cercaUtente(String username, String email, String password) {
        Utente utenteTrovato = null;
        for (Utente utente : listaUtenti) {
            if (Objects.equals(utente.getUsername(), username) &&
                    Objects.equals(utente.getEmail(), email) &&
                    Objects.equals(utente.getPassword(), password)) {
                utenteTrovato = utente;
            }
        }
        return utenteTrovato;
    }


    // =========================================================================================
    //GESTIONE CAMPAGNA
    // =========================================================================================

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
        if (listaCampagne.containsValue((Master) utenteAttivo)) throw new CampagnaAttivaEsistenteException("Hai già una campagna attiva. Concludila prima di crearne una nuova.");
        if (listaCampagne.containsKey(cercaCampagna(nomeCampagna))) throw new NomeCampagnaInUsoException("Nome già in uso.");

        Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, (Master) utenteAttivo);

        int idCampagna = campagnaDAO.creaCampagna(campagna);
        campagna.setId(idCampagna);

        listaCampagne.put(campagna, (Master) utenteAttivo);
    }

    /**
     * Elimina definitivamente una campagna dal sistema.
     *
     * @param nomeCampagna Il nome della campagna da rimuovere.
     * @return {@code true} se viene eliminata correttamente.
     * @throws DatiMancantiException Se la campagna non esiste.
     * @throws Exception Se l'operazione non è autorizzata.
     */
    public boolean eliminaCampagna(String nomeCampagna) throws DatiMancantiException, Exception {
        Campagna campagnaTarget = cercaCampagna(nomeCampagna);
        if (campagnaTarget == null) throw new DatiMancantiException("Campagna non esistente.");
        if (!controllaPrivilegiMaster(campagnaTarget)) throw new Exception("Operazione non autorizzata: non sei il proprietario di questa campagna.");
        listaCampagne.remove(campagnaTarget);
        campagnaDAO.eliminaCampagna(campagnaTarget);
        return true;
    }

    /**
     * Entra nell'unica campagna da lui gestita (Master), visualizza le campagne a cui è iscritto (Giocatore).
     *
     * @param nomeCampagna Il nome della campagna in cui entrare.
     * @return {@code true} se l'accesso alla scheda del personaggio è immediato, {@code false} se il personaggio deve essere creato.
     * @throws DatiMancantiException Se il nome fornito non è valido.
     * @throws RuntimeException Se non è possibile accedere alla campagna.
     */
    public boolean recuperaDatiCampagna(String nomeCampagna) throws DatiMancantiException {
        if (nomeCampagna == null || nomeCampagna.trim().isEmpty()) {
            throw new DatiMancantiException("Nome della campagna non valido.");
        }
        this.campagnaAttiva = cercaCampagna(nomeCampagna);
        if (campagnaAttiva == null) throw new RuntimeException("Campagna non esistente.");

        campagnaDAO.leggiListaPersonaggi(campagnaAttiva.getListaPG(), true, campagnaAttiva.getNome());
        campagnaDAO.leggiListaPersonaggi(campagnaAttiva.getListaPnG(), false, campagnaAttiva.getNome());

        campagnaAttiva.getCatalogoOggetti().clear();
        campagnaAttiva.getCatalogoOggetti().addAll(campagnaDAO.caricaCatalogoNegozio(campagnaAttiva.getId()));

        campagnaDAO.leggiListaClassi(campagnaAttiva.getListaClassi(), campagnaAttiva.getId());
        campagnaDAO.leggiListaRazze(campagnaAttiva.getListaRazze(), campagnaAttiva.getId());
        campagnaDAO.leggiGiocatori(campagnaAttiva.getPartecipanti(), campagnaAttiva.getNome());

        if (utenteAttivo instanceof Giocatore) {
            try {
                Giocatore giocatore = (Giocatore) utenteAttivo;
                Personaggio pg = null;

                if (giocatore.getListaPartecipazioni() != null) {
                    for (Campagna c : giocatore.getListaPartecipazioni().keySet()) {
                        if (c.getId() == campagnaAttiva.getId()) {
                            pg = giocatore.getListaPartecipazioni().get(c);
                            break;
                        }
                    }
                } else return false;

                if (pg == null) return false;
                if (pg.getInventarioConsumabili() == null || pg.getInventarioEquipaggiabili() == null) {
                    System.err.println("ATTENZIONE: Le HashMap in Personaggio sono rimaste a null!");
                } else {
                    leggiInventarioPersonaggio(pg);
                    abilitaDao.caricaAbilitaSbloccabili(pg.getClasse());
                    abilitaDao.caricaAbilitaApprese(pg);
                }
                return true;
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
     * Modifica lo stato della campagna attiva ("Non Iniziata" / "In Corso") per permettere o bloccare le iscrizioni.
     * Sincronizza il cambiamento sia in RAM che sul Database tramite il DAO.
     */
    public void cambiaStatoCampagna() {
        if (campagnaAttiva.isIniziata()) {
            campagnaDAO.cambiaStato(campagnaAttiva.getId(), false);
            campagnaAttiva.setIniziata(false);
        } else {
            campagnaDAO.cambiaStato(campagnaAttiva.getId(), true);
            campagnaAttiva.setIniziata(true);
        }
    }

    /**
     * Recupera l'istanza della campagna attualmente selezionata e in corso di esecuzione.
     *
     * @return La {@link Campagna} attiva nella sessione corrente.
     */
    public Campagna getCampagnaAttiva() {
        return campagnaAttiva;
    }

    /**
     * Restituisce la mappa completa delle campagne disponibili nel sistema e dei Master proprietari.
     *
     * @return Una Map non modificabile di tipo {@code Campagna -> Master}.
     */
    public Map<Campagna, Master> getListaCampagne() {
        return Collections.unmodifiableMap(listaCampagne);
    }

    /**
     * Ricerca una specifica campagna tramite il suo nome all'interno dei dati in RAM.
     *
     * @param nomeCampagna Il nome testuale della campagna da cercare.
     * @return La {@link Campagna} corrispondente, o {@code null} se inesistente.
     */
    public Campagna cercaCampagna(String nomeCampagna) {
        Campagna campagnaTrovata = null;
        for (Campagna campagna : listaCampagne.keySet()) {
            if (nomeCampagna.equalsIgnoreCase(campagna.getNome())) {
                campagnaTrovata = campagna;
                break;
            }
        }
        return campagnaTrovata;
    }



    /**
     * Verifica se l'utente attualmente connesso è il creatore e il Master della campagna selezionata.
     *
     * @param campagna La campagna da controllare.
     * @return {@code true} se l'utente attivo è il Master autorizzato per questa campagna, {@code false} altrimenti.
     */
    public boolean controllaPrivilegiMaster(Campagna campagna) {
        return utenteAttivo.equals((listaCampagne.get(campagna)));
    }


    // =========================================================================================
    //GIOCATORE — ISCRIZIONE E PERSONAGGIO
    // =========================================================================================

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
        if (campagnaIscrizione.isIniziata()) throw new RuntimeException("Impossibile completare l'iscrizione: campagna già iniziata.");

        try {
            if (!(campagnaDAO.contaPartecipanti(campagnaIscrizione.getId()) < campagnaIscrizione.getMaxGiocatori())) throw new RuntimeException("Impossibile completare l'iscrizione: limite partecipanti raggiunto");
            giocatoreDAO.iscrivitiACampagna(utenteAttivo.getId(), campagnaIscrizione.getId());

            Giocatore giocatore = (Giocatore) utenteAttivo;
            giocatore.addPartecipazioneDati(campagnaIscrizione, null);
            campagnaIscrizione.getPartecipanti().add(giocatore);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
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
     * @param campagna     La campagna in cui il PG opererà.
     * @throws NomePgNonValidoException Se il nome del PG non è valido.
     */
    public void creaNuovoPersonaggio(String nome, Razza razza, Classe classe, Campagna campagna) throws NomePgNonValidoException {
        if (nome == null || nome.isEmpty()) {
            throw new NomePgNonValidoException("Nome non valido.");
        }

        Personaggio nuovoPg = new Personaggio(classe, razza, nome, true);

        try {
            int idGenerato = giocatoreDAO.salvaPersonaggio(nuovoPg, utenteAttivo.getId(), campagna.getId());
            nuovoPg.setId(idGenerato);

            Giocatore giocatore = (Giocatore) utenteAttivo;
            giocatore.addPartecipazioneDati(campagna, nuovoPg);
            campagna.getListaPG().add(nuovoPg);

            System.out.println("Personaggio '" + nome + "' creato con successo! ID DB: " + nuovoPg.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException("Errore durante la creazione del personaggio: " + e.getMessage());
        }
    }

    /**
     * Consuma un punto statistica guadagnato per incrementare permanentemente
     * una caratteristica base del personaggio.
     *
     * @param nomeStatistica Il nome della statistica da potenziare.
     * @throws StatisticaNonSelezionataException Se non viene passata una statistica valida.
     * @throws RuntimeException Se i punti sono esauriti o la statistica non è potenziabile.
     */
    public void aumentaStatistica(String nomeStatistica) throws StatisticaNonSelezionataException, RuntimeException {
        if (nomeStatistica == null || nomeStatistica.trim().isEmpty()) {
            throw new StatisticaNonSelezionataException("Seleziona una statistica valida.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        if (pg.getPuntiStatistica() <= 0) {
            throw new RuntimeException("Non hai abbastanza Punti Statistica da spendere!");
        }

        Statistica base = pg.getStatisticheBase();

        switch (nomeStatistica) {
            case "Forza":
                base.setForza(base.getForza() + 1);
                break;
            case "Destrezza":
                base.setDestrezza(base.getDestrezza() + 1);
                break;
            case "Costituzione":
                base.setCostituzione(base.getCostituzione() + 1);
                break;
            case "Intelligenza":
                base.setIntelligenza(base.getIntelligenza() + 1);
                break;
            case "Fede":
                base.setFede(base.getFede() + 1);
                break;
            case "Carisma":
                base.setCarisma(base.getCarisma() + 1);
                break;
            case "Fortuna":
                base.setFortuna(base.getFortuna() + 1);
                break;
            default:
                throw new StatisticaNonSelezionataException("Non puoi usare i punti per potenziare: " + nomeStatistica);
        }

        pg.setPuntiStatistica(pg.getPuntiStatistica() - 1);
        personaggioDAO.aggiornaStatistichePersonaggio(pg.getId(), base);
        masterDAO.assegnaPuntiStatistica(pg.getId(), -1);
        pg.ricalcolaStatisticheFinali();
    }

    /**
     * Svuota l'inventario locale del personaggio in RAM per evitare inconsistenze e lo ripopola
     * leggendo i dati freschi direttamente dal database. Calcola automaticamente i ricalcoli delle statistiche finali.
     *
     * @param pg L'oggetto {@link Personaggio} da riallineare col database.
     * @throws RuntimeException Se il pg è nullo e corrotto in memoria.
     */
    public void leggiInventarioPersonaggio(Personaggio pg) {
        if (pg == null) throw new RuntimeException("Impossibile trovare il personaggio selezionato - possibile corruzione dei dati");
        pg.svuotaInventari();
        Map<Oggetto, Integer> zainoPersonaggio = inventarioDAO.caricaInventarioPersonaggio(pg.getId());

        for (Map.Entry<Oggetto, Integer> entry : zainoPersonaggio.entrySet()) {
            Oggetto oggetto = entry.getKey();
            int quantita = entry.getValue();

            if (oggetto instanceof OggettoConsumabile) {
                pg.addConsumabile((OggettoConsumabile) oggetto, quantita);
            } else if (oggetto instanceof OggettoEquipaggiabile) {
                pg.addEquipaggiabile((OggettoEquipaggiabile) oggetto);
                pg.impostaStatoEquipaggiabile((OggettoEquipaggiabile) oggetto, oggetto.isEquipaggiato());
            }
        }

        pg.ricalcolaStatisticheFinali();
    }

    /**
     * Recupera l'elenco delle Abilità precedentemente apprese da un personaggio consultando il database
     * tramite il relativo DAO.
     *
     * @param pg Il personaggio da ispezionare.
     * @throws RuntimeException Se il riferimento al personaggio fornito è nullo.
     */
    public void leggiAbilitaPersonaggio(Personaggio pg) {
        if (pg == null) throw new RuntimeException("Impossibile trovare il personaggio selezionato - possibile corruzione dei dati.");
        abilitaDao.caricaAbilitaApprese(pg);
    }




    // =========================================================================================
    //MASTER — GESTIONE PARTECIPANTI E PERSONAGGI
    // =========================================================================================

    /**
     * Rimuove un Personaggio Giocante (PG) dalla Campagna gestita dal Master.
     *
     * @param nomePersonaggio Il nome del PG da rimuovere.
     * @param nomeProprietario Il nome del Giocatore che interpreta il PG.
     * @throws PgNonSelezionatoException Se non viene specificato alcun personaggio o stringa vuota.
     * @throws DatiMancantiException Se il nome del proprietario è invalido o inesistente.
     * @throws PersonaggioNonTrovatoException Se la ricerca del PG fallisce.
     */
    public void rimuoviPGdaCampagna(String nomePersonaggio, String nomeProprietario) throws PersonaggioNonTrovatoException, DatiMancantiException, PgNonSelezionatoException {
        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PgNonSelezionatoException("Seleziona un personaggio da rimuovere.");
        }
        if (nomeProprietario == null || nomeProprietario.trim().isEmpty() || Objects.equals(nomeProprietario, "Sconosciuto"))
            throw new DatiMancantiException("ATTENZIONE: personaggio non associato a nessun giocatore.");

        Personaggio daRimuovere = cercaPg(nomePersonaggio, nomeProprietario);

        masterDAO.rimuoviPersonaggio(daRimuovere);
        campagnaAttiva.getListaPG().remove(daRimuovere);
    }

    /**
     * Cerca all'interno della lista giocatori e personaggi una corrispondenza esatta per individuare il Personaggio.
     * Metodo di utilità privato impiegato per operazioni di rimozione o modifica statistica dei PG da parte del Master.
     *
     * @param nomePersonaggio Il nome del personaggio giocante nel mondo fittizio.
     * @param nomeProprietario Lo username del giocatore reale a cui esso appartiene.
     * @return L'oggetto {@link Personaggio} corrispondente.
     * @throws PersonaggioNonTrovatoException Se l'attraversamento delle associazioni Giocatore-PG non fornisce risultati validi.
     */
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

    /**
     * Rimuove un Personaggio Non Giocante (PnG) dal sistema.
     *
     * @param id L'identificativo del PnG da eliminare.
     * @throws PersonaggioNonTrovatoException Se l'id non esiste o la ricerca fallisce.
     */
    public void rimuoviPnG(int id) throws PersonaggioNonTrovatoException {
        Personaggio daTrovare = null;
        for (Personaggio png : campagnaAttiva.getListaPnG()) {
            if (png.getId() == id) {
                daTrovare = png;
                break;
            }
        }
        if (daTrovare == null) throw new PersonaggioNonTrovatoException("Id non esistente, impossibile trovare il png.");
        masterDAO.rimuoviPersonaggio(daTrovare);
        campagnaAttiva.getListaPnG().remove(daTrovare);
    }

    /**
     * Crea un Personaggio Non Giocante (PnG) standard gestito dal Master.
     *
     * @param nome         Il nome del PnG.
     * @param razza        La razza del PnG.
     * @param classe       La classe del PnG.
     * @throws NomeMancantePngException Se il nome del PnG non viene inserito.
     * @throws DatiMancantiException Se mancano razza o classe.
     */
    public void creaPnG(String nome, Razza razza, Classe classe) throws NomeMancantePngException, DatiMancantiException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        if (razza == null || classe == null) throw new DatiMancantiException("Devi selezionare razza e classe prima di procedere.");
        Personaggio png = new Personaggio(classe, razza, nome, false);

        masterDAO.creaPnG(png, campagnaAttiva.getId());
        campagnaAttiva.getListaPnG().add(png);
    }

    /**
     * Crea un Personaggio Non Giocante (PnG) andando a definire anche i campi oro e punti statistica.
     *
     * @param nome         Il nome del PnG.
     * @param razza        La razza del PnG.
     * @param classe       La classe del PnG.
     * @param oro          La quantità di oro iniziale.
     * @param punti        I punti statistica disponibili fin dall'inizio.
     * @param statBase     La struttura iniziale delle statistiche base per il personaggio.
     * @throws NomeMancantePngException Se il nome del PnG non viene inserito.
     * @throws DatiMancantiException Se mancano razza o classe.
     * @throws IllegalArgumentException Se vengono inseriti valori statistici non validi (es. HpMax a 0).
     */
    public void creaPnG(String nome, Razza razza, Classe classe, int oro, int punti, Statistica statBase) throws NomeMancantePngException, DatiMancantiException, IllegalArgumentException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeMancantePngException("Il nome del PnG non può essere vuoto.");
        }
        if (razza == null || classe == null) throw new DatiMancantiException("Devi selezionare razza e classe prima di procedere.");

        // Controlli preventivi per evitare i crash SQL di violazione vincoli (Constraints)
        if (statBase.getHpMax() <= 0) {
            throw new IllegalArgumentException("Un personaggio deve avere almeno 1 HP massimo per poter essere creato!");
        }
        if (oro < 0) {
            throw new IllegalArgumentException("La quantità di oro iniziale non può essere negativa.");
        }
        if (punti < 0) {
            throw new IllegalArgumentException("I punti statistica non possono essere negativi.");
        }

        Personaggio png = new Personaggio(classe, razza, statBase, nome, oro, punti);

        masterDAO.creaPnG(png, campagnaAttiva.getId());
        campagnaAttiva.getListaPnG().add(png);
    }

    /**
     * Espelle un Giocatore partecipante dall'attuale sessione di campagna, eliminandone l'iscrizione sia in RAM
     * che a livello persistente e slegando di conseguenza le interazioni del suo Personaggio.
     *
     * @param idGiocatore L'identificativo numerico del Giocatore da allontanare.
     * @throws GiocatoreNonTrovatoException Se il Giocatore in questione non risulta regolarmente iscritto alla Campagna.
     * @throws RuntimeException Se si verifica un impedimento o un errore DB durante la fase di aggiornamento.
     */
    public void rimuoviGiocatoreDaCampagna(int idGiocatore) {
        Giocatore daRimuovere = null;
        for (Giocatore g : campagnaAttiva.getPartecipanti()) {
            if (g.getId() == idGiocatore) {
                daRimuovere = g;
                break;
            }
        }
        if (daRimuovere == null) throw new GiocatoreNonTrovatoException("Giocatore non esistente.");

        Personaggio pgDaRimuovere = daRimuovere.getPersonaggioInCampagna(campagnaAttiva);

        try {
            masterDAO.rimuoviGiocatore(idGiocatore, campagnaAttiva.getId());
            daRimuovere.getListaPartecipazioni().remove(campagnaAttiva);

            if (pgDaRimuovere != null) {
                campagnaAttiva.getListaPG().remove(pgDaRimuovere);
            }
            campagnaAttiva.getPartecipanti().remove(daRimuovere);
        } catch (RuntimeException ex) {
            if (ex.getMessage() == null || ex.getMessage().trim().isEmpty())
                throw new RuntimeException("Impossibile aggiornare i dati in memoria.");
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Eroga un ammontare cumulativo di punti statistica extra che un determinato personaggio potrà utilizzare
     * in seguito per innalzare manualmente le proprie caratteristiche. Il cambiamento viene salvato su database.
     *
     * @param idPersonaggio L'ID del personaggio che beneficerà di questi punti.
     * @param isPg Determina flag logicamente la ricerca del personaggio su target giocanti o non giocanti.
     * @param quantitaPunti Il numero di punti da assegnare (deve essere superiore o uguale a zero).
     * @throws PersonaggioNonTrovatoException Se l'identificativo non produce alcun hit fra i ranghi del party attivo.
     * @throws RuntimeException Se il numero di punti è negativo o si fallisce la persistenza del DAO.
     */
    public void assegnaPuntiStatistica(int idPersonaggio, boolean isPg, int quantitaPunti) throws PersonaggioNonTrovatoException, RuntimeException {
        if (quantitaPunti < 0) throw new RuntimeException("Non è possibile assegnare valori negativi.");
        try {
            Personaggio personaggio = cercaPersonaggio(idPersonaggio, isPg);
            personaggio.addPuntiStatistica(quantitaPunti);
            masterDAO.assegnaPuntiStatistica(personaggio.getId(), quantitaPunti);
            personaggio.ricalcolaStatisticheFinali();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Permette al Master di assegnare oro a un Personaggio Giocante.
     *
     * @param idPersonaggio l'identificativo del PG.
     * @param quantitaOro la quantità di oro da aggiungere (deve essere maggiore di zero).
     * @throws PersonaggioNonTrovatoException Se la ricerca del PG fallisce.
     * @throws RuntimeException Se si verifica un errore durante il salvataggio.
     */
    public void assegnaOroMaster(int idPersonaggio, int quantitaOro) throws PersonaggioNonTrovatoException {
        if (quantitaOro <= 0) throw new RuntimeException("La quantità da assegnare deve essere maggiore di zero.");

        try {
            Personaggio pg = cercaPersonaggio(idPersonaggio, true);
            pg.setOro(pg.getOro() + quantitaOro);
            giocatoreDAO.aggiornaRisorse(pg);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Permette al Master di sottrarre oro a un Personaggio Giocante (es. multe o sanzioni narrative).
     *
     * @param idPersonaggio l'identificativo del PG.
     * @param quantitaOro la quantità di oro da sottrarre (deve essere maggiore di zero).
     * @throws PersonaggioNonTrovatoException Se la ricerca del PG fallisce.
     * @throws RuntimeException Se l'oro finale andrebbe sotto lo zero.
     */
    public void sottraiOroMaster(int idPersonaggio, int quantitaOro) throws PersonaggioNonTrovatoException {
        if (quantitaOro <= 0) throw new RuntimeException("La quantità da sottrarre deve essere maggiore di zero.");

        try {
            Personaggio pg = cercaPersonaggio(idPersonaggio, true);
            int nuovoOro = pg.getOro() - quantitaOro;

            if (nuovoOro < 0) {
                throw new RuntimeException("L'oro del personaggio non può scendere sotto lo zero.");
            }

            pg.setOro(nuovoOro);
            giocatoreDAO.aggiornaRisorse(pg);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Carica dal database tutte le abilità sbloccabili previste per la classe specificata.
     * Necessario per popolare i menu a tendina dell'assegnazione abilità.
     *
     * @param classe La classe di cui caricare le abilità.
     */
    public void caricaAbilitaSbloccabiliPerClasse(Classe classe) {
        if (classe != null) {
            abilitaDao.caricaAbilitaSbloccabili(classe);
        }
    }

    /**
     * Permette al Master di forzare l'apprendimento di un'abilità per un Personaggio,
     * bypassando i controlli del giocatore.
     *
     * @param idPersonaggio l'identificativo del PG.
     * @param nomeAbilita il nome dell'abilità da assegnare.
     * @throws Exception Se ci sono problemi nel reperire il PG o nel salvataggio.
     */
    public void assegnaAbilitaMaster(int idPersonaggio, String nomeAbilita, boolean isPg) throws Exception {
        if (nomeAbilita == null || nomeAbilita.trim().isEmpty()) {
            throw new AbilitaNonSelezionataException("Nessuna abilità specificata.");
        }

        Personaggio pg = cercaPersonaggio(idPersonaggio, isPg);

        Abilita target = null;
        for (Abilita abilita : pg.getClasse().getAbilitaSbloccabili()) {
            if (abilita.getNome().trim().equalsIgnoreCase(nomeAbilita.trim())) {
                target = abilita;
                break;
            }
        }

        if (target == null) throw new AbilitaNonSbloccabileException("L'abilità non fa parte della classe del personaggio.");

        leggiAbilitaPersonaggio(pg); // Sincronizza prima le abilità già apprese dal DB

        for (Abilita a : pg.getListaAbilita()) {
            if (a.getNome().trim().equalsIgnoreCase(target.getNome().trim())) {
                throw new AbilitaGiaAppresaException("Il personaggio conosce già questa abilità.");
            }
        }

        try {
            abilitaDao.imparaAbilita(pg.getId(), target.getNome());
            pg.addAbilita(target);
        } catch (RuntimeException e) {
            throw new Exception("Errore durante il salvataggio dell'abilità nel Database.");
        }
    }

    /**
     * Permette al Master di assegnare un oggetto all'inventario di un personaggio.
     *
     * @param idPersonaggio L'ID del personaggio a cui assegnare l'oggetto.
     * @param idOggetto L'ID dell'oggetto da assegnare.
     * @throws RuntimeException Se si verifica un errore durante l'assegnazione dell'oggetto.
     */
    public void assegnaOggettoMaster(int idPersonaggio, int idOggetto, boolean isPg) {
        try {
            inventarioDAO.assegnaOggettoAInventario(idPersonaggio, idOggetto);
            // Dopo l'assegnazione, riallinea l'inventario in memoria
            Personaggio pg = cercaPersonaggio(idPersonaggio, isPg);
            leggiInventarioPersonaggio(pg);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'assegnazione dell'oggetto: " + e.getMessage());
        }
    }

    /**
     * Aggiorna e salva le nuove statistiche di un PG o PnG nel database.
     * @param nomePersonaggio Il nome stringa del personaggio.
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
     * @param isPg            Booleano per definire la directory appropriata di ricerca.
     * @throws PngNonSelezionatoException Se il personaggio testuale non è stato selezionato correttamente.
     * @throws PersonaggioNonTrovatoException Se la ricerca del personaggio non ha successo per l'ID dato.
     */
    public void salvaStatisticheModificate(String nomePersonaggio, int idPersonaggio, int forza, int destrezza, int costituzione,
                                           int intelligenza, int fede, int carisma, int fortuna,
                                           int hpMax, int manaMax, boolean isPg) throws PngNonSelezionatoException, PersonaggioNonTrovatoException {

        if (nomePersonaggio == null || nomePersonaggio.trim().isEmpty()) {
            throw new PngNonSelezionatoException("Nessun personaggio selezionato.");
        }
        Personaggio daModificare = null;
        ArrayList<Personaggio> listaPersonaggi = isPg ? campagnaAttiva.getListaPG() : campagnaAttiva.getListaPnG();
        for (Personaggio pg : listaPersonaggi) {
            if (pg.getId() == idPersonaggio) {
                daModificare = pg;
                break;
            }
        }
        if (daModificare == null) throw new PersonaggioNonTrovatoException("Impossibile trovare il personaggio selezionato.");

        Statistica modifiche = new Statistica(costituzione, forza, destrezza, intelligenza, fede, carisma, fortuna, hpMax, manaMax);
        personaggioDAO.aggiornaStatistichePersonaggio(daModificare.getId(), modifiche);
        daModificare.setStatisticaBase(modifiche);

        daModificare.setHpCorrenti(hpMax);
        daModificare.setManaCorrente(manaMax);

        leggiInventarioPersonaggio(daModificare);

        if (daModificare.getInventarioEquipaggiabili() != null) {
            List<OggettoEquipaggiabile> daDisequipaggiare = new ArrayList<>();

            for (Map.Entry<OggettoEquipaggiabile, Boolean> entry : daModificare.getInventarioEquipaggiabili().entrySet()) {
                if (entry.getValue()) {
                    Statistica req = entry.getKey().getRequisiti();

                    if (forza < req.getForza() || destrezza < req.getDestrezza() || costituzione < req.getCostituzione() ||
                            intelligenza < req.getIntelligenza() || fede < req.getFede() || carisma < req.getCarisma() ||
                            fortuna < req.getFortuna() || hpMax < req.getHpMax() || manaMax < req.getManaMax()) {

                        daDisequipaggiare.add(entry.getKey());
                    }
                }
            }

            for (OggettoEquipaggiabile eq : daDisequipaggiare) {
                try {
                    inventarioDAO.impostaEquipaggiamento(daModificare.getId(), eq.getId(), false);
                    daModificare.rimuoviEquipaggiamento(eq);
                    System.out.println("Oggetto disequipaggiato automaticamente per requisiti mancanti: " + eq.getNome());
                } catch (Exception e) {
                    System.err.println("Errore durante l'auto-disequipaggiamento in DB: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Interroga le tabelle del Master alla ricerca di una entità Personaggio che condivida l'id specificato.
     * Serve ad agganciare l'oggetto esatto dalle collezioni residenti su cui applicare aggiornamenti statistici.
     *
     * @param id Il CodPersonaggio da verificare.
     * @param isPg Un booleano in cui true fa cercare nella lista dei Giocanti e false nella lista Non Giocanti.
     * @return Il {@link Personaggio} trovato e identificato con successo.
     * @throws Exception Se non esiste una sessione campagna associabile, o in assenza del parametro di target ricercato.
     */
    public Personaggio cercaPersonaggio(int id, boolean isPg) throws Exception {
        if (campagnaAttiva == null) throw new Exception("Errore critico: campagna relativa al personaggio non trovata!");
        List<Personaggio> daCercare = (isPg) ? campagnaAttiva.getListaPG() : campagnaAttiva.getListaPnG();
        Personaggio trovato = null;
        for (Personaggio pg : daCercare) {
            if (pg.getId() == id) {
                trovato = pg;
                break;
            }
        }
        if (trovato == null) throw new Exception("Impossibile trovare il personaggio selezionato");
        return trovato;
    }


    // =========================================================================================
    //ECONOMIA / NEGOZIO
    // =========================================================================================

    /**
     * Esegue la transazione per far acquistare un oggetto a un personaggio.
     *
     * @param nomeOggetto Il nome dell'oggetto da acquistare.
     * @throws OggettoNonSelezionatoException Se l'oggetto specificato non è valido o se l'oro è insufficiente.
     */
    public void compraOggetto(String nomeOggetto) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Inserisci un oggetto valido.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        List<Oggetto> catalogo = getCatalogoNegozio();
        Oggetto oggettoScelto = null;

        for (Oggetto oggetto : catalogo) {
            if (oggetto.getNome().equalsIgnoreCase(nomeOggetto)) {
                oggettoScelto = oggetto;
                break;
            }
        }

        if (oggettoScelto == null) throw new OggettoNonSelezionatoException("Oggetto non trovato in negozio.");
        if (pg.getOro() < oggettoScelto.getCosto()) throw new OggettoNonSelezionatoException("Oro insufficiente.");

        // CONTROLLO DUPLICATI EQUIPAGGIAMENTO
        if (oggettoScelto instanceof OggettoEquipaggiabile) {
            for (OggettoEquipaggiabile eq : pg.getInventarioEquipaggiabili().keySet()) {
                if (eq.getNome().equalsIgnoreCase(oggettoScelto.getNome())) {
                    throw new OggettoNonSelezionatoException("Possiedi già questo pezzo di equipaggiamento.");
                }
            }
        }

        inventarioDAO.acquistaOggetto(pg.getId(), oggettoScelto.getId(), oggettoScelto.getCosto());
        pg.setOro(pg.getOro() - oggettoScelto.getCosto());

        if (oggettoScelto instanceof OggettoConsumabile) {
            pg.addConsumabile((OggettoConsumabile) oggettoScelto, 1);
        } else if (oggettoScelto instanceof OggettoEquipaggiabile) {
            pg.addEquipaggiabile((OggettoEquipaggiabile) oggettoScelto);
        }
    }

    /**
     * Gestisce la transazione di vendita di un oggetto al negozio.
     * <p>
     * 1. Identifica se l'oggetto e' equipaggiabile o consumabile e ne verifica lo stato.
     * 2. Calcola il ricavo dimezzando il costo base.
     * 3. Esegue un UPDATE tramite DAO per scalare o rimuovere l'oggetto dall'inventario.
     * 4. Esegue un UPDATE tramite DAO aggiornando le monete d'oro del Personaggio.
     * 5. Mantiene coerenti i dati in RAM aggiornando le Hashmap in locale.
     * </p>
     *
     * @param nomeOggetto  Il nome dell'oggetto da vendere.
     * @param nomeCampagna Il nome della campagna attuale.
     * @throws OggettoNonSelezionatoException Se il nome dell'oggetto e' vuoto, non posseduto o attualmente equipaggiato.
     */
    public void vendiOggetto(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da vendere.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoConsumabile targetConsumabile = null;
        OggettoEquipaggiabile targetEquipaggiabile = null;

        for (OggettoConsumabile consumabile : pg.getInventarioConsumabili().keySet()) {
            if (consumabile.getNome().equalsIgnoreCase(nomeOggetto)) {
                targetConsumabile = consumabile;
                break;
            }
        }

        if (targetConsumabile == null) {
            for (OggettoEquipaggiabile equipaggiabile : pg.getInventarioEquipaggiabili().keySet()) {
                if (equipaggiabile.getNome().equalsIgnoreCase(nomeOggetto)) {
                    if (pg.getInventarioEquipaggiabili().get(equipaggiabile)) {
                        throw new OggettoNonSelezionatoException("Non puoi vendere un oggetto attualmente equipaggiato!");
                    }
                    targetEquipaggiabile = equipaggiabile;
                    break;
                }
            }
        }

        if (targetConsumabile == null && targetEquipaggiabile == null) {
            throw new OggettoNonSelezionatoException("Non possiedi questo oggetto.");
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

    /**
     * Imposta il flag di utilizzo al vero su un pezzo di equipaggiamento dopo aver validato
     * con il database l'effettivo soddisfacimento dei requisiti statistici del giocatore.
     * L'interazione sincronizza istantaneamente in RAM l'impatto dei bonus passivi di tale oggetto.
     *
     * @param nomeOggetto Stringa nominale associata al tassello di equipaggiamento.
     * @param nomeCampagna Il nome della campagna di gioco dove è inquadrato l'evento.
     * @throws OggettoNonSelezionatoException Qualora il puntamento sia incongruente o carente di requisiti.
     */
    public void equipaggiaOggetto(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da equipaggiare.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoEquipaggiabile target = null;
        for (OggettoEquipaggiabile equipaggiabile : pg.getInventarioEquipaggiabili().keySet()) {
            if (equipaggiabile.getNome().equalsIgnoreCase(nomeOggetto)) {
                target = equipaggiabile;
                break;
            }
        }

        if (target == null) throw new OggettoNonSelezionatoException("Non possiedi questo equipaggiamento.");

        try {
            inventarioDAO.impostaEquipaggiamento(pg.getId(), target.getId(), true);
            pg.impostaStatoEquipaggiabile(target, true);
            pg.aggiornaStatoPG();
        } catch (RuntimeException e) {
            throw new OggettoNonSelezionatoException(e.getMessage());
        }
    }

    /**
     * Stacca l'oggetto dai calcoli delle statistiche in real-time, svuotando i flag
     * e liberando il corpo del personaggio da tale armamento per riporlo nello zaino.
     *
     * @param nomeOggetto Stringa nominale associata al tassello di equipaggiamento.
     * @param nomeCampagna Il nome della campagna di gioco interconnessa.
     * @throws OggettoNonSelezionatoException Qualora il puntamento sia incongruente.
     */
    public void rimuoviEquipaggiamento(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona un oggetto da rimuovere.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoEquipaggiabile target = null;
        for (OggettoEquipaggiabile equipaggiabile : pg.getInventarioEquipaggiabili().keySet()) {
            if (equipaggiabile.getNome().equalsIgnoreCase(nomeOggetto)) {
                target = equipaggiabile;
                break;
            }
        }

        if (target != null && pg.getInventarioEquipaggiabili().get(target)) {
            inventarioDAO.impostaEquipaggiamento(pg.getId(), target.getId(), false);
            pg.rimuoviEquipaggiamento(target);
        }
    }

    /**
     * Scala e decrementa l'inventario dal database ad un oggetto consumabile valido (es. pozioni)
     * e implementa matematicamente il boost della cura sui profili fisici correnti del personaggio (Hp, Mana).
     * Dispone di una routine difensiva in memoria nel caso in cui fallisca il check lato server (Rollback).
     *
     * @param nomeOggetto Stringa che definisce cosa sta per ingerire/usare l'utente.
     * @param nomeCampagna Campagna correlata allo scope di gioco.
     * @throws OggettoNonSelezionatoException Se il personaggio tenta la consumazione di qualcosa che non possiede.
     */
    public void usaConsumabile(String nomeOggetto, String nomeCampagna) throws OggettoNonSelezionatoException {
        if (nomeOggetto == null || nomeOggetto.trim().isEmpty()) {
            throw new OggettoNonSelezionatoException("Seleziona una pozione da usare.");
        }

        Giocatore giocatore = (Giocatore) utenteAttivo;
        Personaggio pg = giocatore.getPersonaggioInCampagna(campagnaAttiva);

        OggettoConsumabile target = null;
        for (OggettoConsumabile consumabile : pg.getInventarioConsumabili().keySet()) {
            if (consumabile.getNome().equalsIgnoreCase(nomeOggetto)) {
                target = consumabile;
                break;
            }
        }

        if (target == null) {
            throw new OggettoNonSelezionatoException("Non possiedi questo consumabile nel tuo inventario.");
        }

        int oldHp = pg.getHpCorrenti();
        int oldMana = pg.getManaCorrente();

        try {
            inventarioDAO.consumaOggetto(pg.getId(), target.getId());
            pg.ripristinaHP(target.getRipristinoHP());
            pg.ripristinaMana(target.getRipristinoMana());
            giocatoreDAO.aggiornaRisorse(pg);
            pg.rimuoviConsumabile(target, 1);
        } catch (Exception e) {
            pg.setHpCorrenti(oldHp);
            pg.setManaCorrente(oldMana);
            leggiInventarioPersonaggio(pg);
            throw new RuntimeException("Transazione interrotta durante l'utilizzo dell'oggetto: " + e.getMessage());
        }
    }

    /**
     * Rimuove un oggetto dall'inventario di un personaggio.
     * Questa operazione aggiorna sia il database che lo stato in memoria del personaggio.
     *
     * @param pg Il personaggio dal cui inventario rimuovere l'oggetto.
     * @param idOggetto L'ID dell'oggetto da rimuovere.
     * @throws RuntimeException Se si verifica un errore durante la rimozione dell'oggetto.
     */
    public void rimuoviOggettoDaInventario(Personaggio pg, int idOggetto) {
        try {
            inventarioDAO.rimuoviOggetto(pg.getId(), idOggetto);
            // Dopo la rimozione dal DB, riallinea l'inventario in memoria
            leggiInventarioPersonaggio(pg);
        } catch (RuntimeException e) {
            throw new RuntimeException("Errore durante la rimozione dell'oggetto dall'inventario: " + e.getMessage());
        }
    }

    /**
     * Recupera il catalogo del negozio filtrato per la campagna attiva.
     *
     * @return Una lista di Oggetti acquistabili in questa specifica campagna.
     */
    public List<Oggetto> getCatalogoNegozio() {
        if (campagnaAttiva != null) {
            return campagnaDAO.caricaCatalogoNegozio(campagnaAttiva.getId());
        }
        return new ArrayList<>();
    }


    // =========================================================================================
    //EDITOR MASTER — RAZZE, CLASSI, OGGETTI, ABILITA'
    // =========================================================================================

    /**
     * Crea una nuova Razza associata alla campagna attiva e la salva permanentemente nel database.
     *
     * @param nome Il nome stringa identificativo della razza.
     * @param descrizione Descrizione estesa del lore della razza.
     * @param mFor Modificatore base aggiunto al pool statico della Forza.
     * @param mDes Modificatore base per la Destrezza.
     * @param mCos Modificatore base per la Costituzione.
     * @param mInt Modificatore base per l'Intelligenza.
     * @param mFed Modificatore base applicabile per Fede.
     * @param mCar Modificatore base per il Carisma.
     * @param mForz Modificatore passivo bonus della Fortuna.
     * @param mHp Bonus ai punti vitalità.
     * @param mMana Bonus al pool del mana incantato.
     * @throws Exception Generato se sfugge o fallisce la campagna per cui stiamo generating la referenza.
     */
    public void creaNuovaRazza(String nome, String descrizione, int mFor, int mDes, int mCos,
                               int mInt, int mFed, int mCar, int mForz, int mHp, int mMana) throws Exception {

        if (campagnaAttiva == null) throw new Exception("Nessuna campagna attiva selezionata.");
        if (nome == null || nome.trim().isEmpty()) throw new exception.DatiMancantiException("Il nome della razza è obbligatorio.");

        for (Razza razza : campagnaAttiva.getListaRazze()) {
            if (razza.getNome().trim().equalsIgnoreCase(nome.trim())) {
                throw new RuntimeException("Una razza chiamata '" + nome + "' esiste già in questa campagna.");
            }
        }

        Razza nuovaRazza = new Razza(mCos, mFor, mDes, mInt, mFed, mCar, mForz, mHp, mMana, nome);

        int idGenerato = razzaDao.salvaRazza(nuovaRazza, descrizione, campagnaAttiva.getId());
        nuovaRazza.setId(idGenerato);
        campagnaAttiva.getListaRazze().add(nuovaRazza);
    }

    /**
     * Crea una nuova Classe archetipica, la salva in modo persistente nel database
     * tramite il DAO dedicato e la aggiunge alla sessione della Campagna attiva.
     *
     * @param nome        Il nome identificativo della classe (es. "Guerriero").
     * @param descrizione La lore o background narrativo della classe.
     * @throws DatiMancantiException Se il nome della classe è nullo o vuoto,
     *                               o se non vi è alcuna campagna attiva in sessione.
     */
    public void creaNuovaClasse(String nome, String descrizione) throws DatiMancantiException {
        if (campagnaAttiva == null) {
            throw new exception.DatiMancantiException("Nessuna campagna attiva selezionata.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new exception.DatiMancantiException("Il nome della classe è obbligatorio.");
        }

        for (Classe classe : campagnaAttiva.getListaClassi()) {
            if (classe.getNome().trim().equalsIgnoreCase(nome.trim())) {
                throw new RuntimeException("Una classe chiamata '" + nome + "' esiste già in questa campagna.");
            }
        }

        Classe nuovaClasse = new Classe(nome);
        nuovaClasse.setDescrizione(descrizione);

        int idGenerato = classeDao.salvaClasse(nuovaClasse, descrizione, campagnaAttiva.getId());

        nuovaClasse.setId(idGenerato);
        campagnaAttiva.getListaClassi().add(nuovaClasse);
    }

    /**
     * Crea una nuova Abilità, la associa a una specifica Classe e la salva in modo persistente.
     * Include un controllo preventivo per evitare l'inserimento di abilità duplicate.
     *
     * @param classe La classe di gioco (es. Mago) a cui associare l'abilità.
     * @param nome Il nome identificativo dell'abilità.
     * @param descrizione La descrizione del suo funzionamento nel gioco.
     * @throws exception.DatiMancantiException Se il nome o la descrizione sono vuoti.
     * @throws IllegalArgumentException Se l'abilità esiste già o se si verifica un errore sul database.
     */
    public void creaNuovaAbilita(Classe classe, String nome, String descrizione) throws DatiMancantiException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new exception.DatiMancantiException("Il nome dell'abilità è obbligatorio.");
        }
        if (descrizione == null || descrizione.trim().isEmpty()) {
            throw new exception.DatiMancantiException("La descrizione dell'abilità è obbligatoria.");
        }

        if (classe.getAbilitaSbloccabili() != null) {
            for (Abilita a : classe.getAbilitaSbloccabili()) {
                if (a.getNome().trim().equalsIgnoreCase(nome.trim())) {
                    throw new IllegalArgumentException("Questa Classe possiede già un'abilità chiamata '" + nome + "'.");
                }
            }
        }

        Abilita nuovaAbilita = new Abilita(nome, descrizione);

        try {
            int idGenerato = abilitaDao.salvaAbilitaSbloccabile(nuovaAbilita, classe.getId());
            nuovaAbilita.setId(idGenerato);

            if (classe.getAbilitaSbloccabili() == null) {
                classe.setAbilitaSbloccabili(new ArrayList<>());
            }

            classe.getAbilitaSbloccabili().add(nuovaAbilita);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio sul DB: " + e.getMessage());
        }
    }

    /**
     * Rimuove un'abilità dall'elenco di quelle sbloccabili per una specifica Classe.
     * Sincronizza l'eliminazione sia sul Database che nella memoria del programma.
     *
     * @param classe La classe di appartenenza.
     * @param nomeAbilita Il nome dell'abilità da rimuovere.
     * @throws RuntimeException Se l'abilità non viene trovata o se il database fallisce.
     */
    public void rimuoviAbilitaDaClasse(Classe classe, String nomeAbilita) {
        if (nomeAbilita == null || nomeAbilita.trim().isEmpty()) {
            throw new RuntimeException("Seleziona un'abilità da rimuovere.");
        }

        Abilita daRimuovere = null;
        for (Abilita abilita : classe.getAbilitaSbloccabili()) {
            if (abilita.getNome().equalsIgnoreCase(nomeAbilita)) {
                daRimuovere = abilita;
                break;
            }
        }

        if (daRimuovere == null) {
            throw new RuntimeException("Abilità non trovata nella classe specificata.");
        }

        try {
            abilitaDao.rimuoviAbilitaSbloccabile(daRimuovere.getId(), classe.getId());
            classe.getAbilitaSbloccabili().remove(daRimuovere);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la rimozione dell'abilità dal DB: " + e.getMessage());
        }
    }

    /**
     * Crea un nuovo Oggetto Consumabile (es. Pozioni) e lo aggiunge al catalogo del negozio.
     *
     * @param nome    Il nome del consumabile.
     * @param costo   Il valore di acquisto in monete d'oro.
     * @param ripHp   La quantità di Punti Vita ripristinati all'utilizzo.
     * @param ripMana La quantità di Punti Mana ripristinati all'utilizzo.
     * @throws DatiMancantiException Se il nome è vuoto o la campagna non è attiva.
     */
    public void creaNuovoConsumabile(String nome, int costo, int ripHp, int ripMana) throws DatiMancantiException {
        if (campagnaAttiva == null) throw new exception.DatiMancantiException("Nessuna campagna attiva selezionata.");
        if (nome == null || nome.trim().isEmpty()) throw new exception.DatiMancantiException("Il nome del consumabile è obbligatorio.");

        for (Oggetto oggetto : campagnaAttiva.getCatalogoOggetti()) {
            if (oggetto.getNome().trim().equalsIgnoreCase(nome.trim())) {
                throw new RuntimeException("Un oggetto chiamato '" + nome + "' esiste già nel catalogo della campagna.");
            }
        }

        OggettoConsumabile consumabile = new OggettoConsumabile(nome, costo, ripHp, ripMana);

        int idGenerato = oggettoDao.salvaConsumabile(consumabile, campagnaAttiva.getId());
        consumabile.setId(idGenerato);

        campagnaAttiva.getCatalogoOggetti().add(consumabile);
    }

    /**
     * Crea un nuovo Oggetto Equipaggiabile (es. Armi, Armature) e lo aggiunge al catalogo.
     * Permette di definire sia i bonus statistici forniti, sia i requisiti minimi per indossarlo.
     *
     * @param nome   Il nome dell'equipaggiamento.
     * @param costo  Il valore in oro.
     * @param bCos   Il bonus conferito alla Costituzione.
     * @param bForz  Il bonus conferito alla Forza.
     * @param bDes   Il bonus conferito alla Destrezza.
     * @param bInt   Il bonus conferito all'Intelligenza.
     * @param bFed   Il bonus conferito alla Fede.
     * @param bCar   Il bonus conferito al Carisma.
     * @param bFort  Il bonus conferito alla Fortuna.
     * @param bHp    L'aumento dei Punti Vita massimi.
     * @param bMana  L'aumento dei Punti Mana massimi.
     * @param rCos   Requisito minimo di Costituzione.
     * @param rForz  Requisito minimo di Forza.
     * @param rDes   Requisito minimo di Destrezza.
     * @param rInt   Requisito minimo di Intelligenza.
     * @param rFed   Requisito minimo di Fede.
     * @param rCar   Requisito minimo di Carisma.
     * @param rFort  Requisito minimo di Fortuna.
     * @param rHp    Requisito minimo di Punti Vita.
     * @param rMana  Requisito minimo di Punti Mana.
     * @throws DatiMancantiException Se il nome è vuoto o la campagna non è attiva.
     */
    public void creaNuovoEquipaggiamento(String nome, int costo,
                                         int bCos, int bForz, int bDes, int bInt, int bFed, int bCar, int bFort, int bHp, int bMana,
                                         int rCos, int rForz, int rDes, int rInt, int rFed, int rCar, int rFort, int rHp, int rMana) throws DatiMancantiException {

        if (campagnaAttiva == null) throw new exception.DatiMancantiException("Nessuna campagna attiva selezionata.");
        if (nome == null || nome.trim().isEmpty()) throw new exception.DatiMancantiException("Il nome dell'equipaggiamento è obbligatorio.");

        for (Oggetto oggetto : campagnaAttiva.getCatalogoOggetti()) {
            if (oggetto.getNome().trim().equalsIgnoreCase(nome.trim())) {
                throw new RuntimeException("Un oggetto chiamato '" + nome + "' esiste già nel catalogo della campagna.");
            }
        }

        model.Statistica requisitiMinimi = new model.Statistica(rCos, rForz, rDes, rInt, rFed, rCar, rFort, rHp, rMana);
        model.Statistica bonus = new model.Statistica(bCos, bForz, bDes, bInt, bFed, bCar, bFort, bHp, bMana);

        OggettoEquipaggiabile equip = new OggettoEquipaggiabile(nome, costo, requisitiMinimi, bonus);

        int idGenerato = oggettoDao.salvaEquipaggiamento(equip, campagnaAttiva.getId());
        equip.setId(idGenerato);

        campagnaAttiva.getCatalogoOggetti().add(equip);
    }

    /**
     * Recupera l'elenco delle Razze abilitate dal Master per una specifica campagna.
     *
     * @param campagna l'oggetto Campagna da cui recuperare le informazioni.
     * @return La lista di Razze disponibili.
     */
    public List<Razza> getRazzePerCampagna(Campagna campagna) {
        List<Razza> razzeDisponibili = new ArrayList<>();
        if (campagna != null) {
            campagnaDAO.leggiListaRazze(razzeDisponibili, campagna.getId());
        }
        return razzeDisponibili;
    }

    /**
     * Recupera l'elenco delle Classi create dal Master per una specifica campagna.
     *
     * @param campagna l'oggetto Campagna da cui recuperare le informazioni.
     * @return La lista di Classi disponibili.
     */
    public List<Classe> getClassiPerCampagna(Campagna campagna) {
        List<Classe> classiDisponibili = new ArrayList<>();
        if (campagna != null) {
            campagnaDAO.leggiListaClassi(classiDisponibili, campagna.getId());
        }
        return classiDisponibili;
    }

}