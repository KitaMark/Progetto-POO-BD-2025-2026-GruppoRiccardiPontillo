package implementazionePostgresDAO;

import dao.MasterDAO;
import model.Campagna;
import database.ConnessioneDatabase;
import exception.DatiMancantiException;
import exception.NomeCampagnaInUsoException;
import model.Personaggio;

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
    @Override
    public void rimuoviPersonaggio(Personaggio pg) {
        String query = "DELETE FROM PERSONAGGIO WHERE CodPersonaggio = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, pg.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del personaggio: " + e.getMessage());
        }
    }

    @Override
    public void assegnaPuntiStatistica(Personaggio personaggio, int quantitaPunti) {
        String query = "UPDATE STATISTICA SET PuntiSpendere = PuntiSpendere + ? WHERE CodPersonaggio = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, quantitaPunti);
            stmt.setInt(2, personaggio.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento dei punti: " + e.getMessage());
        }
    }

    //vuota per ora
}