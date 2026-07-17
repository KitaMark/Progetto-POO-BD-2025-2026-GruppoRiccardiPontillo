package gui;

import controller.Controller;
import model.Personaggio;
import model.Statistica;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica di popup dedicata alla modifica manuale degli attributi.
 * <p>
 * È progettata per tradurre in interfaccia visiva un privilegio specifico del ruolo
 * del Master: la capacità di modificare arbitrariamente le statistiche di tutti
 * i personaggi della sua campagna (siano essi PG o PnG) per necessità narrative.
 * Raccoglie i nuovi valori tramite dei selettori numerici e li invia al {@link Controller}.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class ModificaStatisticheGUI {
    private JPanel mainPanel;
    private JButton modificaButton;
    private JSpinner forzaSpinner;
    private JSpinner destrezzaSpinner;
    private JSpinner costituzioneSpinner;
    private JSpinner intelligenzaSpinner;
    private JSpinner fedeSpinner;
    private JSpinner carismaSpinner;
    private JSpinner fortunaSpinner;
    private JSpinner maxHpSpinner;
    private JSpinner manaMaxSpinner;
    private JLabel forza;
    private JLabel destrezza;
    private JLabel costituzione;
    private JLabel intelligenzaLabel;
    private JLabel fede;
    private JLabel carisma;
    private JLabel fortuna;
    private JLabel maxHp;
    private JLabel manaMax;
    private JPanel buttonPanel;

    /** Il Controller di sistema a cui delegare il salvataggio dei nuovi valori. */
    private Controller controller;

    /** Il nome identificativo del personaggio di cui si stanno modificando le statistiche. */
    private String nomePersonaggioSelezionato;

    /**
     * Costruisce l'interfaccia di modifica statistiche, caricando i valori attuali
     * del personaggio e abilitando l'ascoltatore per il pulsante di conferma.
     *
     * @param controller       Il {@link Controller} che comunicherà i nuovi dati al DAO.
     * @param nomePg           Il nome del personaggio bersaglio della modifica.
     * @param id               L'id univoco del personaggio.
     * @param isPg             Booleano per distinguere PG da PnG.
     * @param frameChiamante   Il Frame chiamante per la gestione del popup.
     */
    public ModificaStatisticheGUI(Controller controller, String nomePg, int id, boolean isPg, JFrame frameChiamante) {
        this.controller = controller;
        this.nomePersonaggioSelezionato = nomePg;

        Personaggio pgDaModificare = null;
        try {
            pgDaModificare = controller.cercaPersonaggio(id, isPg);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frameChiamante, "Errore nel caricamento del personaggio: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return; // Blocca la creazione della finestra se il personaggio non esiste
        }

        JDialog frame = new JDialog(frameChiamante, "Modifica Statistiche - " + nomePg, true);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        inizializzaSpinner(pgDaModificare);

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frame, "Le statistiche del personaggio " +
                                "saranno sovrascritte forzatamente. Vuoi continuare?", "Conferma modifica",
                        JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.NO_OPTION) return;

                try {
                    int forzaVal = (int) forzaSpinner.getValue();
                    int destrezzaVal = (int) destrezzaSpinner.getValue();
                    int costituzioneVal = (int) costituzioneSpinner.getValue();
                    int intelligenzaVal = (int) intelligenzaSpinner.getValue();
                    int fedeVal = (int) fedeSpinner.getValue();
                    int carismaVal = (int) carismaSpinner.getValue();
                    int fortunaVal = (int) fortunaSpinner.getValue();
                    int hpMaxVal = (int) maxHpSpinner.getValue();
                    int manaMaxVal = (int) manaMaxSpinner.getValue();

                    controller.salvaStatisticheModificate(nomePersonaggioSelezionato, id, forzaVal, destrezzaVal,
                            costituzioneVal, intelligenzaVal, fedeVal, carismaVal,
                            fortunaVal, hpMaxVal, manaMaxVal, isPg);

                    JOptionPane.showMessageDialog(frame,
                            "Statistiche di " + nomePersonaggioSelezionato + " aggiornate con successo!",
                            "Successo", JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose(); // Chiudiamo il popup

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Metodo privato che configura i modelli numerici per ciascun {@link JSpinner}.
     * Imposta dinamicamente il valore di partenza prelevandolo dalle statistiche
     * attuali del personaggio selezionato.
     *
     * @param pg Il personaggio di cui pre-caricare le statistiche base.
     */
    private void inizializzaSpinner(Personaggio pg) {
        Statistica statAttuali = pg.getStatisticheBase();

        forzaSpinner.setModel(new SpinnerNumberModel(statAttuali.getForza(), 1, 100, 1));
        destrezzaSpinner.setModel(new SpinnerNumberModel(statAttuali.getDestrezza(), 1, 100, 1));
        costituzioneSpinner.setModel(new SpinnerNumberModel(statAttuali.getCostituzione(), 1, 100, 1));
        intelligenzaSpinner.setModel(new SpinnerNumberModel(statAttuali.getIntelligenza(), 1, 100, 1));
        fedeSpinner.setModel(new SpinnerNumberModel(statAttuali.getFede(), 1, 100, 1));
        carismaSpinner.setModel(new SpinnerNumberModel(statAttuali.getCarisma(), 1, 100, 1));
        fortunaSpinner.setModel(new SpinnerNumberModel(statAttuali.getFortuna(), 1, 100, 1));

        maxHpSpinner.setModel(new SpinnerNumberModel(statAttuali.getHpMax(), 1, 9999, 1));
        manaMaxSpinner.setModel(new SpinnerNumberModel(statAttuali.getManaMax(), 1, 9999, 1));
    }

    /**
     * Restituisce il pannello principale della finestra di modifica, necessario
     * per il caricamento visivo all'interno del {@link JFrame}.
     *
     * @return Il {@link JPanel} contenente i campi numerici e i pulsanti.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}