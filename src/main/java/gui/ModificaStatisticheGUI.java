package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private Controller controller;
    private String nomePersonaggioSelezionato;
    private JFrame frameAttuale;

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
                            "Statistiche di " + nomePersonaggioSelezionato + " aggiornate con successo!",
                            "Successo", JOptionPane.INFORMATION_MESSAGE);

                    frameAttuale.dispose(); // Chiudiamo il popup

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

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

    public JPanel getMainPanel() {
        return mainPanel;
    }
}