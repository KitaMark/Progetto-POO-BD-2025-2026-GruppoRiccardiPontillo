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
    private JSpinner inteliggenzaSpinner;
    private JSpinner fedeSpinner;
    private JSpinner carismaSpinner;
    private JSpinner fortunaSpinner;
    private JSpinner maxHpSpinner;
    private JSpinner manaMaxSpinner;
    private JLabel forza;
    private JLabel destrezza;
    private JLabel costituzione;
    private JLabel inteligenza;
    private JLabel fede;
    private JLabel carisma;
    private JLabel fortuna;
    private JLabel maxHp;
    private JLabel manaMax;

    /** Il Controller di sistema a cui delegare il salvataggio dei nuovi valori. */
    private Controller controller;

    /** Il nome identificativo del personaggio di cui si stanno modificando le statistiche. */
    private String nomePersonaggioSelezionato;

    /** Riferimento alla finestra di popup corrente, utilizzato per chiuderla dopo il salvataggio. */
    private JFrame frameAttuale;


    /**
     * Costruisce l'interfaccia di modifica statistiche, preimpostando i limiti numerici
     * dei campi e abilitando l'ascoltatore per il pulsante di conferma.
     *
     * @param controller      Il {@link Controller} che comunicherà i nuovi dati al DAO.
     * @param nomePersonaggio Il nome del personaggio bersaglio della modifica.
     * @param frame           Il {@link JFrame} che ospita questo pannello (popup).
     */
    public ModificaStatisticheGUI(Controller controller, String nomePersonaggio, JFrame frame) {
        this.controller = controller;
        this.nomePersonaggioSelezionato = nomePersonaggio;
        this.frameAttuale = frame;

        inizializzaSpinner();

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int forzaVal = (int) forzaSpinner.getValue();
                    int destrezzaVal = (int) destrezzaSpinner.getValue();
                    int costituzioneVal = (int) costituzioneSpinner.getValue();
                    int intelligenzaVal = (int) inteliggenzaSpinner.getValue();
                    int fedeVal = (int) fedeSpinner.getValue();
                    int carismaVal = (int) carismaSpinner.getValue();
                    int fortunaVal = (int) fortunaSpinner.getValue();
                    int hpMaxVal = (int) maxHpSpinner.getValue();
                    int manaMaxVal = (int) manaMaxSpinner.getValue();

                    controller.salvaStatisticheModificate(nomePersonaggioSelezionato, forzaVal, destrezzaVal,
                            costituzioneVal, intelligenzaVal, fedeVal, carismaVal,
                            fortunaVal, hpMaxVal, manaMaxVal);

                    JOptionPane.showMessageDialog(frameAttuale,
                            "Statistica di " + nomePersonaggioSelezionato + " aggiornate con successo!",
                            "Successo", JOptionPane.INFORMATION_MESSAGE);

                    frameAttuale.dispose(); // Chiudiamo il popup

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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
        inteliggenzaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        fedeSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        carismaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));
        fortunaSpinner.setModel(new SpinnerNumberModel(10, 1, 100, 1));

        //(da definire max per hp e mana)
        maxHpSpinner.setModel(new SpinnerNumberModel(100, 1, 9999, 1));
        manaMaxSpinner.setModel(new SpinnerNumberModel(50, 0, 9999, 1));
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