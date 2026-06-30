package implementazionePostgresDAO;

import dao.StatisticaDAO;
import database.ConnessioneDatabase;
import model.Statistica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImplementazionePostgresStatistica implements StatisticaDAO {
    public void aggiornaStatistichePersonaggio(int idPersonaggio, Statistica modifiche) {
        String query = """
    UPDATE statistica
    SET forza = ?,
        destrezza = ?,
        costituzione = ?,
        intelligenza = ?,
        fede = ?,
        carisma = ?,
        fortuna = ?,
        hpmax = ?,
        manamax = ?,
        hpattuali = ?,  -- Imposta HP attuali uguali a HP Max
        manaattuali = ? -- Imposta Mana attuali uguali a Mana Max
    WHERE codpersonaggio = ?
    """;

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, modifiche.getForza());
            stmt.setInt(2, modifiche.getDestrezza());
            stmt.setInt(3, modifiche.getCostituzione());
            stmt.setInt(4, modifiche.getIntelligenza());
            stmt.setInt(5, modifiche.getFede());
            stmt.setInt(6, modifiche.getCarisma());
            stmt.setInt(7, modifiche.getFortuna());
            stmt.setInt(8, modifiche.getHpMax());
            stmt.setInt(9, modifiche.getManaMax());
            stmt.setInt(10, modifiche.getHpMax());
            stmt.setInt(11, modifiche.getManaMax());
            stmt.setInt(12, idPersonaggio);

            int righeCoinvolte = stmt.executeUpdate();

            if (righeCoinvolte == 0) {
                System.out.println("DEBUG: Nessuna statistica trovata per il personaggio con ID: " + idPersonaggio);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("DEBUG: Errore nell'aggiornamento dati del personaggio all'interno del database.");
        }
    }
}