package gui;

import controller.Controller;

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
     * Costruisce l'interfaccia di modifica statistiche, preimpostando i limiti numerici
     * dei campi e abilitando l'ascoltatore per il pulsante di conferma.
     *
     * @param controller      Il {@link Controller} che comunicherà i nuovi dati al DAO.
     * @param nomePg Il nome del personaggio bersaglio della modifica.
     * @param id l'id univoco del personaggio.
     */
    public ModificaStatisticheGUI(Controller controller, String nomePg, int id, boolean isPg, JFrame frameChiamante) {
        this.controller = controller;
        nomePersonaggioSelezionato = nomePg;

        JDialog frame = new JDialog(frameChiamante, "Modifica Statistiche - " + nomePg, true);
        frame.setContentPane(mainPanel);
        // Usiamo DISPOSE_ON_CLOSE per chiudere solo questo popup e non tutto
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);


        inizializzaSpinner();

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frame, "Le statistiche del personaggio " +
                        "saranno sovrascritte forzatamente. Vuoi continuare?", "Conferma modifica",
                        JOptionPane.YES_NO_OPTION);
                if(conferma == JOptionPane.NO_OPTION) return;
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
     * Imposta il valore di partenza, il limite minimo, il limite massimo e il l' incremento.
     */
    private void inizializzaSpinner() {
        // Immpostazoni di min e max (da definire max)
        forzaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        destrezzaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        costituzioneSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        intelligenzaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        fedeSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        carismaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        fortunaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));

        //(da definire max per hp e mana)
        maxHpSpinner.setModel(new SpinnerNumberModel(100, 1, 9999, 1));
        manaMaxSpinner.setModel(new SpinnerNumberModel(50, 1, 9999, 1));
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