package implementazionePostgresDAO;

import dao.MasterDAO;
import model.Campagna;
import database.ConnessioneDatabase;
import exception.DatiMancantiException;
import exception.NomeCampagnaInUsoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementazione concreta dell'interfaccia {@link MasterDAO} per il database PostgreSQL.
 * Gestisce le operazioni di persistenza esclusive del ruolo Master, traducendo
 * le chiamate ad alto livello in query SQL.
 * * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresMaster implements MasterDAO {

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

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);

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
     * @param nomeCampagna Il nome testuale identificativo della campagna da eliminare.
     * @throws DatiMancantiException Se il database non trova nessuna campagna con il nome
     * specificato (nessuna riga modificata).
     * @throws RuntimeException      Se si verifica un errore critico durante la comunicazione
     * con il database.
     */
    @Override
    public void eliminaCampagna(String nomeCampagna) throws DatiMancantiException {
        String query = "DELETE FROM CAMPAGNA WHERE Nome = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nomeCampagna);

            // executeUpdate() restituisce quante righe ha cancellato
            int righeModificate = stmt.executeUpdate();

            // Se ha cancellato 0 righe, la campagna non esisteva nel db
            if (righeModificate == 0) {
                throw new DatiMancantiException("Impossibile eliminare: La campagna '" + nomeCampagna + "' non esiste.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante l'eliminazione della campagna.");
        }
    }
}