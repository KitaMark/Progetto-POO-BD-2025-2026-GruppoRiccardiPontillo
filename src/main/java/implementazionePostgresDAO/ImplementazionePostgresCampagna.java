package implementazionePostgresDAO;

import dao.CampagnaDAO;
import database.ConnessioneDatabase;
import exception.NomeCampagnaInUsoException;
import model.Campagna;
import model.Giocatore;
import model.Master;
import database.ConnessioneDatabase;
import exception.DatiMancantiException;
import model.Personaggio;
import model.Statistica;
import model.Razza;
import model.Classe;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Implementazione concreta dell'interfaccia {@link CampagnaDAO} per il database PostgreSQL.
 * Gestisce il recupero massivo e l'eliminazione delle campagne di gioco, occupandosi di
 * ricostruire le relazioni tra le entità (Campagna e Master) tramite query.
 * * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresCampagna implements CampagnaDAO {

    /**
     * Recupera tutte le campagne presenti nel database e popola la mappa fornita in input.
     * @param listaCampagne La mappa (inizialmente vuota) in cui inserire le campagne estratte.
     * La chiave sarà l'oggetto Campagna, il valore sarà l'oggetto Master.
     * @throws RuntimeException Se si verifica un errore critico durante la comunicazione col database.
     */
    @Override
    public void leggiCampagne(HashMap<Campagna, Master> listaCampagne) {
        String query = "SELECT c.Nome, c.MaxGiocatori, c.Stato, u.Username, u.Email, u.Password " +
                "FROM CAMPAGNA c " +
                "INNER JOIN UTENTE u ON c.CodMaster = u.CodUtente";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()){


            while (rs.next()) {
                // 1. Dati Master
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                Master master = new Master(email, username, password);

                // 2. Dati Campagna
                String nomeCampagna = rs.getString("Nome");
                int maxGiocatori = rs.getInt("MaxGiocatori");
                String statoDb = rs.getString("Stato"); // equivalente isIniziata

                Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, master);

                //per effettivamente settare lo stati della campagna in java rispetto al db
                if ("Non Iniziata".equals(statoDb)) {
                    campagna.setIniziata(false);
                } else {
                    // Se invece il contrario
                    campagna.setIniziata(true);
                }

                // 3. Inserisco la coppia nella HashMap
                listaCampagne.put(campagna, master);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante il caricamento delle campagne dal database.");
        }
    }

    /**
     * Inserisce una nuova campagna di gioco all'interno del database.
     * Il metodo utilizza una sotto-query SQL per recuperare dinamicamente
     * l'identificativo (CodUtente) del Master a partire dal suo Username,
     * garantendo la corretta associazione della chiave esterna.
     *
     * @param campagna L'oggetto {@link Campagna} contenente i dati da persistere.
     * @throws NomeCampagnaInUsoException Se l'inserimento fallisce, presumibilmente perché
     * il nome scelto per la campagna è già presente nel database (violazione vincolo UNIQUE).
     */
    @Override
    public void creaCampagna(Campagna campagna) throws NomeCampagnaInUsoException {
        String query = "INSERT INTO CAMPAGNA (Nome, MaxGiocatori, Stato, CodMaster) " +
                "VALUES (?, ?, 'Non Iniziata', (SELECT CodUtente FROM UTENTE WHERE Username = ?))";

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, campagna.getNome());
            stmt.setInt(2, campagna.getMaxGiocatori());
            stmt.setString(3, campagna.getMaster().getUsername());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NomeCampagnaInUsoException("Creazione fallita: Il nome '" + campagna.getNome() + "' è già utilizzato da un'altra campagna!");
        }
    }

    /**
     * Rimuove definitivamente una campagna dal database partendo dal suo nome.
     * L'eliminazione della campagna comporterà la rimozione automatica di tutte
     * le iscrizioni e dei personaggi ad essa associati (ON DELETE CASCADE).
     *
     * @param campagnaTarget la {@link Campagna} da eliminare.
     * @throws DatiMancantiException Se il database non trova nessuna campagna con il nome
     * specificato (nessuna riga modificata).
     * @throws RuntimeException      Se si verifica un errore critico durante la comunicazione
     * con il database.
     */

    @Override
    public void eliminaCampagna(Campagna campagnaTarget) throws DatiMancantiException {
        String query = "DELETE FROM CAMPAGNA WHERE Nome = ?";

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);) {


            stmt.setString(1, campagnaTarget.getNome());

            // executeUpdate() restituisce quante righe ha cancellato
            int righeModificate = stmt.executeUpdate();

            // Se ha cancellato 0 righe, la campagna non esisteva nel db
            if (righeModificate == 0) {
                throw new DatiMancantiException("Impossibile eliminare: La campagna '" + campagnaTarget.getNome() + "' non esiste.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante l'eliminazione della campagna.");
        }
    }

    @Override
    public void leggiListaPersonaggi(List<Personaggio> listaPersonaggi, boolean isPg) throws DatiMancantiException {
         //ho fatto solo una query poichè presente nel db attributo che controlla se il personaggio è png o pg
        String query = "SELECT p.CodPersonaggio, p.Nome, p.Oro, p.IsPG, " +
                "c.Nome AS nome_classe, " +
                "r.Nome AS nome_razza, " +
                "sp.HpAttuali, sp.ManaAttuali, sp.PuntiSpendere, " +
                "sp.Forza AS forza_base, sp.Destrezza AS destrezza_base, sp.Costituzione AS costituzione_base, sp.Intelligenza AS intelligenza_base, sp.Fede AS fede_base, sp.Carisma AS carisma_base, sp.Fortuna AS fortuna_base, " +
                "sr.Forza AS mod_forza, sr.Destrezza AS mod_destrezza, sr.Costituzione AS mod_costituzione, sr.Intelligenza AS mod_intelligenza, sr.Fede AS mod_fede, sr.Carisma AS mod_carisma, sr.Fortuna AS mod_fortuna " +
                "FROM PERSONAGGIO p " +
                "JOIN CLASSE c ON p.CodClasse = c.CodClasse " +
                "JOIN RAZZA r ON p.CodRazza = r.CodRazza " +
                "LEFT JOIN STATISTICA sp ON sp.CodPersonaggio = p.CodPersonaggio " +
                "LEFT JOIN STATISTICA sr ON sr.CodRazza = r.CodRazza " +
                "WHERE p.IsPG = ?";

        // Il try-with-resources chiude automaticamente pstmt e ResultSet
        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isPg);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Dati PG
                    int id = rs.getInt("CodPersonaggio");
                    String nome = rs.getString("Nome");
                    int hpCorrenti = rs.getInt("HpAttuali");
                    int manaCorrente = rs.getInt("ManaAttuali");
                    int oro = rs.getInt("Oro");
                    int puntiStatistica = rs.getInt("PuntiSpendere");

                    // Statistiche base del personaggio
                    Statistica statBase = new Statistica();
                    statBase.setForza(rs.getInt("forza_base"));
                    statBase.setDestrezza(rs.getInt("destrezza_base"));
                    statBase.setCostituzione(rs.getInt("costituzione_base"));
                    statBase.setIntelligenza(rs.getInt("intelligenza_base"));
                    statBase.setFede(rs.getInt("fede_base"));
                    statBase.setCarisma(rs.getInt("carisma_base"));
                    statBase.setFortuna(rs.getInt("fortuna_base"));

                    // Modificatori provenienti dalla razza
                    Statistica modRazza = new Statistica();
                    modRazza.setForza(rs.getInt("mod_forza"));
                    modRazza.setDestrezza(rs.getInt("mod_destrezza"));
                    modRazza.setCostituzione(rs.getInt("mod_costituzione"));
                    modRazza.setIntelligenza(rs.getInt("mod_intelligenza"));
                    modRazza.setFede(rs.getInt("mod_fede"));
                    modRazza.setCarisma(rs.getInt("mod_carisma"));
                    modRazza.setFortuna(rs.getInt("mod_fortuna"));

                    Razza razza = new Razza(rs.getString("nome_razza"), modRazza);
                    Classe classe = new Classe(rs.getString("nome_classe"));

                    // Applichiamo i modificatori
                    statBase.aggiungiBonus(modRazza);

                    //per implementare tutto al meglio ho sovuto creare un costruttore apposito per il dao nel model
                    //di personaggio
                    Personaggio pg = new Personaggio(id, nome, classe, razza, statBase, hpCorrenti, manaCorrente, oro, puntiStatistica, isPg);

                    listaPersonaggi.add(pg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatiMancantiException("Errore critico durante il caricamento dei personaggi: " + e.getMessage());
        }
    }

    @Override
    public void leggiGiocatori(List<Giocatore> partecipanti) throws DatiMancantiException {

        String query = "SELECT Username, Email, Password FROM UTENTE WHERE Ruolo = 'Giocatore'";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("Password");

                Giocatore giocatore = new Giocatore(username, email, password);
                partecipanti.add(giocatore);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatiMancantiException("Errore critico durante il caricamento dei giocatori: " + e.getMessage());
        }
    }
}