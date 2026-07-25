package implementazionePostgresDAO;

import dao.CampagnaDAO;
import database.ConnessioneDatabase;
import exception.NomeCampagnaInUsoException;
import model.*;
import exception.DatiMancantiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
     */
    @Override
    public void leggiCampagne(HashMap<Campagna, Master> listaCampagne) {
        String query = "SELECT c.CodCampagna, c.Nome, c.MaxGiocatori, c.Stato, " +
                "u.CodUtente, u.Username, u.Email, u.Password " +
                "FROM CAMPAGNA c " +
                "INNER JOIN UTENTE u ON c.CodMaster = u.CodUtente";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int idMaster = rs.getInt("CodUtente");
                    String username = rs.getString("Username");
                    String email = rs.getString("Email");
                    String password = rs.getString("Password");
                    Master master = new Master(email, username, password);
                    master.setId(idMaster);

                    int idCampagna = rs.getInt("CodCampagna");
                    String nomeCampagna = rs.getString("Nome");
                    int maxGiocatori = rs.getInt("MaxGiocatori");
                    String statoDb = rs.getString("Stato");

                    Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, master);
                    campagna.setId(idCampagna);

                    campagna.setIniziata(!"Non Iniziata".equalsIgnoreCase(statoDb));

                    listaCampagne.put(campagna, master);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante il caricamento delle campagne dal database.");
        }
    }

    /**
     * Inserisce una nuova campagna di gioco all'interno del database generata dal Master.
     */
    @Override
    public int creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException {
        String query = "INSERT INTO CAMPAGNA (Nome, MaxGiocatori, Stato, CodMaster) " +
                "VALUES (?, ?, 'Non Iniziata', (SELECT CodUtente FROM UTENTE WHERE Username = ?)) " +
                "RETURNING CodCampagna";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, campagna.getNome());
                stmt.setInt(2, campagna.getMaxGiocatori());
                stmt.setString(3, campagna.getMaster().getUsername());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("CodCampagna");
                    } else {
                        throw new SQLException("Nessun ID generato dal database.");
                    }
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new NomeCampagnaInUsoException("Il nome della campagna è già in uso.");
            }
            e.printStackTrace();
            System.err.println("DEBUG: Errore salvataggio campagna in db");
            throw new RuntimeException("Errore durante il salvataggio della campagna: " + e.getMessage());
        }
    }

    /**
     * Rimuove definitivamente una campagna dal database partendo dal suo nome univoco.
     */
    @Override
    public void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException {
        String query = "DELETE FROM CAMPAGNA WHERE CodCampagna = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, campagnaTarget.getId());
                int righeModificate = stmt.executeUpdate();

                if (righeModificate == 0) {
                    throw new DatiMancantiException("Impossibile eliminare: La campagna '" + campagnaTarget.getNome() + "' non esiste.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante l'eliminazione della campagna.");
        }
    }

    /**
     * Carica i personaggi appartenenti a una specifica campagna filtrandoli per tipologia (PG o PnG).
     */
    @Override
    public void leggiListaPersonaggi(List<Personaggio> listaPersonaggi, boolean isPg, String nomeCampagna) throws DatiMancantiException {
        listaPersonaggi.clear();

        String query = "SELECT p.CodPersonaggio, p.Nome, p.Oro, p.IsPG, " +
                "c.Nome AS nome_classe, " +
                "r.Nome AS nome_razza, " +
                "sp.HpAttuali, sp.ManaAttuali, sp.PuntiSpendere, " +
                "sp.HpMax, sp.ManaMax, " +
                "sp.Forza AS forza_base, sp.Destrezza AS destrezza_base, sp.Costituzione AS costituzione_base, " +
                "sp.Intelligenza AS intelligenza_base, sp.Fede AS fede_base, sp.Carisma AS carisma_base, sp.Fortuna AS fortuna_base, " +
                "r.ModForza, r.ModDestrezza, r.ModCostituzione, r.ModIntelligenza, r.ModFede, r.ModCarisma, r.ModFortuna " +
                "FROM PERSONAGGIO p " +
                "JOIN CLASSE c ON p.CodClasse = c.CodClasse " +
                "JOIN RAZZA r ON p.CodRazza = r.CodRazza " +
                "JOIN CAMPAGNA cam ON p.CodCampagna = cam.CodCampagna " +
                "LEFT JOIN STATISTICA sp ON sp.CodPersonaggio = p.CodPersonaggio " +
                "WHERE p.IsPG = ? AND LOWER(cam.Nome) = LOWER(?)";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

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
                        int hpMax = rs.getInt("HpMax");
                        int manaMax = rs.getInt("ManaMax");

                        Statistica statBase = new Statistica(
                                rs.getInt("costituzione_base"), rs.getInt("forza_base"), rs.getInt("destrezza_base"),
                                rs.getInt("intelligenza_base"), rs.getInt("fede_base"), rs.getInt("carisma_base"),
                                rs.getInt("fortuna_base"), hpMax, manaMax
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatiMancantiException("Errore nel caricamento dei personaggi: " + e.getMessage());
        }
    }

    /**
     * Carica i giocatori che partecipano a una campagna specifica per la dashboard del Master.
     */
    @Override
    public void leggiGiocatori(List<Giocatore> partecipanti, String nomeCampagna) throws DatiMancantiException {
        partecipanti.clear();

        String query = "SELECT u.CodUtente, u.Username, u.Email, u.Password, p.CodPersonaggio, c.CodCampagna " +
                "FROM UTENTE u " +
                "JOIN ISCRIZIONE i ON u.CodUtente = i.CodUtente " +
                "JOIN CAMPAGNA c ON i.CodCampagna = c.CodCampagna " +
                "LEFT JOIN PERSONAGGIO p ON (p.CodUtente = u.CodUtente AND p.CodCampagna = c.CodCampagna) " +
                "WHERE u.Ruolo = 'Giocatore' AND LOWER(c.Nome) = LOWER(?)";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, nomeCampagna);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int idGiocatore = rs.getInt("CodUtente");
                        String username = rs.getString("Username");
                        String email = rs.getString("Email");
                        String password = rs.getString("Password");
                        int idPersonaggio = rs.getInt("CodPersonaggio");
                        int idCampagna = rs.getInt("CodCampagna");

                        Giocatore giocatore = new Giocatore(email, username, password);
                        giocatore.setId(idGiocatore);

                        if (idPersonaggio != 0) {
                            Personaggio pgFittizio = new Personaggio(idPersonaggio, null, null, null, null, 0, 0, 0, 0, true);
                            Campagna campagnaFittizia = new Campagna(idCampagna, nomeCampagna, 0, false, null);
                            giocatore.addPartecipazioneDati(campagnaFittizia, pgFittizio);
                        }

                        partecipanti.add(giocatore);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatiMancantiException("Errore critico durante il caricamento dei giocatori: " + e.getMessage());
        }
    }

    /**
     * Carica il catalogo degli oggetti disponibili per una specifica campagna.
     */
    @Override
    public List<Oggetto> caricaCatalogoNegozio(int idCampagna) {
        List<Oggetto> catalogo = new ArrayList<>();

        String query = "SELECT o.CodOggetto, o.Nome, o.Costo, o.Tipo, " +
                "eq.Req_Forza, eq.Req_Destrezza, eq.Req_Costituzione, eq.Req_Intelligenza, " +
                "eq.Req_Fede, eq.Req_Carisma, eq.Req_Fortuna, eq.Req_HpMax, eq.Req_ManaMax, " +
                "eq.Bonus_Forza, eq.Bonus_Destrezza, eq.Bonus_Costituzione, eq.Bonus_Intelligenza, " +
                "eq.Bonus_Fede, eq.Bonus_Carisma, eq.Bonus_Fortuna, eq.Bonus_HpMax, eq.Bonus_ManaMax, " +
                "con.RipristinoHp, con.RipristinoMana " +
                "FROM OGGETTO o " +
                "LEFT JOIN OGGETTO_EQUIPAGGIABILE eq ON o.CodOggetto = eq.CodOggetto " +
                "LEFT JOIN OGGETTO_CONSUMABILE con ON o.CodOggetto = con.CodOggetto " +
                "WHERE o.CodCampagna = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idCampagna);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("CodOggetto");
                        String nome = rs.getString("Nome");
                        int costo = rs.getInt("Costo");
                        String tipo = rs.getString("Tipo");

                        if ("Consumabile".equalsIgnoreCase(tipo)) {
                            catalogo.add(new OggettoConsumabile(id, nome, costo, tipo, rs.getInt("RipristinoHp"), rs.getInt("RipristinoMana")));
                        } else if ("Equipaggiamento".equalsIgnoreCase(tipo)) {
                            Statistica req = new Statistica(rs.getInt("Req_Costituzione"), rs.getInt("Req_Forza"), rs.getInt("Req_Destrezza"), rs.getInt("Req_Intelligenza"), rs.getInt("Req_Fede"), rs.getInt("Req_Carisma"), rs.getInt("Req_Fortuna"), rs.getInt("Req_HpMax"), rs.getInt("Req_ManaMax"));
                            Statistica bon = new Statistica(rs.getInt("Bonus_Costituzione"), rs.getInt("Bonus_Forza"), rs.getInt("Bonus_Destrezza"), rs.getInt("Bonus_Intelligenza"), rs.getInt("Bonus_Fede"), rs.getInt("Bonus_Carisma"), rs.getInt("Bonus_Fortuna"), rs.getInt("Bonus_HpMax"), rs.getInt("Bonus_ManaMax"));
                            catalogo.add(new OggettoEquipaggiabile(id, nome, costo, tipo, req, bon));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore caricamento catalogo: " + e.getMessage());
        }

        return catalogo;
    }

    /**
     * Recupera l'elenco delle Razze abilitate per una specifica campagna.
     */
    @Override
    public void leggiListaRazze(List<Razza> lista, int idCampagna) {
        lista.clear();
        String query = "SELECT CodRazza, Nome, Descrizione, ModCostituzione, ModForza, ModDestrezza, " +
                "ModIntelligenza, ModFede, ModCarisma, ModFortuna, ModHpMax, ModManaMax " +
                "FROM RAZZA WHERE CodCampagna = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, idCampagna);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("CodRazza");
                        String nome = rs.getString("Nome");
                        String descrizione = rs.getString("Descrizione");

                        Statistica modificatori = new Statistica(
                                rs.getInt("ModCostituzione"), rs.getInt("ModForza"), rs.getInt("ModDestrezza"),
                                rs.getInt("ModIntelligenza"), rs.getInt("ModFede"), rs.getInt("ModCarisma"),
                                rs.getInt("ModFortuna"), rs.getInt("ModHpMax"), rs.getInt("ModManaMax")
                        );

                        Razza razza = new Razza(nome, modificatori);
                        razza.setId(id);
                        razza.setDescrizione(descrizione);
                        lista.add(razza);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento delle razze: " + e.getMessage());
        }
    }

    /**
     * Recupera l'elenco delle Classi previste in una specifica campagna.
     */
    @Override
    public void leggiListaClassi(List<Classe> lista, int idCampagna) {
        lista.clear();
        String query = "SELECT CodClasse, Nome, Descrizione FROM CLASSE WHERE CodCampagna = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, idCampagna);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("CodClasse");
                        String nome = rs.getString("Nome");
                        String descrizione = rs.getString("Descrizione");

                        Classe classe = new Classe(id, nome);
                        classe.setDescrizione(descrizione);
                        lista.add(classe);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento delle classi: " + e.getMessage());
        }
    }

    @Override
    public void cambiaStato(int id, boolean stato) {
        String query = "UPDATE CAMPAGNA SET Stato = ? WHERE CodCampagna = ?";
        String statoStringa = stato ? "In Corso" : "Non Iniziata";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, statoStringa);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Impossibile aggiornare lo stato della campagna, dati corrotti.");
        }
    }

    @Override
    public int contaPartecipanti(int codCampagna) {
        String query = "SELECT COUNT(*) FROM ISCRIZIONE WHERE CodCampagna = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, codCampagna);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il conteggio dei partecipanti alla campagna: " + e.getMessage());
        }
        return 0;
    }
}