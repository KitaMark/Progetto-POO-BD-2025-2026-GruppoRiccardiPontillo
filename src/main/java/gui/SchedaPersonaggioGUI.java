package gui;

import controller.Controller;
import model.Abilita;
import model.OggettoConsumabile;
import model.OggettoEquipaggiabile;
import model.Personaggio;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.Map;
import java.awt.*;

/**
 * Rappresenta l'interfaccia grafica (di sola lettura) dedicata all'ispezione
 * completa dei dettagli di un Personaggio (sia esso PG o PnG).
 * <p>
 * Questa finestra organizza i dati in sezioni tramite un'interfaccia a schede,
 * mostrando i valori anagrafici, le statistiche finali (aggiornate in tempo reale
 * con i bonus dell'equipaggiamento), il riepilogo dell'inventario e le abilità apprese.
 * Viene utilizzata principalmente dal Master per la consultazione rapida dello stato del party.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class SchedaPersonaggioGUI extends JDialog {
    private JPanel contentPane;
    private JLabel forzaLabel;
    private JLabel destrezzaLabel;
    private JLabel costituzioneLabel;
    private JLabel fortunaLabel;
    private JLabel fedeLabel;
    private JLabel carismaLabel;
    private JLabel intelligenzaLabel;
    private JProgressBar hpBar;
    private JProgressBar manaBar;
    private JLabel razzaLabel;
    private JLabel classeLabel;
    private JLabel nomeTesto;
    private JLabel razzaTesto;
    private JLabel classeTesto;
    private JLabel forzaTesto;
    private JLabel destrezzaTesto;
    private JTabbedPane mainTab;
    private JLabel costituzioneTesto;
    private JLabel intelligenzaTesto;
    private JLabel carismaTesto;
    private JLabel fedeTesto;
    private JLabel fortunaTesto;
    private JLabel hpLabel;
    private JLabel manaLabel;
    private JPanel statPane;
    private JPanel inventarioPane;
    private JLabel nomeLabel;
    private JPanel abilitaPane;
    private JTable abilitaTable;
    private JLabel puntiStatisticaLabel;
    private JLabel puntiTesto;
    private JTabbedPane inventarioTabbedPane;
    private JPanel equipaggiabiliPane;
    private JScrollPane equipaggiabiliScrollPane;
    private JTable equipaggiabiliTable;
    private JPanel slotConsumabiliPane;
    private JScrollPane consumabiliPane;
    private JTable consumabiliTable;
    private JLabel oroLabel;
    private JLabel oroTesto;

    /** Il Controller di sistema per delegare il fetching dei dati dal modello. */
    private Controller controller;

    /** Il Personaggio di cui si stanno ispezionando i dettagli. */
    private Personaggio pgAttivo;

    /**
     * Costruisce l'interfaccia di visualizzazione della Scheda Personaggio.
     * Configura le progress bar per le risorse vitali (HP e Mana) e popola le
     * etichette con i valori statici e dinamici del profilo.
     *
     * @param frameChiamante Il frame genitore da cui viene aperta la finestra (comportamento modale).
     * @param controller     Il {@link Controller} di sistema.
     * @param isPg           Flag booleano che indica se l'entità ispezionata è un PG (true) o PnG (false).
     * @param pg             L'oggetto {@link Personaggio} caricato in memoria da esaminare.
     */
    public SchedaPersonaggioGUI(JFrame frameChiamante, Controller controller, boolean isPg, Personaggio pg) {
        super(frameChiamante, "Scheda "+pg.getNome()+ ((isPg)? "" : " - [PnG]"), true);
        this.controller = controller;
        this.pgAttivo = pg;
        super.setContentPane(contentPane);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(false);

        inizializzaDatiPrincipali();
        aggiungiListenerTabs();

        super.setMinimumSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    /**
     * Aggiunge un ChangeListener al JTabbedPane principale, garantendo che
     * le tabelle (inventario e abilità) vengano caricate e aggiornate dinamicamente
     * solo quando l'utente naviga nella rispettiva scheda, ottimizzando le risorse.
     */
    private void aggiungiListenerTabs() {
        mainTab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int tabSelezionata = mainTab.getSelectedIndex();

                if(tabSelezionata == 1){
                    inizializzaInventarioEquipaggiabili();
                    inizializzaInventarioConsumabili();
                }
                else if(tabSelezionata == 2){
                    inizializzaListaAbilita();
                }
            }
        });
    }

    /**
     * Popola le etichette testuali (JLabel) e le barre di caricamento (JProgressBar)
     * della prima scheda interrogando le statistiche finali del personaggio,
     * includendo i modificatori derivati dalla razza e dagli oggetti attualmente equipaggiati.
     */
    private void inizializzaDatiPrincipali(){
        nomeTesto.setText(pgAttivo.getNome());
        razzaTesto.setText(pgAttivo.getRazza().toString());
        classeTesto.setText(pgAttivo.getClasse().toString());

        Color hpFill = new Color(139, 38, 53);
        Color hpEmpty = new Color(55, 20, 25);
        Color manaFill = new Color(112, 66, 122);
        Color manaEmpty = new Color(45, 25, 50);

        hpBar.setMinimum(0);
        hpBar.setMaximum(pgAttivo.getStatisticheFinali().getHpMax());
        hpBar.setValue(pgAttivo.getHpCorrenti());
        hpBar.setStringPainted(true);
        hpBar.setString(pgAttivo.getHpCorrenti() + "/" + pgAttivo.getStatisticheFinali().getHpMax());
        hpBar.setForeground(hpFill);
        hpBar.setBackground(hpEmpty);
        hpBar.putClientProperty("JProgressBar.selectionForeground", Color.WHITE);
        hpBar.putClientProperty("JProgressBar.selectionBackground", Color.LIGHT_GRAY);

        manaBar.setMinimum(0);
        manaBar.setMaximum(pgAttivo.getStatisticheFinali().getManaMax());
        manaBar.setValue(pgAttivo.getManaCorrente());
        manaBar.setStringPainted(true);
        manaBar.setString(pgAttivo.getManaCorrente() + "/" + pgAttivo.getStatisticheFinali().getManaMax());
        manaBar.setForeground(manaFill);
        manaBar.setBackground(manaEmpty);
        manaBar.putClientProperty("JProgressBar.selectionForeground", Color.WHITE);
        manaBar.putClientProperty("JProgressBar.selectionBackground", Color.LIGHT_GRAY);

        forzaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getForza()));
        destrezzaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getDestrezza()));
        costituzioneTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getCostituzione()));
        intelligenzaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getIntelligenza()));
        carismaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getCarisma()));
        fedeTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getFede()));
        fortunaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getFortuna()));
        puntiTesto.setText(String.valueOf(pgAttivo.getPuntiStatistica()));
        oroTesto.setText(String.valueOf(pgAttivo.getOro()));
    }

    /**
     * Costruisce il modello dati per la tabella che riassume gli Equipaggiabili.
     * Le celle sono rese "Read-Only". Mostra il nome dell'oggetto e lo stato
     * di equipaggiamento (indossato o nello zaino).
     */
    private void inizializzaInventarioEquipaggiabili(){
        String[] colonne = {"Nome", "Stato", "ID"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };

        equipaggiabiliTable.setModel(model);
        equipaggiabiliTable.getTableHeader().setResizingAllowed(false);
        equipaggiabiliTable.getTableHeader().setReorderingAllowed(false);
        TableColumn colonnaID = equipaggiabiliTable.getColumnModel().getColumn(2);
        equipaggiabiliTable.removeColumn(colonnaID);

        for(OggettoEquipaggiabile equipaggiabile : pgAttivo.getInventarioEquipaggiabili().keySet()){
            String stato = pgAttivo.getInventarioEquipaggiabili().get(equipaggiabile)? "Equipaggiato" : "Non equipaggiato";
            model.addRow(new Object[]{equipaggiabile.getNome(), stato, equipaggiabile.getId()});
        }
    }

    /**
     * Costruisce il modello dati per la tabella che riassume i Consumabili.
     * La tabella mostra il nome dell'oggetto (es. Pozione) e la sua quantità nello zaino.
     */
    private void inizializzaInventarioConsumabili(){
        String[] colonne = {"Nome", "Quantità", "ID"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };

        consumabiliTable.setModel(model);
        consumabiliTable.getTableHeader().setResizingAllowed(false);
        consumabiliTable.getTableHeader().setReorderingAllowed(false);
        TableColumn colonnaID = consumabiliTable.getColumnModel().getColumn(2);
        consumabiliTable.removeColumn(colonnaID);

        for(Map.Entry<OggettoConsumabile, Integer> consumabile: pgAttivo.getInventarioConsumabili().entrySet()){
            model.addRow(new Object[]{consumabile.getKey().getNome(), consumabile.getValue(), consumabile.getKey().getId()});
        }
    }

    /**
     * Popola la tabella della terza scheda con la lista dei talenti e incantesimi
     * appresi dal personaggio, corredandoli di descrizione in linea.
     */
    private void inizializzaListaAbilita(){
        String[] colonne = {"Nome", "Descrizione"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };

        abilitaTable.setModel(model);
        abilitaTable.getTableHeader().setResizingAllowed(false);
        abilitaTable.getTableHeader().setReorderingAllowed(false);

        for(Abilita abilita: pgAttivo.getListaAbilita()){
            model.addRow(new String[]{abilita.getNome(), abilita.getDescrizione()});
        }
    }
}