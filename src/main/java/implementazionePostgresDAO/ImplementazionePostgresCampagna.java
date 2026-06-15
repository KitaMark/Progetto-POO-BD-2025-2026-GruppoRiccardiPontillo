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
    public void leggiListaPersonaggi(List<Personaggio> listaPersonaggi, boolean isPg, String nomeCampagna) throws DatiMancantiException {
        listaPersonaggi.clear();

        // Query modificata: aggiunta la JOIN con CAMPAGNA per filtrare tramite il Nome della campagna
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
                "JOIN CAMPAGNA cam ON p.CodCampagna = cam.CodCampagna " + // Nuova JOIN
                "LEFT JOIN STATISTICA sp ON sp.CodPersonaggio = p.CodPersonaggio " +
                "WHERE p.IsPG = ? AND cam.Nome = ?"; // Filtro sul nome della campagna

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isPg);
            pstmt.setString(2, nomeCampagna); // Impostiamo la stringa del nome campagna

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("CodPersonaggio");
                    String nome = rs.getString("Nome");
                    int hpCorrenti = rs.getInt("HpAttuali");
                    int manaCorrente = rs.getInt("ManaAttuali");
                    int oro = rs.getInt("Oro");
                    int puntiStatistica = rs.getInt("PuntiSpendere");

                    Statistica statBase = new Statistica(
                            rs.getInt("costituzione_base"),
                            rs.getInt("forza_base"),
                            rs.getInt("destrezza_base"),
                            rs.getInt("intelligenza_base"),
                            rs.getInt("fede_base"),
                            rs.getInt("carisma_base"),
                            rs.getInt("fortuna_base"),
                            0, 0
                    );

                    Statistica modRazza = new Statistica(
                            rs.getInt("ModCostituzione"),
                            rs.getInt("ModForza"),
                            rs.getInt("ModDestrezza"),
                            rs.getInt("ModIntelligenza"),
                            rs.getInt("ModFede"),
                            rs.getInt("ModCarisma"),
                            rs.getInt("ModFortuna"),
                            0, 0
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

    @Override
    public void leggiGiocatori(List<Giocatore> partecipanti, String nomeCampagna) throws DatiMancantiException {
        // Svuotiamo la lista per evitare duplicati in caso di chiamate ripetute
        partecipanti.clear();
        // Query con JOIN per prendere solo i giocatori iscritti a QUELLA campagna
        // e per estrarre anche il codice del personaggio che possiedono in quella sessione
        String query = "SELECT u.Username, u.Email, u.Password, p.CodPersonaggio " +
                "FROM UTENTE u " +
                "JOIN ISCRIZIONE i ON u.CodUtente = i.CodUtente " +
                "JOIN CAMPAGNA c ON i.CodCampagna = c.CodCampagna " +
                "LEFT JOIN PERSONAGGIO p ON (p.CodUtente = u.CodUtente AND p.CodCampagna = c.CodCampagna) " +
                "WHERE u.Ruolo = 'Giocatore' AND c.Nome = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nomeCampagna);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("Username");
                    String email = rs.getString("Email");
                    String password = rs.getString("Password");
                    int idPersonaggio = rs.getInt("CodPersonaggio");

                    // 1. Creiamo il giocatore
                    Giocatore giocatore = new Giocatore(email, username, password);

                    // 2. Risolviamo il problema di "Sconosciuto":
                    // Se il giocatore ha un personaggio in questa campagna (idPersonaggio != 0),
                    // dobbiamo inserirlo nella sua mappa delle partecipazioni!
                    if (idPersonaggio != 0) {
                        // Creiamo un oggetto Personaggio fittizio (o "proxy") che ha lo stesso ID.
                        // Grazie all'override di .equals() basato sull'ID che hai messo in Personaggio,
                        // Java lo riconoscerà come IDENTICO a quello caricato da leggiListaPersonaggi!
                        Personaggio pgFittizio = new Personaggio(idPersonaggio, null, null, null, null, 0, 0, 0, 0, true);

                        // Creiamo un oggetto Campagna fittizio con lo stesso nome per fare da chiave
                        Campagna campagnaFittizia = new Campagna(nomeCampagna, 0, null);

                        // Per aggirare l'unmodifiableMap del getter, dobbiamo inserire il dato
                        // sfruttando la logica di inizializzazione dell'oggetto, oppure se hai un metodo
                        // addPartecipazione(Campagna, Personaggio) dentro Giocatore usa quello.
                        // Se non hai quel metodo, inserisci questo trucco di riflessione o un metodo d'appoggio:
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
}