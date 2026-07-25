package implementazionePostgresDAO;

import dao.ClasseDao;
import model.Classe;
import database.ConnessioneDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione specifica per PostgreSQL dell'interfaccia {@link ClasseDao}.
 * <p>
 * Si occupa dell'inserimento e la corretta propagazione nel database delle configurazioni
 * riguardanti le nuove Classi generate dal Master all'interno di una specifica Campagna.
 * </p>
 */
public class ImplementazionePostgresClasse implements ClasseDao {

    /**
     * Crea una nuova Classe associata alla campagna attiva e la salva nel database.
     *
     * @param classe      l'istanza di tipo {@link Classe} popolata dal controller.
     * @param descrizione una stringa di approfondimento della lore e background di tale classe.
     * @param idCampagna  l'identificativo della campagna che conterrà la classe.
     * @return l'identificativo intero della chiave primaria autogenerata all'interno del DB.
     * @throws RuntimeException se un problema relativo alla connessione impedisce la scrittura o fallisce la restituzione dell'ID.
     */
    @Override
    public int salvaClasse(Classe classe, String descrizione, int idCampagna) {
        String query = "INSERT INTO CLASSE (Nome, Descrizione, CodCampagna) VALUES (?, ?, ?) RETURNING CodClasse";

        try (PreparedStatement stmt = ConnessioneDatabase.getInstance().connection.prepareStatement(query)) {

            stmt.setString(1, classe.getNome());
            stmt.setString(2, descrizione);
            stmt.setInt(3, idCampagna);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idGenerato = rs.getInt(1);
                    // Sincronizza l'oggetto in RAM col DB, senza toccare il costruttore
                    classe.setDescrizione(descrizione);
                    return idGenerato;
                } else {
                    throw new RuntimeException("Errore di sistema: nessun ID generato dal database per la nuova classe.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore di connessione al DB durante il salvataggio della classe: " + e.getMessage());
        }
    }
}