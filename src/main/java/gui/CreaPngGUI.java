package gui;

import controller.Controller;
import model.Master;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreaPngGUI {
    // Variabili del UI Designer
    private JPanel mainPanel;
    private JLabel nome;
    private JLabel razza;
    private JLabel classe;
    private JTextField campoNome;
    private JComboBox razzaComboBox;
    private JComboBox ClasseComboBox;
    private JCheckBox avanzateCheckBox;
    private JLabel oro;
    private JSpinner OroSpinner;
    private JLabel statistica;
    private JSpinner spinner1; // Corrisponde ai Punti Statistica
    private JButton creaButton;

    private Controller controller;
    private Master masterLoggato;
    private String nomeCampagnaAttuale;
    private JFrame frameAttuale;

    public CreaPngGUI(Controller controller, Master master, String nomeCampagna, JFrame frame) {
        this.controller = controller;
        this.masterLoggato = master;
        this.nomeCampagnaAttuale = nomeCampagna;
        this.frameAttuale = frame;

        inizializzaComponenti();


        avanzateCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se c'è la spunta, i campi diventano cliccabili (true), altrimenti si spengono (false)
                boolean isAttivo = avanzateCheckBox.isSelected();
                OroSpinner.setEnabled(isAttivo);
                spinner1.setEnabled(isAttivo);
            }
        });


        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeInserito = campoNome.getText().trim();

                if (nomeInserito.isEmpty()) {
                    JOptionPane.showMessageDialog(frameAttuale, "Inserisci un nome per il PnG!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String razzaSelezionata = razzaComboBox.getSelectedItem().toString();
                String classeSelezionata = ClasseComboBox.getSelectedItem().toString();

                try {
                    // IL BIVIO: Controlliamo se la spunta è attiva
                    if (avanzateCheckBox.isSelected()) {
                        // Creazione Avanzata
                        int oroIniziale = (int) OroSpinner.getValue();
                        int puntiIniziali = (int) spinner1.getValue();
                        controller.creaPnGAvanzato(nomeInserito, razzaSelezionata, classeSelezionata, oroIniziale, puntiIniziali, nomeCampagnaAttuale);
                    } else {
                        // Creazione Base
                        controller.creaPnGBase(nomeInserito, razzaSelezionata, classeSelezionata, nomeCampagnaAttuale);
                    }

                    JOptionPane.showMessageDialog(frameAttuale, "PnG creato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                    frameAttuale.dispose(); // Chiude il popup

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void inizializzaComponenti() {
        // Popoliamo le Razze
        razzaComboBox.addItem("Mostro/Bestia");
        razzaComboBox.addItem("Umano");
        razzaComboBox.addItem("Orco");
        razzaComboBox.addItem("Non-Morto");

        // Popoliamo le Classi
        ClasseComboBox.addItem("Guerriero");
        ClasseComboBox.addItem("Mago");
        ClasseComboBox.addItem("Nessuna"); // I mostri base potrebbero non avere classe

        // Impediamo che i JSpinner vadano sotto lo zero (Valore iniziale, Min, Max, Step)
        OroSpinner.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
        spinner1.setModel(new SpinnerNumberModel(0, 0, 100, 1));

        // Partono disattivati per colpa della CheckBox
        OroSpinner.setEnabled(false);
        spinner1.setEnabled(false);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}