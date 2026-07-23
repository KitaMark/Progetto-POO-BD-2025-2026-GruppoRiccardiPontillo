package implementazionePostgresDAO;

import dao.RazzaDao;
import model.Razza;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImplementazionePostgresRazza implements RazzaDao {

    @Override
    public int salvaRazza(Razza razza, String descrizione, int idCampagna) throws Exception {
        String query = "INSERT INTO RAZZA " +
                "(Nome, Descrizione, CodCampagna, ModCostituzione, ModForza, ModDestrezza, " +
                "ModIntelligenza, ModFede, ModCarisma, ModFortuna, ModHpMax, ModManaMax) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING CodRazza";

        // NB: la Connection è il singleton condiviso: NON va chiusa qui (niente try-with-resources su di essa),
        // altrimenti si chiude per tutta l'applicazione.
        Connection connection = ConnessioneDatabase.getInstance().connection;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            // Parametri base
            stmt.setString(1, razza.getNome());
            stmt.setString(2, descrizione);
            stmt.setInt(3, idCampagna);

            // Parametri dei modificatori prelevati dall'oggetto Statistica interno
            stmt.setInt(4, razza.getModificatori().getCostituzione());
            stmt.setInt(5, razza.getModificatori().getForza());
            stmt.setInt(6, razza.getModificatori().getDestrezza());
            stmt.setInt(7, razza.getModificatori().getIntelligenza());
            stmt.setInt(8, razza.getModificatori().getFede());
            stmt.setInt(9, razza.getModificatori().getCarisma());
            stmt.setInt(10, razza.getModificatori().getFortuna());
            stmt.setInt(11, razza.getModificatori().getHpMax());
            stmt.setInt(12, razza.getModificatori().getManaMax());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idGenerato = rs.getInt(1);
                    razza.setDescrizione(descrizione);
                    return idGenerato;
                } else {
                    throw new Exception("Errore: nessun ID generato dal database per la nuova razza.");
                }
            }

        } catch (SQLException e) {
            throw new Exception("Errore di connessione al DB durante il salvataggio della razza: " + e.getMessage());
        }
    }
}