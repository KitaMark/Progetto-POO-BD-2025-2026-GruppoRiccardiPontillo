package implementazionePostgresDAO;

import dao.CampagnaDAO;
import database.ConnessioneDatabase;
import exception.NomeCampagnaInUsoException;
import model.*;
import database.ConnessioneDatabase;
import exception.DatiMancantiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Implementazione concreta dell'interfaccia {@link CampagnaDAO} per il database PostgreSQL.
 * <p>
 * Gestisce la persistenza, il recupero massivo e l'eliminazione delle campagne di gioco.
 * Si occupa di ricostruire in RAM le complesse relazioni tra le entità (Campagna, Master, Giocatori e Personaggi)
 * garantendo la corretta mappatura delle chiavi primarie per mantenere l'identità degli oggetti.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresCampagna implements CampagnaDAO {

    /**
     * Recupera tutte le campagne presenti nel database e popola la mappa fornita in input all'avvio.
     * <p>
     * Legge sia i dati della campagna che quelli del Master.
     * </p>
     *
     * @param listaCampagne La mappa (inizialmente vuota) in cui inserire le campagne estratte.
     * La chiave sarà l'oggetto {@link Campagna}, il valore sarà l'oggetto {@link Master}.
     * @throws RuntimeException Se si verifica un errore critico durante la comunicazione col database.
     */
    @Override
    public void leggiCampagne(HashMap<Campagna, Master> listaCampagne) {
        String query = "SELECT c.CodCampagna, c.Nome, c.MaxGiocatori, c.Stato, " +
                "u.CodUtente, u.Username, u.Email, u.Password " +
                "FROM CAMPAGNA c " +
                "INNER JOIN UTENTE u ON c.CodMaster = u.CodUtente";

        try {

            Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idMaster = rs.getInt("CodUtente");
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                Master master = new Master(email, username, password);
                master.setId(idMaster); // Salviamo l'ID reale del Master!

                int idCampagna = rs.getInt("CodCampagna");
                String nomeCampagna = rs.getString("Nome");
                int maxGiocatori = rs.getInt("MaxGiocatori");
                String statoDb = rs.getString("Stato");

                Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, master);
                campagna.setId(idCampagna);

                if ("Non Iniziata".equals(statoDb)) {
                    campagna.setIniziata(false);
                } else {
                    campagna.setIniziata(true);
                }

                listaCampagne.put(campagna, master);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante il caricamento delle campagne dal database.");
        }
    }

    /**
     * Inserisce una nuova campagna di gioco all'interno del database generata dal Master.
     * <p>
     * Sfrutta una squery per recuperare l'ID del Master e, tramite la clausola {@code RETURNING CodCampagna},
     * restituisce immediatamente la chiave primaria appena generata da PostgreSQL.
     * </p>
     *
     * @param campagna L'oggetto {@link Campagna} contenente i dati da persistere.
     * @return L'identificativo numerico (ID) assegnato dal database alla nuova campagna.
     * @throws NomeCampagnaInUsoException Se il nome scelto per la campagna è già presente nel DB.
     */
    @Override
    public int creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException {
        String query = "INSERT INTO CAMPAGNA (Nome, MaxGiocatori, Stato, CodMaster) " +
                "VALUES (?, ?, 'Non Iniziata', (SELECT CodUtente FROM UTENTE WHERE Username = ?)) " +
                "RETURNING CodCampagna";

        try{
            Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, campagna.getNome());
            stmt.setInt(2, campagna.getMaxGiocatori());
            stmt.setString(3, campagna.getMaster().getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CodCampagna"); // Restituisce l'ID al Controller
                } else {
                    throw new SQLException("Nessun ID generato dal database.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DEBUG: Errore salvataggio campagna in db");
            throw new NomeCampagnaInUsoException("Dati campagna corrotti durante il salvataggio.");
        }
    }

    /**
     * Rimuove definitivamente una campagna dal database partendo dal suo nome univoco.
     * <p>
     * L'eliminazione innesca un effetto a cascata (ON DELETE CASCADE) a livello di database,
     * distruggendo automaticamente tutte le iscrizioni alla tabella ponte e i personaggi associati.
     * </p>
     *
     * @param campagnaTarget la {@link Campagna} da eliminare.
     * @throws DatiMancantiException Se nessuna riga viene modificata (la campagna non esiste).
     * @throws RuntimeException      Se si verifica un errore SQL critico.
     */
    @Override
    public void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException {
        String query = "DELETE FROM CAMPAGNA WHERE Nome = ?";

        try{

             Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, campagnaTarget.getNome());
            int righeModificate = stmt.executeUpdate();

            if (righeModificate == 0) {
                throw new DatiMancantiException("Impossibile eliminare: La campagna '" + campagnaTarget.getNome() + "' non esiste.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante l'eliminazione della campagna.");
        }
    }

    /**
     * Carica i personaggi appartenenti a una specifica campagna filtrandoli per tipologia (PG o PnG).
     * <p>
     * Esegue una serie di {@code JOIN} con le tabelle {@code CLASSE}, {@code RAZZA}, {@code CAMPAGNA}
     * e {@code STATISTICA} per ricostruire interamente l'albero delle istanze del personaggio.
     * </p>
     *
     * @param listaPersonaggi La lista (svuotata e ripopolata) che conterrà i personaggi trovati.
     * @param isPg            {@code true} per cercare i Personaggi Giocanti, {@code false} per i Personaggi Non Giocanti (Master).
     * @param nomeCampagna    Il nome della campagna in cui cercare.
     * @throws DatiMancantiException In caso di errori durante l'estrazione SQL.
     */
    @Override
    public void leggiListaPersonaggi(List<Personaggio> listaPersonaggi, boolean isPg, String nomeCampagna) throws DatiMancantiException {
        listaPersonaggi.clear();

        String query = "SELECT p.CodPersonaggio, p.Nome, p.Oro, p.IsPG, " +
                "c.Nome AS nome_classe, " +
                "r.Nome AS nome_razza, " +
                "sp.HpAttuali, sp.ManaAttuali, sp.PuntiSpendere, " +
                "sp.Forza AS forza_base, sp.Destrezza AS destrezza_base, sp.Costituzione AS costituzione_base, " +
                "sp.Intelligenza AS intelligenza_base, sp.Fede AS fede_base, sp.Carisma AS carisma_base, sp.Fortuna AS fortuna_base, " +
                "r.ModForza, r.ModDestrezza, r.ModCostituzione, r.ModIntelligenza, r.ModFede, r.ModCarisma, r.ModFortuna " +
                "FROM PERSONAGGIO p " +
                "JOIN CLASSE c ON p.CodClasse = c.CodClasse " +
                "JOIN RAZZA r ON p.CodRazza = r.CodRazza " +
                "JOIN CAMPAGNA cam ON p.CodCampagna = cam.CodCampagna " +
                "LEFT JOIN STATISTICA sp ON sp.CodPersonaggio = p.CodPersonaggio " +
                "WHERE p.IsPG = ? AND cam.Nome = ?";

        try{

             Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setBoolean(1, isPg);
            pstmt.setString(2, nomeCampagna);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("CodPersonaggio");
                    String nome = rs.getString("Nome");
                    int hpCorrenti = rs.getInt("HpAttuali");
                    int manaCorrente = rs.getInt("ManaAttuali");
                    int oro = rs.getInt("Oro");
                    int puntiStatistica = rs.getInt("PuntiSpendere");

                    Statistica statBase = new Statistica(
                            rs.getInt("costituzione_base"), rs.getInt("forza_base"), rs.getInt("destrezza_base"),
                            rs.getInt("intelligenza_base"), rs.getInt("fede_base"), rs.getInt("carisma_base"),
                            rs.getInt("fortuna_base"), 0, 0
                    );

                    Statistica modRazza = new Statistica(
                            rs.getInt("ModCostituzione"), rs.getInt("ModForza"), rs.getInt("ModDestrezza"),
                            rs.getInt("ModIntelligenza"), rs.getInt("ModFede"), rs.getInt("ModCarisma"),
                            rs.getInt("ModFortuna"), 0, 0
                    );

                    Razza razza = new Razza(rs.getString("nome_razza"), modRazza);
                    Classe classe = new Classe(rs.getString("nome_classe"));

                    Personaggio pg = new Personaggio(id, nome, classe, razza, statBase, hpCorrenti, manaCorrente, oro, puntiStatistica, isPg);
                    listaPersonaggi.add(pg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatiMancantiException("Errore nel caricamento dei personaggi: " + e.getMessage());
        }
    }

    /**
     * Carica i giocatori che partecipano a una campagna specifica per la dashboard del Master.
     * <p>
     * Se il giocatore ha già creato un personaggio per la campagna in questione,
     * viene istanziato un oggetto fittizzio del Personaggio contenente il solo ID.
     * Grazie all'override del metodo {@code equals()}, il sistema lo riconoscerà automaticamente
     * come equivalente al personaggio reale caricato nel metodo {@code leggiListaPersonaggi},
     * permettendo il corretto accoppiamento visivo nella GUI ed evitando il problema dello stato "Sconosciuto".
     * </p>
     *
     * @param partecipanti La lista (svuotata e ripopolata) che conterrà i {@link Giocatore} iscritti.
     * @param nomeCampagna La stringa che identifica la campagna da analizzare.
     * @throws DatiMancantiException In caso di errori durante la risoluzione relazionale SQL.
     */
    @Override
    public void leggiGiocatori(List<Giocatore> partecipanti, String nomeCampagna) throws DatiMancantiException {
        partecipanti.clear();

        String query = "SELECT u.CodUtente, u.Username, u.Email, u.Password, p.CodPersonaggio " +
                "FROM UTENTE u " +
                "JOIN ISCRIZIONE i ON u.CodUtente = i.CodUtente " +
                "JOIN CAMPAGNA c ON i.CodCampagna = c.CodCampagna " +
                "LEFT JOIN PERSONAGGIO p ON (p.CodUtente = u.CodUtente AND p.CodCampagna = c.CodCampagna) " +
                "WHERE u.Ruolo = 'Giocatore' AND c.Nome = ?";

        try{

             Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, nomeCampagna);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idGiocatore = rs.getInt("CodUtente");
                    String username = rs.getString("Username");
                    String email = rs.getString("Email");
                    String password = rs.getString("Password");
                    int idPersonaggio = rs.getInt("CodPersonaggio");

                    Giocatore giocatore = new Giocatore(email, username, password);
                    giocatore.setId(idGiocatore); // Mappiamo l'ID anche per il Giocatore iscritto!

                    if (idPersonaggio != 0) {
                        Personaggio pgFittizio = new Personaggio(idPersonaggio, null, null, null, null, 0, 0, 0, 0, true);
                        Campagna campagnaFittizia = new Campagna(nomeCampagna, 0, null);
                        giocatore.addPartecipazioneDati(campagnaFittizia, pgFittizio);
                    }

                    partecipanti.add(giocatore);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatiMancantiException("Errore critico durante il caricamento dei giocatori: " + e.getMessage());
        }
    }

    /**
     * Carica il catalogo degli oggetti disponibili per una specifica campagna.
     * <p>
     * Effettua una lettura  della tabella OGGETTO filtrando per {@code CodCampagna}.
     * Durante la lettura, filtra  tra oggetti di tipo "Consumabile"
     * e "Equipaggiamento", istanziando le relative sottoclassi e popolando i requisiti e i bonus.
     * </p>
     *
     * @param catalogo La lista (che verrà svuotata e ripopolata) destinata a contenere gli oggetti.
     * @param idCampagna L'identificativo della campagna da cui pescare il catalogo.
     */
    @Override
    public void leggiCatalogoOggetti(List<Oggetto> catalogo, int idCampagna) {
        catalogo.clear();
        String query = "SELECT CodOggetto, Nome, Costo, Tipo, ReqForza, ReqDestrezza, BonusForza, RipristinoHp, RipristinoMana " +
                "FROM OGGETTO WHERE CodCampagna = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idCampagna);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("CodOggetto");
                    String nome = rs.getString("Nome");
                    int costo = rs.getInt("Costo");
                    String tipo = rs.getString("Tipo");

                    if ("Consumabile".equalsIgnoreCase(tipo)) {
                        int hp = rs.getInt("RipristinoHp");
                        int mana = rs.getInt("RipristinoMana");

                        OggettoConsumabile consumabile = new OggettoConsumabile(id, nome, costo, tipo, hp, mana);
                        catalogo.add(consumabile);

                    } else if ("Equipaggiamento".equalsIgnoreCase(tipo)) {
                        Statistica requisiti = new Statistica(0, rs.getInt("ReqForza"), rs.getInt("ReqDestrezza"), 0, 0, 0, 0, 0, 0);
                        Statistica bonus = new Statistica(0, rs.getInt("BonusForza"), 0, 0, 0, 0, 0, 0, 0);

                        OggettoEquipaggiabile equipaggiabile = new OggettoEquipaggiabile(nome, costo, requisiti, bonus);
                        equipaggiabile.setId(id);
                        catalogo.add(equipaggiabile);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento del catalogo oggetti: " + e.getMessage());
        }
    }

    /**
     * Recupera l'elenco delle Razze abilitate per una specifica campagna.
     * <p>
     * Legge i modificatori di statistica associati alla razza e ricompone l'oggetto
     * mappando correttamente il suo ID nel database.
     * </p>
     *
     * @param lista La lista di destinazione in cui caricare le razze.
     * @param idCampagna L'identificativo della campagna di riferimento.
     */
    @Override
    public void leggiListaRazze(List<Razza> lista, int idCampagna) {
        lista.clear();
        String query = "SELECT CodRazza, Nome, ModCostituzione, ModForza, ModDestrezza, " +
                "ModIntelligenza, ModFede, ModCarisma, ModFortuna " +
                "FROM RAZZA WHERE CodCampagna = ?";

        try{
              Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, idCampagna);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("CodRazza");
                    String nome = rs.getString("Nome");

                    Statistica modificatori = new Statistica(
                            rs.getInt("ModCostituzione"), rs.getInt("ModForza"), rs.getInt("ModDestrezza"),
                            rs.getInt("ModIntelligenza"), rs.getInt("ModFede"), rs.getInt("ModCarisma"),
                            rs.getInt("ModFortuna"), 0, 0
                    );

                    Razza razza = new Razza(nome, modificatori);
                    razza.setId(id);
                    lista.add(razza);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento delle razze: " + e.getMessage());
        }
    }

    /**
     * Recupera l'elenco delle Classi previste in una specifica campagna.
     *
     * @param lista La lista di destinazione per le classi caricate.
     * @param idCampagna L'identificativo della campagna di riferimento.
     */
    @Override
    public void leggiListaClassi(List<Classe> lista, int idCampagna) {
        lista.clear();
        String query = "SELECT CodClasse, Nome FROM CLASSE WHERE CodCampagna = ?";

        try{
             Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, idCampagna);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("CodClasse");
                    String nome = rs.getString("Nome");

                    Classe classe = new Classe(id, nome);
                    lista.add(classe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento delle classi: " + e.getMessage());
        }
    }
}