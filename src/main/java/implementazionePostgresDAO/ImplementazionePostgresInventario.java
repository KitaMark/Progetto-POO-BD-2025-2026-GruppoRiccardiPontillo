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
     * Recupera dal database il catalogo completo di tutti gli oggetti acquistabili nel gioco.
     * <p>
     * Effettua il mapping  leggendo il campo {@code tipo}: se il valore è 'Equipaggiamento',
     * istanzia un {@link OggettoEquipaggiabile} impostando i requisiti e i bonus strutturati;
     * altrimenti, istanzia un {@link OggettoConsumabile} mappando i valori di ripristino di HP e Mana.
     * </p>
     *
     * @return una {@link List} di oggetti di tipo {@link Oggetto} che rappresentano il catalogo globale del negozio.
     * @throws RuntimeException se si verifica un errore di comunicazione SQL con il database.
     */
    @Override
    public List<Oggetto> caricaCatalogoNegozio() {
        List<Oggetto> catalogo = new ArrayList<>();
        String query = "SELECT * FROM OGGETTO";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("codoggetto");
                String nome = rs.getString("nome");
                int costo = rs.getInt("costo");
                String tipo = rs.getString("tipo");

                Oggetto oggetto;

                if ("Equipaggiamento".equalsIgnoreCase(tipo)) {
                    Statistica requisiti = new Statistica(
                            0, rs.getInt("reqforza"), rs.getInt("reqdestrezza"),
                            0, 0, 0, 0, 0, 0
                    );
                    Statistica bonus = new Statistica(
                            0, rs.getInt("bonusforza"), 0,
                            0, 0, 0, 0, 0, 0
                    );
                    oggetto = new OggettoEquipaggiabile(id, nome, costo, tipo, requisiti, bonus);
                } else {
                    oggetto = new OggettoConsumabile(
                            id, nome, costo, tipo,
                            rs.getInt("ripristinohp"), rs.getInt("ripristinomana")
                    );
                }
                catalogo.add(oggetto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel caricamento del catalogo negozio: " + e.getMessage());
        }

        return catalogo;
    }

    /**
     * Carica lo zaino/inventario corrente di uno specifico personaggio, associando a ciascun oggetto la sua quantità.
     * <p>
     * Esegue un'operazione di {@code JOIN} tra le tabelle {@code INVENTARIO} e {@code OGGETTO} filtrando per l'ID del personaggio.
     * Ricostruisce  l'albero delle istanze e mappa lo stato di equipaggiamento direttamente sull'oggetto.
     * </p>
     *
     * @param codPersonaggio l'identificativo univoco del personaggio di cui caricare l'inventario.
     * @return una {@link Map} contenente come chiavi le istanze di {@link Oggetto} e come valori le relative quantità possedute.
     * @throws RuntimeException se si verifica un errore durante il recupero dei dati o l'accoppiamento relazionale.
     */
    @Override
    public Map<Oggetto, Integer> caricaInventarioPersonaggio(int codPersonaggio) {
        Map<Oggetto, Integer> inventario = new HashMap<>();
        String query = "SELECT o.codoggetto, o.nome, o.costo, o.tipo, " +
                "o.reqforza, o.reqdestrezza, o.bonusforza, o.ripristinohp, o.ripristinomana, " +
                "i.quantita, i.equipaggiato " +
                "FROM INVENTARIO i " +
                "JOIN OGGETTO o ON i.codoggetto = o.codoggetto " +
                "WHERE i.codpersonaggio = ?";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, codPersonaggio);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("codoggetto");
                String nome = rs.getString("nome");
                int costo = rs.getInt("costo");
                String tipo = rs.getString("tipo");

                Oggetto oggetto;

                if ("Equipaggiamento".equalsIgnoreCase(tipo)) {
                    Statistica requisiti = new Statistica(
                            0, rs.getInt("reqforza"), rs.getInt("reqdestrezza"),
                            0, 0, 0, 0, 0, 0
                    );
                    Statistica bonus = new Statistica(
                            0, rs.getInt("bonusforza"), 0,
                            0, 0, 0, 0, 0, 0
                    );
                    oggetto = new OggettoEquipaggiabile(id, nome, costo, tipo, requisiti, bonus);
                } else {
                    oggetto = new OggettoConsumabile(
                            id, nome, costo, tipo,
                            rs.getInt("ripristinohp"), rs.getInt("ripristinomana")
                    );
                }

                oggetto.setEquipaggiato(rs.getBoolean("equipaggiato"));
                int quantita = rs.getInt("quantita");
                inventario.put(oggetto, quantita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel caricamento dell'inventario: " + e.getMessage());
        }

        return inventario;
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