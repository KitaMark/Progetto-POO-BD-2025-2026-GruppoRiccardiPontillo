package implementazionePostgresDAO;

import dao.OggettoDao;
import database.ConnessioneDatabase;
import model.OggettoConsumabile;
import model.OggettoEquipaggiabile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione PostgreSQL dell'interfaccia {@link OggettoDao}.
 * <p>
 * Questa classe gestisce l'inserimento in tabelle gerarchiche (ereditarietà su DB).
 * Esegue inserimenti prima nella tabella padre (OGGETTO) recuperando l'ID generato,
 * per poi utilizzarlo come Foreign Key per l'inserimento nelle tabelle figlie
 * (OGGETTO_CONSUMABILE o OGGETTO_EQUIPAGGIABILE). L'intera operazione è gestita
 * tramite transazioni esplicite (Commit/Rollback) per garantire la consistenza dei dati.
 * </p>
 */
public class ImplementazionePostgresOggetto implements OggettoDao {

    /**
     * Salva un nuovo oggetto di tipo Consumabile nel database.
     *
     * @param consumabile L'oggetto da salvare.
     * @param idCampagna  L'ID della campagna di riferimento.
     * @return L'ID univoco autogenerato per l'oggetto creato.
     */
    @Override
    public int salvaConsumabile(OggettoConsumabile consumabile, int idCampagna) {
        String insertOggetto = "INSERT INTO OGGETTO (Nome, Costo, Tipo, CodCampagna) VALUES (?, ?, 'Consumabile', ?) RETURNING CodOggetto";
        String insertConsumabile = "INSERT INTO OGGETTO_CONSUMABILE (CodOggetto, RipristinoHp, RipristinoMana) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            int idGenerato = -1;

            try (PreparedStatement stmtPadre = conn.prepareStatement(insertOggetto)) {
                stmtPadre.setString(1, consumabile.getNome());
                stmtPadre.setInt(2, consumabile.getCosto());
                stmtPadre.setInt(3, idCampagna);

                ResultSet rs = stmtPadre.executeQuery();
                if (rs.next()) idGenerato = rs.getInt(1);
                else throw new SQLException("Impossibile generare l'ID per l'oggetto.");
            }

            try (PreparedStatement stmtFiglio = conn.prepareStatement(insertConsumabile)) {
                stmtFiglio.setInt(1, idGenerato);
                stmtFiglio.setInt(2, consumabile.getRipristinoHP());
                stmtFiglio.setInt(3, consumabile.getRipristinoMana());
                stmtFiglio.executeUpdate();
            }

            conn.commit();
            return idGenerato;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Errore salvataggio consumabile: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    /**
     * Salva un nuovo oggetto di tipo Equipaggiabile nel database, includendo sia i bonus che i requisiti minimi.
     *
     * @param equip L'oggetto contenente i bonus statistici e i requisiti.
     * @param idCampagna L'ID della campagna di riferimento.
     * @return L'ID univoco autogenerato per l'oggetto creato.
     */
    @Override
    public int salvaEquipaggiamento(OggettoEquipaggiabile equip, int idCampagna) {
        String insertOggetto = "INSERT INTO OGGETTO (Nome, Costo, Tipo, CodCampagna) VALUES (?, ?, 'Equipaggiamento', ?) RETURNING CodOggetto";

        String insertEquip = "INSERT INTO OGGETTO_EQUIPAGGIABILE " +
                "(CodOggetto, Bonus_Forza, Bonus_Destrezza, Bonus_Costituzione, Bonus_Intelligenza, " +
                "Bonus_Fede, Bonus_Carisma, Bonus_Fortuna, Bonus_HpMax, Bonus_ManaMax, " +
                "Req_Forza, Req_Destrezza, Req_Costituzione, Req_Intelligenza, " +
                "Req_Fede, Req_Carisma, Req_Fortuna, Req_HpMax, Req_ManaMax) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConnessioneDatabase.getInstance().connection;
            conn.setAutoCommit(false);

            int idGenerato = -1;

            try (PreparedStatement stmtPadre = conn.prepareStatement(insertOggetto)) {
                stmtPadre.setString(1, equip.getNome());
                stmtPadre.setInt(2, equip.getCosto());
                stmtPadre.setInt(3, idCampagna);

                ResultSet rs = stmtPadre.executeQuery();
                if (rs.next()) idGenerato = rs.getInt(1);
                else throw new SQLException("Impossibile generare l'ID per l'oggetto.");
            }

            try (PreparedStatement stmtFiglio = conn.prepareStatement(insertEquip)) {
                stmtFiglio.setInt(1, idGenerato);

                // Set dei Bonus
                stmtFiglio.setInt(2, equip.getBonus().getForza());
                stmtFiglio.setInt(3, equip.getBonus().getDestrezza());
                stmtFiglio.setInt(4, equip.getBonus().getCostituzione());
                stmtFiglio.setInt(5, equip.getBonus().getIntelligenza());
                stmtFiglio.setInt(6, equip.getBonus().getFede());
                stmtFiglio.setInt(7, equip.getBonus().getCarisma());
                stmtFiglio.setInt(8, equip.getBonus().getFortuna());
                stmtFiglio.setInt(9, equip.getBonus().getHpMax());
                stmtFiglio.setInt(10, equip.getBonus().getManaMax());

                // Set dei Requisiti
                stmtFiglio.setInt(11, equip.getRequisiti().getForza());
                stmtFiglio.setInt(12, equip.getRequisiti().getDestrezza());
                stmtFiglio.setInt(13, equip.getRequisiti().getCostituzione());
                stmtFiglio.setInt(14, equip.getRequisiti().getIntelligenza());
                stmtFiglio.setInt(15, equip.getRequisiti().getFede());
                stmtFiglio.setInt(16, equip.getRequisiti().getCarisma());
                stmtFiglio.setInt(17, equip.getRequisiti().getFortuna());
                stmtFiglio.setInt(18, equip.getRequisiti().getHpMax());
                stmtFiglio.setInt(19, equip.getRequisiti().getManaMax());

                stmtFiglio.executeUpdate();
            }

            conn.commit();
            return idGenerato;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Errore salvataggio equipaggiamento: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}