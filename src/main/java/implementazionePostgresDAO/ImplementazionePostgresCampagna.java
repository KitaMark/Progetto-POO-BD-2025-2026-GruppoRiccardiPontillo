package implementazionePostgresDAO;

import dao.CampagnaDAO;
import model.Campagna;
import model.Master;
import database.ConnessioneDatabase;
import exception.DatiMancantiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Implementazione concreta dell'interfaccia {@link CampagnaDAO} per il database PostgreSQL.
 * Gestisce il recupero massivo e l'eliminazione delle campagne di gioco, occupandosi di
 * ricostruire le relazioni tra le entità (Campagna e Master) tramite query.
 * * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresCampagna implements CampagnaDAO {

    /**
     * Recupera tutte le campagne presenti nel database e popola la mappa fornita in input.
     * @param listaCampagne La mappa (inizialmente vuota) in cui inserire le campagne estratte.
     * La chiave sarà l'oggetto Campagna, il valore sarà l'oggetto Master.
     * @throws RuntimeException Se si verifica un errore critico durante la comunicazione col database.
     */
    @Override
    public void leggiCampagne(HashMap<Campagna, Master> listaCampagne) {
        String query = "SELECT c.Nome, c.MaxGiocatori, c.Stato, u.Username, u.Email, u.Password " +
                "FROM CAMPAGNA c " +
                "INNER JOIN UTENTE u ON c.CodMaster = u.CodUtente";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // 1. Dati Master
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                Master master = new Master(email, username, password);

                // 2. Dati Campagna
                String nomeCampagna = rs.getString("Nome");
                int maxGiocatori = rs.getInt("MaxGiocatori");
                String statoDb = rs.getString("Stato"); // equivalente isIniziata

                Campagna campagna = new Campagna(nomeCampagna, maxGiocatori, master);

                //per effettivamente settare lo stati della campagna in java rispetto al db
                if ("Non Iniziata".equals(statoDb)) {
                    campagna.setIniziata(false);
                } else {
                    // Se invece il contrario
                    campagna.setIniziata(true);
                }

                // 3. Inserisco la coppia nella HashMap
                listaCampagne.put(campagna, master);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante il caricamento delle campagne dal database.");
        }
    }

    /**
     * Elimina una specifica campagna dal database estraendone il nome dall'oggetto passato come parametro.
     *
     * @param campagnaTarget L'oggetto {@link Campagna} che si desidera eliminare.
     * @throws DatiMancantiException Se il database non trova nessuna campagna con quel nome (0 righe modificate).
     * @throws RuntimeException      Se si verifica un errore tecnico (es. connessione interrotta).
     */
    @Override
    public void rimuoviCampagna(Campagna campagnaTarget) throws DatiMancantiException {
        String query = "DELETE FROM CAMPAGNA WHERE Nome = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, campagnaTarget.getNome());


            int righeModificate = stmt.executeUpdate();

            if (righeModificate == 0) {
                throw new DatiMancantiException("Impossibile eliminare: La campagna '" + campagnaTarget.getNome() + "' non esiste nel database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico durante l'eliminazione della campagna.");
        }
    }
}