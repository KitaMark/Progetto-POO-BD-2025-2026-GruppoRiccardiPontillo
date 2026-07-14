package implementazionePostgresDAO;

import dao.PersonaggioDAO;
import database.ConnessioneDatabase;
import model.Personaggio;
import model.Statistica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImplementazionePostgresPersonaggio implements PersonaggioDAO {
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
        hpattuali = ?,  
        manaattuali = ? 
    WHERE codpersonaggio = ?
    """;

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);) {

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

    public void leggiInventarioPersonaggio(int idPersonaggio, Personaggio personaggio){
        String query = """
                SELECT o.*
                FROM public.oggetto o
                JOIN public.inventario i ON o.codoggetto = i.codoggetto
                WHERE i.codpersonaggio = ?;
                """;
        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);){
            stmt.setInt(1, idPersonaggio);
            try(ResultSet rs = stmt.executeQuery()){
               while(rs.next()){
                   //TODO
               }
            } catch(SQLException ex){
                ex.printStackTrace();
                System.err.println("DEBUG: Errore durante la lettura dell'inventario.");
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            System.err.println("DEBUG: Errore connessione a db per lettura inventario.");
        }

    }
}