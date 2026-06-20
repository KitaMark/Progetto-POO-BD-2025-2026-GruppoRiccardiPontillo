package implementazionePostgresDAO;

import dao.GiocatoreDao;
import model.Personaggio;
import model.Campagna;
import model.Statistica;
import model.Classe;
import model.Razza;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ImplementazionePostgresGiocatore implements GiocatoreDao {

    @Override
    public void iscrivitiACampagna(int codUtente, int codCampagna) {
        String query = "INSERT INTO ISCRIZIONE (CodUtente, CodCampagna) VALUES (?, ?)";

        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query) ;

            stmt.setInt(1, codUtente);
            stmt.setInt(2, codCampagna);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'iscrizione alla campagna: " + e.getMessage());
        }
    }

    @Override
    public void salvaPersonaggio(Personaggio pg, int codUtente, int codCampagna) {

        String query = """
        WITH nuovo_pg AS (
            INSERT INTO PERSONAGGIO (Nome, Oro, IsPG, CodUtente, CodCampagna, CodClasse, CodRazza) 
            VALUES (
                ?, ?, TRUE, ?, ?, 
                (SELECT CodClasse FROM CLASSE WHERE Nome = ?), 
                (SELECT CodRazza FROM RAZZA WHERE Nome = ?)
            ) RETURNING CodPersonaggio
        )
        INSERT INTO STATISTICA (Forza, Destrezza, Costituzione, Intelligenza, Fede, Carisma, Fortuna, HpMax, HpAttuali, ManaMax, ManaAttuali, CodPersonaggio) 
        SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CodPersonaggio FROM nuovo_pg;
        """;

        try {Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmt = conn.prepareStatement(query);

            // Parametri per la tabella PERSONAGGIO
            stmt.setString(1, pg.getNome());
            stmt.setInt(2, pg.getOro());
            stmt.setInt(3, codUtente);
            stmt.setInt(4, codCampagna);
            stmt.setString(5, pg.getClasse().getNome());
            stmt.setString(6, pg.getRazza().getNome());

            // Parametri per la tabella STATISTICA
            Statistica s = pg.getStatisticheBase();
            stmt.setInt(7, s.getForza());
            stmt.setInt(8, s.getDestrezza());
            stmt.setInt(9, s.getCostituzione());
            stmt.setInt(10, s.getIntelligenza());
            stmt.setInt(11, s.getFede());
            stmt.setInt(12, s.getCarisma());
            stmt.setInt(13, s.getFortuna());
            stmt.setInt(14, s.getHpMax());
            stmt.setInt(15, pg.getHpCorrenti());
            stmt.setInt(16, s.getManaMax());
            stmt.setInt(17, pg.getManaCorrente());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il salvataggio del personaggio: " + e.getMessage());
        }
    }



    @Override
    public void aggiornaRisorse(Personaggio pg) {
        String queryStat = "UPDATE STATISTICA SET HpAttuali = ?, ManaAttuali = ? WHERE CodPersonaggio = ?";
        String queryOro = "UPDATE PERSONAGGIO SET Oro = ? WHERE CodPersonaggio = ?";

        try{
            Connection conn = ConnessioneDatabase.getInstance().connection;
             PreparedStatement stmtStat = conn.prepareStatement(queryStat);
             PreparedStatement stmtOro = conn.prepareStatement(queryOro);

            // Update Statistiche
            stmtStat.setInt(1, pg.getHpCorrenti());
            stmtStat.setInt(2, pg.getManaCorrente());
            stmtStat.setInt(3, pg.getId());
            stmtStat.executeUpdate();

            // Update Oro Personaggio
            stmtOro.setInt(1, pg.getOro());
            stmtOro.setInt(2, pg.getId());
            stmtOro.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'aggiornamento delle risorse: " + e.getMessage());
        }
    }

    public HashMap<Campagna, Personaggio> caricaTutteLePartecipazioni(int codUtente) {
        HashMap<Campagna, Personaggio> mappaPartecipazioni = new HashMap<>();

        // Utilizziamo solo alias minuscoli nella SELECT e richiamiamo quelli nel ResultSet
        String query = "SELECT " +
                "c.CodCampagna AS id_campagna, c.Nome AS nome_campagna, c.MaxGiocatori AS max_g, c.Stato AS stato_campagna, " +
                "p.CodPersonaggio AS id_pg, p.Nome AS nome_pg, p.Oro AS oro, p.IsPG AS is_pg, " +
                "s.HpAttuali AS hp_att, s.ManaAttuali AS mana_att, s.PuntiSpendere AS punti, " +
                "s.Forza AS forza, s.Destrezza AS destrezza, s.Costituzione AS cost, s.Intelligenza AS intel, " +
                "s.Fede AS fede, s.Carisma AS carisma, s.Fortuna AS fortuna, s.HpMax AS hp_max, s.ManaMax AS mana_max, " +
                "cl.CodClasse AS id_classe, cl.Nome AS nome_classe, " +
                "r.CodRazza AS id_razza, r.Nome AS nome_razza " +
                "FROM ISCRIZIONE isc " +
                "JOIN CAMPAGNA c ON isc.CodCampagna = c.CodCampagna " +
                "LEFT JOIN PERSONAGGIO p ON isc.CodCampagna = p.CodCampagna AND isc.CodUtente = p.CodUtente " +
                "LEFT JOIN STATISTICA s ON p.CodPersonaggio = s.CodPersonaggio " +
                "LEFT JOIN CLASSE cl ON p.CodClasse = cl.CodClasse " +
                "LEFT JOIN RAZZA r ON p.CodRazza = r.CodRazza " +
                "WHERE isc.CodUtente = ?";


        try {
            Connection conn = ConnessioneDatabase.getInstance().connection;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, codUtente);

            ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    // 1. Ricostruzione Campagna (sempre presente)
                    int idCampagna = rs.getInt("id_campagna");
                    String nomeCampagna = rs.getString("nome_campagna");
                    int maxGiocatori = rs.getInt("max_g");
                    String statoCampagnaStr = rs.getString("stato_campagna");
                    boolean isIniziata = (statoCampagnaStr != null && statoCampagnaStr.equals("Iniziata"));

                    Campagna campagna = new Campagna(idCampagna, nomeCampagna, maxGiocatori, isIniziata, null);

                    Personaggio personaggio = null;

                    // 2. Verifica esistenza Personaggio
                    if (rs.getInt("id_pg") != 0) {
                        Classe classe = new Classe(rs.getInt("id_classe"), rs.getString("nome_classe"));
                        Razza razza = new Razza(rs.getInt("id_razza"), rs.getString("nome_razza"));

                        Statistica statBase = new Statistica(
                                rs.getInt("cost"),
                                rs.getInt("forza"),
                                rs.getInt("destrezza"),
                                rs.getInt("intel"),
                                rs.getInt("fede"),
                                rs.getInt("carisma"),
                                rs.getInt("fortuna"),
                                rs.getInt("hp_max"),
                                rs.getInt("mana_max")
                        );

                        personaggio = new Personaggio(
                                rs.getInt("id_pg"),
                                rs.getString("nome_pg"),
                                classe,
                                razza,
                                statBase,
                                rs.getInt("hp_att"),
                                rs.getInt("mana_att"),
                                rs.getInt("oro"),
                                rs.getInt("punti"),
                                rs.getBoolean("is_pg")
                        );
                    }

                    mappaPartecipazioni.put(campagna, personaggio);
                }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel recupero delle campagne dal database: " + e.getMessage());
        }

        return mappaPartecipazioni;
    }


}