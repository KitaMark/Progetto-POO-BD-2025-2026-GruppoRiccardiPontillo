package implementazionePostgresDAO;

import dao.UtenteDAO;
import model.Utente;
import model.Master;
import model.Giocatore;
import exception.AutenticazioneException;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementazione concreta dell'interfaccia {@link UtenteDAO} per il database PostgreSQL.
 * Gestisce tutte le operazioni di persistenza relative all' Utente,
 * traducendo le chiamate ad alto livello in query SQL
 * * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresUtente implements UtenteDAO {

    /**
     * Inserisce un nuovo account utente all'interno del database.
     * Identifica automaticamente il ruolo (Master o Giocatore) in base all'istanza
     * dell'oggetto passato e gestisce i vincoli di unicità imposti dallo schema relazionale.
     *
     * @param utente L'oggetto {@link Utente} (istanziato come Master o Giocatore) da salvare.
     * @throws AutenticazioneException Se i dati inseriti sono già in uso nel sistema.
     */
    @Override
    public void aggiungiUtente(Utente utente) throws AutenticazioneException {
        String query = "INSERT INTO UTENTE (Username, Email, Password, Ruolo) VALUES (?, ?, ?, ?)";

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);) {


            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getEmail());
            stmt.setString(3, utente.getPassword());

            if (utente instanceof Master) {
                stmt.setString(4, "Master");
            } else {
                stmt.setString(4, "Giocatore");
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new AutenticazioneException("Registrazione fallita: Username o Email già in uso!");
        }
    }


    /**
     * Recupera tutti i record dalla tabella UTENTE e popola una lista fornita in input.
     * @param utenti La lista (inizialmente vuota o preesistente) in cui verranno inseriti
     * tutti gli oggetti {@link Utente} ricostruiti dal database.
     */
    @Override
    public void leggiUtenti(List<Utente> utenti) {
        String query = "SELECT * FROM UTENTE";

        try(Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();) {


            while (rs.next()) {
                String usernameDb = rs.getString("Username");
                String emailDb = rs.getString("Email");
                String passwordDb = rs.getString("Password");
                String ruoloDb = rs.getString("Ruolo");

                Utente utenteRuolo;
                if ("Master".equals(ruoloDb)) {
                    utenteRuolo = new Master(emailDb, usernameDb, passwordDb);
                } else {
                    utenteRuolo = new Giocatore(emailDb, usernameDb, passwordDb);
                }
                utenti.add(utenteRuolo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            //non è un errore legato all'utente, ma legato al db(spento oppure la tabella legata all'utente cancellata
            throw new RuntimeException("Errore critico durante il caricamento degli utenti dal database.");
        }
    }
}