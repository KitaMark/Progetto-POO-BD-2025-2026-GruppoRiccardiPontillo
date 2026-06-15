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
    }
    //vuota per ora
}