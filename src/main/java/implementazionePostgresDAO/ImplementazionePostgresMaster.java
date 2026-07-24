package implementazionePostgresDAO;

import dao.MasterDAO;
import database.ConnessioneDatabase;
import model.Giocatore;
import model.Personaggio;
import model.Statistica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        try{
            Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, pg.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'eliminazione del personaggio: " + e.getMessage());
        }
    }

    @Override
    public void assegnaPuntiStatistica(Personaggio personaggio, int quantitaPunti) {
        String query = "UPDATE STATISTICA SET PuntiSpendere = PuntiSpendere + ? WHERE CodPersonaggio = ?";

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);) {


            stmt.setInt(1, quantitaPunti);
            stmt.setInt(2, personaggio.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'aggiornamento dei punti: " + e.getMessage());
        }
    }

    @Override
    public void creaPnG(Personaggio png, int codCampagna) {

        String query = """
        WITH nuovo_png AS (
            INSERT INTO PERSONAGGIO (Nome, Oro, IsPG, CodUtente, CodCampagna, CodClasse, CodRazza) 
            VALUES (
                ?, ?, FALSE, NULL, ?, 
                (SELECT CodClasse FROM CLASSE WHERE Nome = ?), 
                (SELECT CodRazza FROM RAZZA WHERE Nome = ?)
            ) RETURNING CodPersonaggio
        )
        INSERT INTO STATISTICA (Forza, Destrezza, Costituzione, Intelligenza, Fede, Carisma, Fortuna, HpMax, HpAttuali, ManaMax, ManaAttuali, CodPersonaggio) 
        SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CodPersonaggio FROM nuovo_png
        RETURNING CodPersonaggio;
        """;

        try(Connection connection = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, png.getNome());
            stmt.setInt(2, png.getOro());
            stmt.setInt(3, codCampagna);
            stmt.setString(4, png.getClasse().getNome());
            stmt.setString(5, png.getRazza().getNome());

            Statistica s = png.getStatisticheBase();
            stmt.setInt(6, s.getForza());
            stmt.setInt(7, s.getDestrezza());
            stmt.setInt(8, s.getCostituzione());
            stmt.setInt(9, s.getIntelligenza());
            stmt.setInt(10, s.getFede());
            stmt.setInt(11, s.getCarisma());
            stmt.setInt(12, s.getFortuna());
            stmt.setInt(13, s.getHpMax());
            stmt.setInt(14, png.getHpCorrenti());
            stmt.setInt(15, s.getManaMax());
            stmt.setInt(16, png.getManaCorrente());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idGenerato = rs.getInt("CodPersonaggio");
                png.setId(idGenerato);

            } else {
                throw new SQLException("Creazione fallita: nessun ID restituito dal database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il salvataggio del personaggio: " + e.getMessage());
        }
    }


    @Override
    public void rimuoviGiocatore(int idGiocatore, int idCampagna) {
        String query = "DELETE FROM PERSONAGGIO WHERE CodUtente = ? AND CodCampagna = ?"+
        "DELETE FROM ISCRIZIONE WHERE CodUtente = ? AND CodCampagna = ?";

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, idGiocatore);
            stmt.setInt(2, idCampagna);

            stmt.setInt(3, idGiocatore);
            stmt.setInt(4, idCampagna);
            stmt.executeUpdate();
        } catch (SQLException ex){
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            throw new RuntimeException("Impossibile rimuovere il giocatore selezionato dalla campagna - dati corrotti.");
        }
    }

}