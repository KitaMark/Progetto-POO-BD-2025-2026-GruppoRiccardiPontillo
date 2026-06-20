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

public class ImplementazionePostgresInventario implements InventarioDao {

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