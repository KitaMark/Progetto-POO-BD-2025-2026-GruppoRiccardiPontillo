package implementazionePostgresDAO;

import dao.InventarioDao;
import model.*;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementazione specifica per PostgreSQL dell'interfaccia {@link InventarioDao}.
 * <p>
 * Questa classe gestisce la persistenza, il recupero e le transazioni legate agli oggetti di gioco.
 * Coordina l'interazione tra le tabelle {@code OGGETTO}, {@code INVENTARIO} e {@code PERSONAGGIO}.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresInventario implements InventarioDao {

    /**
     * Ricostruisce lo zaino in memoria di un giocatore interrogando il database.
     * <p>
     * Effettua una join multipla per instradare a RAM tutte le differenziazioni
     * (consumabile, equipaggiabile) relative agli oggetti presenti nella tabella ponte.
     * </p>
     *
     * @param idPersonaggio l'identificativo del proprietario dello zaino.
     * @return Una mappa strutturata che usa come chiavi gli {@oggettoconsumabile} polimorfici e come valore la rispettiva quantità.
     * @throws RuntimeException se insorgono errori di rete col DB in fase di lettura.
     */
    @Override
    public Map<Oggetto, Integer> caricaInventarioPersonaggio(int idPersonaggio) {
        Map<Oggetto, Integer> inv = new HashMap<>();

        String query = "SELECT o.CodOggetto, o.Nome, o.Costo, o.Tipo, i.Quantita, i.Equipaggiato, " +
                "eq.Req_Forza, eq.Req_Destrezza, eq.Req_Costituzione, eq.Req_Intelligenza, eq.Req_Fede, eq.Req_Carisma, eq.Req_Fortuna, eq.Req_HpMax, eq.Req_ManaMax, " +
                "eq.Bonus_Forza, eq.Bonus_Destrezza, eq.Bonus_Costituzione, eq.Bonus_Intelligenza, eq.Bonus_Fede, eq.Bonus_Carisma, eq.Bonus_Fortuna, eq.Bonus_HpMax, eq.Bonus_ManaMax, " +
                "con.RipristinoHp, con.RipristinoMana " +
                "FROM INVENTARIO i JOIN OGGETTO o ON i.CodOggetto = o.CodOggetto " +
                "LEFT JOIN OGGETTO_EQUIPAGGIABILE eq ON o.CodOggetto = eq.CodOggetto " +
                "LEFT JOIN OGGETTO_CONSUMABILE con ON o.CodOggetto = con.CodOggetto " +
                "WHERE i.CodPersonaggio = ?";

        try (PreparedStatement stmt = ConnessioneDatabase.getInstance().connection.prepareStatement(query)) {
            stmt.setInt(1, idPersonaggio);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("CodOggetto");
                String nome = rs.getString("Nome");
                int costo = rs.getInt("Costo");
                String tipo = rs.getString("Tipo");
                int q = rs.getInt("Quantita");
                boolean eq = rs.getBoolean("Equipaggiato");

                if ("Consumabile".equalsIgnoreCase(tipo)) {
                    inv.put(new OggettoConsumabile(id, nome, costo, tipo, rs.getInt("RipristinoHp"), rs.getInt("RipristinoMana")), q);
                } else {
                    Statistica req = new Statistica(rs.getInt("Req_Costituzione"), rs.getInt("Req_Forza"), rs.getInt("Req_Destrezza"), rs.getInt("Req_Intelligenza"), rs.getInt("Req_Fede"), rs.getInt("Req_Carisma"), rs.getInt("Req_Fortuna"), rs.getInt("Req_HpMax"), rs.getInt("Req_ManaMax"));
                    Statistica bon = new Statistica(rs.getInt("Bonus_Costituzione"), rs.getInt("Bonus_Forza"), rs.getInt("Bonus_Destrezza"), rs.getInt("Bonus_Intelligenza"), rs.getInt("Bonus_Fede"), rs.getInt("Bonus_Carisma"), rs.getInt("Bonus_Fortuna"), rs.getInt("Bonus_HpMax"), rs.getInt("Bonus_ManaMax"));
                    OggettoEquipaggiabile o = new OggettoEquipaggiabile(id, nome, costo, tipo, req, bon);
                    o.setEquipaggiato(eq);
                    inv.put(o, q);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore caricamento inventario: " + e.getMessage());
        }

        return inv;
    }

    /**
     * Gestisce la transazione di acquisto di un oggetto da parte di un personaggio.
     * <p>
     * Verifica se l'oggetto è già posseduto:
     * in caso positivo incrementa la quantità nella tabella {@code INVENTARIO}, in caso negativo inserisce una nuova riga.
     * Successivamente, scala il costo in oro dalle finanze del personaggio nella tabella {@code PERSONAGGIO}.
     * Se una delle operazioni fallisce, viene eseguito il {@code rollback} per preservare la consistenza del DB.
     * </p>
     *
     * @param pId    l'identificativo univoco del personaggio che effettua l'acquisto.
     * @param oId    l'identificativo univoco dell'oggetto da acquistare.
     * @param costo  la quantità di oro da sottrarre al personaggio.
     * @throws RuntimeException se la transazione fallisce, provocando il rollback dello stato.
     */
    @Override
    public void acquistaOggetto(int pId, int oId, int costo) {
        String queryCheck = "SELECT quantita FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita + 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryInsert = "INSERT INTO INVENTARIO (codpersonaggio, codoggetto, quantita, equipaggiato) VALUES (?, ?, 1, FALSE)";
        String queryUpdateOro = "UPDATE PERSONAGGIO SET oro = oro - ? WHERE codpersonaggio = ?";

        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            int quantita = 0;
            try (PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {
                stmtCheck.setInt(1, pId);
                stmtCheck.setInt(2, oId);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    quantita = rs.getInt("quantita");
                }
            }

            if (quantita > 0) {
                try (PreparedStatement stmtUp = conn.prepareStatement(queryUpdateInv)) {
                    stmtUp.setInt(1, pId);
                    stmtUp.setInt(2, oId);
                    stmtUp.executeUpdate();
                }
            } else {
                try (PreparedStatement stmtIns = conn.prepareStatement(queryInsert)) {
                    stmtIns.setInt(1, pId);
                    stmtIns.setInt(2, oId);
                    stmtIns.executeUpdate();
                }
            }

            try (PreparedStatement stmtOro = conn.prepareStatement(queryUpdateOro)) {
                stmtOro.setInt(1, costo);
                stmtOro.setInt(2, pId);
                stmtOro.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Acquisto fallito: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Gestisce la transazione di vendita di un oggetto posseduto da un personaggio per ricavarne oro.
     * <p>
     * L'operazione è gestita tramite transazione manuale. Verifica la quantità corrente:
     * se l'oggetto è posseduto in copia singola, rimuove la riga dalla tabella {@code INVENTARIO},
     * se posseduto in copie multiple, ne decrementa il contatore. Infine, accredita il ricavo in oro
     * nella tabella {@code PERSONAGGIO}.
     * </p>
     *
     * @param pId    l'identificativo del venditore.
     * @param oId    l'identificativo dell'oggetto da vendere.
     * @param ricavo l'oro da aggiungere al bilancio del personaggio.
     * @throws RuntimeException se l'oggetto non è presente o se si verifica un errore durante l'esecuzione SQL.
     */
    @Override
    public void vendiOggetto(int pId, int oId, int ricavo) {
        String queryCheck = "SELECT quantita FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita - 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryDelete = "DELETE FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateOro = "UPDATE PERSONAGGIO SET oro = oro + ? WHERE codpersonaggio = ?";

        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            int quantita = 0;
            try (PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {
                stmtCheck.setInt(1, pId);
                stmtCheck.setInt(2, oId);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    quantita = rs.getInt("quantita");
                }
            }

            if (quantita <= 0) {
                throw new SQLException("Oggetto non presente nell'inventario.");
            }

            if (quantita > 1) {
                try (PreparedStatement stmtUp = conn.prepareStatement(queryUpdateInv)) {
                    stmtUp.setInt(1, pId);
                    stmtUp.setInt(2, oId);
                    stmtUp.executeUpdate();
                }
            } else {
                try (PreparedStatement stmtDel = conn.prepareStatement(queryDelete)) {
                    stmtDel.setInt(1, pId);
                    stmtDel.setInt(2, oId);
                    stmtDel.executeUpdate();
                }
            }

            try (PreparedStatement stmtOro = conn.prepareStatement(queryUpdateOro)) {
                stmtOro.setInt(1, ricavo);
                stmtOro.setInt(2, pId);
                stmtOro.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Vendita fallita: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Aggiorna lo stato di equipaggiamento di un oggetto specifico all'interno dell'inventario del personaggio.
     * <p>
     * Modifica il flag booleano della colonna {@code equipaggiato} nella tabella {@code INVENTARIO}
     * per riflettere se l'arma o l'armatura è attualmente indossata dal personaggio.
     * Intercetta gli errori sollevati dai trigger (P0001) per mostrare messaggi puliti in caso di requisiti non soddisfatti.
     * </p>
     *
     * @param pId l'identificativo del personaggio di riferimento.
     * @param oId l'identificativo dell'oggetto da equipaggiare o rimuovere.
     * @param eq  {@code true} per impostare l'oggetto come equipaggiato, {@code false} altrimenti.
     * @throws RuntimeException se si verifica un errore durante l'aggiornamento del record.
     */
    @Override
    public void impostaEquipaggiamento(int pId, int oId, boolean eq) {
        String query = "UPDATE INVENTARIO SET equipaggiato = ? WHERE codpersonaggio = ? AND codoggetto = ?";

        try (PreparedStatement stmt = ConnessioneDatabase.getInstance().connection.prepareStatement(query)) {
            stmt.setBoolean(1, eq);
            stmt.setInt(2, pId);
            stmt.setInt(3, oId);

            int righeAggiornate = stmt.executeUpdate();
            if (righeAggiornate == 0) {
                throw new SQLException("Oggetto non trovato. Desincronizzazione rilevata.");
            }

        } catch (SQLException e) {
            if ("P0001".equals(e.getSQLState())) {
                String puliziaMessaggio = e.getMessage().split("\n")[0].replaceAll("(?i)errore:\\s*", "").replaceAll("(?i)error:\\s*", "");
                throw new RuntimeException(puliziaMessaggio);
            }
            throw new RuntimeException("Errore di sistema DB: " + e.getMessage());
        }
    }

    /**
     * Decrementa la quantità o rimuove un oggetto consumabile dall'inventario in seguito al suo utilizzo.
     * <p>
     * Controlla il quantitativo residuo dell'oggetto per il personaggio: se è superiore a 1, effettua un
     * {@code UPDATE} per scalare di un'unità il contatore; se è pari a 1 (ultima copia rimasta), esegue
     * un {@code DELETE} per ripulire la riga dall'inventario sul database.
     * </p>
     *
     * @param pId l'identificativo univoco del personaggio che consuma l'oggetto.
     * @param oId l'identificativo univoco del consumabile utilizzato.
     * @throws RuntimeException se l'interrogazione o la rimozione sul database generano un'eccezione SQL.
     */
    @Override
    public void consumaOggetto(int pId, int oId) {
        String queryCheck = "SELECT quantita FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdate = "UPDATE INVENTARIO SET quantita = quantita - 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryDelete = "DELETE FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            int q = 0;

            try (PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {
                stmtCheck.setInt(1, pId);
                stmtCheck.setInt(2, oId);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    q = rs.getInt("quantita");
                }
            }

            if (q > 1) {
                try (PreparedStatement stmtUp = conn.prepareStatement(queryUpdate)) {
                    stmtUp.setInt(1, pId);
                    stmtUp.setInt(2, oId);
                    if (stmtUp.executeUpdate() == 0) {
                        throw new SQLException("Oggetto non trovato. Desincronizzazione rilevata.");
                    }
                }
            } else if (q == 1) {
                try (PreparedStatement stmtDel = conn.prepareStatement(queryDelete)) {
                    stmtDel.setInt(1, pId);
                    stmtDel.setInt(2, oId);
                    if (stmtDel.executeUpdate() == 0) {
                        throw new SQLException("Oggetto non trovato. Desincronizzazione rilevata.");
                    }
                }
            } else {
                throw new SQLException("Oggetto non presente in inventario.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore consumo: " + e.getMessage());
        }
    }

    /**
     * Rimuove un oggetto dall'inventario di un personaggio.
     * <p>
     * Se l'oggetto è un consumabile con quantità maggiore di 1, decrementa la quantità.
     * Altrimenti, rimuove completamente l'oggetto dall'inventario.
     * Se l'oggetto è equipaggiato, lo disequipaggia prima di rimuoverlo.
     * </p>
     *
     * @param pId l'identificativo del personaggio.
     * @param oId l'identificativo dell'oggetto da rimuovere.
     * @throws RuntimeException se si verifica un errore durante l'operazione.
     */
    @Override
    public void rimuoviOggetto(int pId, int oId) {
        String queryCheck = "SELECT quantita, equipaggiato, o.Tipo FROM INVENTARIO i JOIN OGGETTO o ON i.CodOggetto = o.CodOggetto WHERE codpersonaggio = ? AND i.codoggetto = ?";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita - 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryDelete = "DELETE FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUnequip = "UPDATE INVENTARIO SET equipaggiato = FALSE WHERE codpersonaggio = ? AND codoggetto = ?";


        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            int quantita = 0;
            boolean equipaggiato = false;
            String tipoOggetto = null;

            try (PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {
                stmtCheck.setInt(1, pId);
                stmtCheck.setInt(2, oId);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    quantita = rs.getInt("quantita");
                    equipaggiato = rs.getBoolean("equipaggiato");
                    tipoOggetto = rs.getString("Tipo");
                } else {
                    throw new SQLException("Oggetto non presente nell'inventario.");
                }
            }

            if (equipaggiato) {
                try (PreparedStatement stmtUnequip = conn.prepareStatement(queryUnequip)) {
                    stmtUnequip.setInt(1, pId);
                    stmtUnequip.setInt(2, oId);
                    stmtUnequip.executeUpdate();
                }
            }

            if ("Consumabile".equalsIgnoreCase(tipoOggetto) && quantita > 1) {
                try (PreparedStatement stmtUp = conn.prepareStatement(queryUpdateInv)) {
                    stmtUp.setInt(1, pId);
                    stmtUp.setInt(2, oId);
                    stmtUp.executeUpdate();
                }
            } else {
                try (PreparedStatement stmtDel = conn.prepareStatement(queryDelete)) {
                    stmtDel.setInt(1, pId);
                    stmtDel.setInt(2, oId);
                    stmtDel.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Rimozione oggetto fallita: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Assegna un oggetto all'inventario di un personaggio, senza costi.
     * Se l'oggetto è già presente e consumabile, ne incrementa la quantità.
     * Se è equipaggiabile e già presente, lancia un'eccezione.
     *
     * @param pId l'identificativo del personaggio a cui assegnare l'oggetto.
     * @param oId l'identificativo dell'oggetto da assegnare.
     * @throws RuntimeException se si verifica un errore durante l'operazione o se si tenta di assegnare un equipaggiabile già posseduto.
     */
    @Override
    public void assegnaOggettoAInventario(int pId, int oId) {
        String queryCheck = "SELECT i.quantita, o.Tipo FROM INVENTARIO i JOIN OGGETTO o ON i.CodOggetto = o.CodOggetto WHERE i.codpersonaggio = ? AND i.codoggetto = ?";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita + 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryInsert = "INSERT INTO INVENTARIO (codpersonaggio, codoggetto, quantita, equipaggiato) VALUES (?, ?, 1, FALSE)";

        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            int quantita = 0;
            String tipoOggetto = null;

            try (PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {
                stmtCheck.setInt(1, pId);
                stmtCheck.setInt(2, oId);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next()) {
                    quantita = rs.getInt("quantita");
                    tipoOggetto = rs.getString("Tipo");
                }
            }

            if (quantita > 0) {
                if ("Consumabile".equalsIgnoreCase(tipoOggetto)) {
                    try (PreparedStatement stmtUp = conn.prepareStatement(queryUpdateInv)) {
                        stmtUp.setInt(1, pId);
                        stmtUp.setInt(2, oId);
                        stmtUp.executeUpdate();
                    }
                } else { // Equipaggiabile
                    throw new RuntimeException("Il personaggio possiede già questo oggetto equipaggiabile.");
                }
            } else {
                try (PreparedStatement stmtIns = conn.prepareStatement(queryInsert)) {
                    stmtIns.setInt(1, pId);
                    stmtIns.setInt(2, oId);
                    stmtIns.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Assegnazione oggetto fallita: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }
}