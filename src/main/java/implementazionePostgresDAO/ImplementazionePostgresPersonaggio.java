package implementazionePostgresDAO;

import dao.PersonaggioDAO;
import database.ConnessioneDatabase;
import model.Personaggio;
import model.Statistica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione PostgreSQL dell'interfaccia {@link PersonaggioDAO}.
 * <p>
 * Consente aggiornamenti mirati sul singolo personaggio come modifiche di statistiche base e
 * supporta letture specializzate.
 * </p>
 */
public class ImplementazionePostgresPersonaggio implements PersonaggioDAO {

    /**
     * Sovrascrive permanentemente la striscia statistica completa di uno specifico personaggio nel DB.
     *
     * @param idPersonaggio il codice del personaggio alterato da applicare alla clausola where.
     * @param modifiche     l'oggetto statistica recante i nuovi valori ricalcolati in base ai punti.
     */
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

    /**
     * Interroga la base di dati e ottiene una scansione preliminare del file di inventario
     * a uso debuggistico e di fetch generico.
     *
     * @param idPersonaggio l'id del personaggio dal quale fetchare la join tra le tabelle di riferimento.
     * @param personaggio   l'oggetto istanziato che recepirà gli ingressi parsati dal reader sql.
     */
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