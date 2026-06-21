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

/**
 * Implementazione specifica per PostgreSQL dell'interfaccia {@link AbilitaDao}.
 * <p>
 * Gestisce la persistenza e il recupero dei dati relativi alle abilità dei personaggi
 * e delle classi all'interno del database relazionale, interfacciandosi con le tabelle
 * {@code ABILITA} e la tabella ponte {@code PERSONAGGIO_ABILITA}.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresAbilita implements AbilitaDao {


    /**
     * Registra nel database l'apprendimento di una nuova abilità da parte di un personaggio.
     * <p>
     * Sfrutta una sottoquery per recuperare il {@code CodAbilita} a partire dal nome testuale dell'abilità
     * e inserisce la coppia identificativa all'interno della tabella ponte {@code PERSONAGGIO_ABILITA}.
     * </p>
     *
     * @param codPersonaggio l'identificativo univoco del personaggio che impara l'abilità.
     * @param nomeAbilita    il nome dell'abilità da associare al personaggio.
     * @throws RuntimeException se si verifica un errore SQL durante l'esecuzione della query.
     */
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

    /**
     * Carica dal database tutte le abilità sbloccabili associate a una specifica classe di gioco.
     * <p>
     * Il metodo svuota preventivamente la lista in memoria della classe per evitare duplicati,
     * esegue una sottoquery per filtrare le abilità in base al nome della classe e popola
     * l'oggetto {@link Classe} con le istanze di {@link Abilita} trovate.
     * </p>
     *
     * @param classe l'oggetto {@link Classe} a cui associare le abilità sbloccabili caricate.
     */
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


    /**
     * Recupera dal database le abilità che un determinato personaggio ha già appreso.
     * <p>
     * Effettua un'operazione di {@code JOIN} tra la tabella ponte {@code PERSONAGGIO_ABILITA}
     * e la tabella {@code ABILITA} filtrando per l'ID del personaggio. Le abilità recuperate
     * vengono istanziate e inserite direttamente nella lista interna dell'oggetto {@link Personaggio}.
     * </p>
     *
     * @param pg l'oggetto {@link Personaggio} di cui caricare le abilità apprese.
     */
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