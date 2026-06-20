package implementazionePostgresDAO;

import dao.AbilitaDao;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import model.Abilita;
import model.Personaggio;
import model.Classe;

public class ImplementazionePostgresAbilita implements AbilitaDao {

    @Override
    public void imparaAbilita(int codPersonaggio, String nomeAbilita) {
        String query = "INSERT INTO PERSONAGGIO_ABILITA (CodPersonaggio, CodAbilita) " +
                "SELECT ?, CodAbilita FROM ABILITA WHERE Nome = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, codPersonaggio);
           stmt.setString(2, nomeAbilita);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore di comunicazione col database durante l'apprendimento: " + e.getMessage());
        }
    }

    @Override
    public void caricaAbilitaSbloccabili(Classe classe) {
        classe.getAbilitaSbloccabili().clear(); // Svuota per evitare doppioni se ricarichi

        // Cerca le abilità che corrispondono al nome della classe
        String query = "SELECT Nome, Descrizione FROM ABILITA WHERE CodClasse = (SELECT CodClasse FROM CLASSE WHERE Nome = ?)";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, classe.getNome());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("Nome");
                String descrizione = rs.getString("Descrizione");

                Abilita abilita = new Abilita(nome, descrizione, classe);
                classe.getAbilitaSbloccabili().add(abilita);
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento abilità della classe: " + e.getMessage());
        }
    }

    @Override
    public void caricaAbilitaApprese(Personaggio pg) {
        pg.getListaAbilita().clear(); // Svuota la lista in memoria

        // Legge dalla tabella ponte (PERSONAGGIO_ABILITA)
        String query = "SELECT a.Nome, a.Descrizione FROM PERSONAGGIO_ABILITA pa " +
                "JOIN ABILITA a ON pa.CodAbilita = a.CodAbilita " +
                "WHERE pa.CodPersonaggio = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, pg.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("Nome");
                String descrizione = rs.getString("Descrizione");

                Abilita abilitaAppresa = new Abilita(nome, descrizione, pg.getClasse());
                pg.addAbilita(abilitaAppresa);
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento abilità apprese: " + e.getMessage());
        }
    }
}