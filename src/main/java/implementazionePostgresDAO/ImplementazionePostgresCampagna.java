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
    public void leggiListaPersonaggi(List<Personaggio> listaPersonaggi, boolean isPg){
        //TODO: implementare
        //pensavo di fare assegnazione condizionale per la query, tipo:
        //String query = isPg()? //qui scrivi la query per cercare tra i PG : //qui scrivi la query per i PnG
    }

    @Override
    public void leggiGiocatori(List<Giocatore> partecipanti) {
        //TODO: implementare
    }
}