package gui;

import controller.Controller;
import model.Giocatore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreaPgGUI {
    // Variabili generate dal tuo UI Designer
    private JPanel mainPanel;
    private JTextField campoNome;
    private JComboBox razzaComboBox;
    private JComboBox ClasseComboBox;
    private JPanel datiPgPanel;
    private JButton creaButton;
    private JLabel nome;
    private JLabel razza;
    private JLabel classe;

    private Controller controller;
    private Giocatore giocatoreLoggato;
    private String nomeCampagnaAttuale;
    private JFrame frameAttuale;

    public CreaPgGUI(Controller controller, Giocatore giocatore, String nomeCampagna, JFrame frame) {
        this.controller = controller;
        this.giocatoreLoggato = giocatore;
        this.nomeCampagnaAttuale = nomeCampagna;
        this.frameAttuale = frame;

        popolaMenuATendina();

        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeInserito = campoNome.getText().trim();

                // Controllo per evitare che l'utente lasci il campo vuoto
                if (nomeInserito.isEmpty()) {
                    JOptionPane.showMessageDialog(frameAttuale,
                            "Inserisci un nome per il tuo eroe!",
                            "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Recuperiamo le scelte dai menu a tendina
                String razzaSelezionata = razzaComboBox.getSelectedItem().toString();
                String classeSelezionata = ClasseComboBox.getSelectedItem().toString();

                try {
                    // Passiamo tutto al Controller
                    controller.creaNuovoPersonaggio(nomeInserito, razzaSelezionata, classeSelezionata, nomeCampagnaAttuale);

                    JOptionPane.showMessageDialog(frameAttuale,
                            "Personaggio creato con successo! L'avventura di " + nomeInserito + " sta per iniziare.",
                            "Successo", JOptionPane.INFORMATION_MESSAGE);

                    // Chiudiamo il popup di creazione
                    frameAttuale.dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    private void popolaMenuATendina() {
        // Opzioni per la Razza
        razzaComboBox.addItem("Umano");
        razzaComboBox.addItem("Elfo");
        razzaComboBox.addItem("Nano");
        razzaComboBox.addItem("Orco");

        // Opzioni per la Classe
        ClasseComboBox.addItem("Guerriero");
        ClasseComboBox.addItem("Mago");
        ClasseComboBox.addItem("Ladro");
        ClasseComboBox.addItem("Chierico");
    }

    // Fondamentale per far mostrare la form nel JFrame
    public JPanel getMainPanel() {
        return mainPanel;
    }
}