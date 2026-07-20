package implementazionePostgresDAO;

import dao.InventarioDao;
import model.Oggetto;
import model.OggettoEquipaggiabile;
import model.OggettoConsumabile;
import model.Statistica;
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
 * Questa classe gestisce la persistenza, il recupero e le transazioni  legate agli oggetti di gioco.
 * Coordina l'interazione tra le tabelle {@code OGGETTO}, {@code INVENTARIO} e {@code PERSONAGGIO}.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ImplementazionePostgresInventario implements InventarioDao {

    /**
     * Carica lo zaino/inventario corrente di uno specifico personaggio, associando a ciascun oggetto la sua quantità.
     * <p>
     * Esegue un'operazione di {@code JOIN} tra le tabelle {@code INVENTARIO} e {@code OGGETTO} filtrando per l'ID del personaggio.
     * Ricostruisce  l'albero delle istanze e mappa lo stato di equipaggiamento direttamente sull'oggetto.
     * </p>
     *
     * @param codPersonaggio l'identificativo univoco del personaggio di cui caricare l'inventario.
     * @param inventarioConsumabili Mappa in cui caricare gli oggetti consumabili con le loro quantità.
     * @param inventarioEquipaggiabili Mappa in cui caricare gli oggetti equipaggiabili con il loro stato (equipaggiato/non equipaggiato).
     * @throws RuntimeException se si verifica un errore durante il recupero dei dati o l'accoppiamento relazionale.
     */
    @Override
    public void caricaInventarioPersonaggio(int codPersonaggio, Map<OggettoConsumabile, Integer> inventarioConsumabili,
                                                             Map<OggettoEquipaggiabile, Boolean> inventarioEquipaggiabili) {

        // Ho corretto la query per includere 'i.quantita' e i nomi delle colonne con underscore
        String query = "SELECT o.codoggetto, o.nome, o.costo, o.tipo, i.equipaggiato, i.quantita, " +
                "oe.req_forza, oe.req_destrezza, oe.req_costituzione, oe.req_intelligenza, " +
                "oe.req_fede, oe.req_carisma, oe.req_fortuna, oe.req_hpmax, oe.req_manamax, " +
                "oe.bonus_forza, oe.bonus_destrezza, oe.bonus_costituzione, oe.bonus_intelligenza, " +
                "oe.bonus_fede, oe.bonus_carisma, oe.bonus_fortuna, oe.bonus_hpmax, oe.bonus_manamax, " +
                "oc.ripristinohp, oc.ripristinomana " +
                "FROM INVENTARIO i " +
                "JOIN OGGETTO o ON i.codoggetto = o.codoggetto " +
                "LEFT JOIN OGGETTO_EQUIPAGGIABILE oe ON o.codoggetto = oe.codoggetto " +
                "LEFT JOIN OGGETTO_CONSUMABILE oc ON o.codoggetto = oc.codoggetto " +
                "WHERE i.codpersonaggio = ?";

        try (Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);){

            stmt.setInt(1, codPersonaggio);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("codoggetto");
                String nome = rs.getString("nome");
                int costo = rs.getInt("costo");
                String tipo = rs.getString("tipo");
                boolean equipaggiato = rs.getBoolean("equipaggiato");
                int quantita = rs.getInt("quantita"); // Ora la colonna è selezionata

                if ("Equipaggiamento".equalsIgnoreCase(tipo)) {
                    Statistica requisiti = new Statistica(
                            rs.getInt("req_costituzione"),
                            rs.getInt("req_forza"),
                            rs.getInt("req_destrezza"),
                            rs.getInt("req_intelligenza"),
                            rs.getInt("req_fede"),
                            rs.getInt("req_carisma"),
                            rs.getInt("req_fortuna"),
                            rs.getInt("req_hpmax"),
                            rs.getInt("req_manamax")
                    );
                    Statistica bonus = new Statistica(
                            rs.getInt("bonus_costituzione"),
                            rs.getInt("bonus_forza"),
                            rs.getInt("bonus_destrezza"),
                            rs.getInt("bonus_intelligenza"),
                            rs.getInt("bonus_fede"),
                            rs.getInt("bonus_carisma"),
                            rs.getInt("bonus_fortuna"),
                            rs.getInt("bonus_hpmax"),
                            rs.getInt("bonus_manamax")
                    );
                    OggettoEquipaggiabile equipaggiabile = new OggettoEquipaggiabile(id, nome, costo, tipo, requisiti, bonus);
                    inventarioEquipaggiabili.put(equipaggiabile, equipaggiato);
                } else if ("Consumabile".equalsIgnoreCase(tipo)) {
                    OggettoConsumabile consumabile = new OggettoConsumabile(
                            id, nome, costo, tipo,
                            rs.getInt("ripristinohp"), rs.getInt("ripristinomana")
                    );
                    inventarioConsumabili.put(consumabile, quantita);
                }
                // Potresti aggiungere un else per gestire tipi di oggetto sconosciuti o loggare un errore.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel caricamento dell'inventario: " + e.getMessage());
        }
    }


    /**
     * Gestisce la transazione di acquisto di un oggetto da parte di un personaggio.
     * <p>
     *Verifica se l'oggetto è già posseduto:
     * in caso positivo incrementa la quantità nella tabella {@code INVENTARIO}, in caso negativo inserisce una nuova riga.
     * Successivamente, scala il costo in oro dalle finanze del personaggio nella tabella {@code PERSONAGGIO}.
     * Se una delle operazioni fallisce, viene eseguito il {@code rollback} per preservare la consistenza del DB.
     * </p>
     *
     * @param codPersonaggio l'identificativo univoco del personaggio che effettua l'acquisto.
     * @param codOggetto     l'identificativo univoco dell'oggetto da acquistare.
     * @param costoOggetto   la quantità di oro da sottrarre al personaggio.
     * @throws RuntimeException se la transazione fallisce, provocando il rollback dello stato.
     */
    @Override
    public void acquistaOggetto(int codPersonaggio, int codOggetto, int costoOggetto) {
        String queryCheck = "SELECT quantita FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryInsert = "INSERT INTO INVENTARIO (codpersonaggio, codoggetto, quantita, equipaggiato) VALUES (?, ?, 1, FALSE)";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita + 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateOro = "UPDATE PERSONAGGIO SET oro = oro - ? WHERE codpersonaggio = ?";

        Connection conn = null;

        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            PreparedStatement stmtCheck = conn.prepareStatement(queryCheck);
            stmtCheck.setInt(1, codPersonaggio);
            stmtCheck.setInt(2, codOggetto);
            ResultSet rs = stmtCheck.executeQuery();

            int quantitaAttuale = 0;
            if (rs.next()) {
                quantitaAttuale = rs.getInt("quantita");
            }

            if (quantitaAttuale > 0) {
                PreparedStatement stmtUpInv = conn.prepareStatement(queryUpdateInv);
                stmtUpInv.setInt(1, codPersonaggio);
                stmtUpInv.setInt(2, codOggetto);
                stmtUpInv.executeUpdate();
            } else {
                PreparedStatement stmtIns = conn.prepareStatement(queryInsert);
                stmtIns.setInt(1, codPersonaggio);
                stmtIns.setInt(2, codOggetto);
                stmtIns.executeUpdate();
            }

            PreparedStatement stmtOro = conn.prepareStatement(queryUpdateOro);
            stmtOro.setInt(1, costoOggetto);
            stmtOro.setInt(2, codPersonaggio);
            stmtOro.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Transazione di acquisto fallita: " + e.getMessage());
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
     * @param codPersonaggio l'identificativo del venditore.
     * @param codOggetto     l'identificativo dell'oggetto da vendere.
     * @param ricavoOggetto  l'oro da aggiungere al bilancio del personaggio.
     * @throws RuntimeException se l'oggetto non è presente o se si verifica un errore durante l'esecuzione SQL.
     */
    @Override
    public void vendiOggetto(int codPersonaggio, int codOggetto, int ricavoOggetto) {
        String queryCheck = "SELECT quantita FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryDelete = "DELETE FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita - 1 WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateOro = "UPDATE PERSONAGGIO SET oro = oro + ? WHERE codpersonaggio = ?";

        Connection conn = null;

        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            PreparedStatement stmtCheck = conn.prepareStatement(queryCheck);
            stmtCheck.setInt(1, codPersonaggio);
            stmtCheck.setInt(2, codOggetto);
            ResultSet rs = stmtCheck.executeQuery();

            int quantitaAttuale = 0;
            if (rs.next()) {
                quantitaAttuale = rs.getInt("quantita");
            }

            if (quantitaAttuale <= 0) {
                throw new SQLException("Impossibile vendere: oggetto non presente nell'inventario.");
            }

            if (quantitaAttuale > 1) {
                PreparedStatement stmtUpInv = conn.prepareStatement(queryUpdateInv);
                stmtUpInv.setInt(1, codPersonaggio);
                stmtUpInv.setInt(2, codOggetto);
                stmtUpInv.executeUpdate();
            } else {
                PreparedStatement stmtDel = conn.prepareStatement(queryDelete);
                stmtDel.setInt(1, codPersonaggio);
                stmtDel.setInt(2, codOggetto);
                stmtDel.executeUpdate();
            }

            PreparedStatement stmtOro = conn.prepareStatement(queryUpdateOro);
            stmtOro.setInt(1, ricavoOggetto);
            stmtOro.setInt(2, codPersonaggio);
            stmtOro.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Transazione di vendita fallita: " + e.getMessage());
        }
    }


    /**
     * Aggiorna lo stato di equipaggiamento di un oggetto specifico all'interno dell'inventario del personaggio.
     * <p>
     * Modifica il flag booleano della colonna {@code equipaggiato} nella tabella {@code INVENTARIO}
     * per riflettere se l'arma o l'armatura è attualmente indossata dal personaggio.
     * </p>
     *
     * @param codPersonaggio l'identificativo del personaggio di riferimento.
     * @param codOggetto     l'identificativo dell'oggetto da equipaggiare o rimuovere.
     * @param equipaggiato   {@code true} per impostare l'oggetto come equipaggiato, {@code false} altrimenti.
     * @throws RuntimeException se si verifica un errore durante l'aggiornamento del record.
     */
    @Override
    public void impostaEquipaggiamento(int codPersonaggio, int codOggetto, boolean equipaggiato) {
        String query = "UPDATE INVENTARIO SET equipaggiato = ? WHERE codpersonaggio = ? AND codoggetto = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setBoolean(1, equipaggiato);
            stmt.setInt(2, codPersonaggio);
            stmt.setInt(3, codOggetto);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
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
     * @param codPersonaggio l'identificativo univoco del personaggio che consuma l'oggetto.
     * @param codOggetto     l'identificativo univoco del consumabile utilizzato.
     * @throws RuntimeException se l'interrogazione o la rimozione sul database generano un'eccezione SQL.
     */
    @Override
    public void consumaOggetto(int codPersonaggio, int codOggetto) {
        String queryCheck = "SELECT quantita FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryDelete = "DELETE FROM INVENTARIO WHERE codpersonaggio = ? AND codoggetto = ?";
        String queryUpdateInv = "UPDATE INVENTARIO SET quantita = quantita - 1 WHERE codpersonaggio = ? AND codoggetto = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmtCheck = conn.prepareStatement(queryCheck);
            stmtCheck.setInt(1, codPersonaggio);
            stmtCheck.setInt(2, codOggetto);
            ResultSet rs = stmtCheck.executeQuery();

            int quantitaAttuale = 0;
            if (rs.next()) {
                quantitaAttuale = rs.getInt("quantita");
            }

            // Se ne ha più di una copia scala il contatore, se ha l'ultima elimina la riga
            if (quantitaAttuale > 1) {
                PreparedStatement stmtUp = conn.prepareStatement(queryUpdateInv);
                stmtUp.setInt(1, codPersonaggio);
                stmtUp.setInt(2, codOggetto);
                stmtUp.executeUpdate();
            } else if (quantitaAttuale == 1) {
                PreparedStatement stmtDel = conn.prepareStatement(queryDelete);
                stmtDel.setInt(1, codPersonaggio);
                stmtDel.setInt(2, codOggetto);
                stmtDel.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel consumo dell'oggetto sul DB: " + e.getMessage());
        }
    }
}