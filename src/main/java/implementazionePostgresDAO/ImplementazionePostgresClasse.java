package implementazionePostgresDAO;

import dao.ClasseDao;
import model.Classe;
import database.ConnessioneDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImplementazionePostgresClasse implements ClasseDao {

    /**
     * Crea una nuova Classe associata alla campagna attiva e la salva nel database.
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