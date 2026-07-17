package gui;

import controller.Controller;
import model.Campagna;
import model.Giocatore;
import model.Classe;
import model.Razza;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Rappresenta l'interfaccia grafica di popup dedicata alla creazione di un nuovo
 * Personaggio Giocante (PG) da parte di un utente.
 * <p>
 * Viene richiamata quando un {@link Giocatore} accede per la prima volta a una
 * campagna in cui non possiede ancora un PG. L'interfaccia permette di
 * definire l'identità del personaggio raccogliendone il nome, la razza e la classe.
 * Le opzioni disponibili nei menu a tendina sono generate dinamicamente in base
 * a ciò che il Master ha configurato per la campagna corrente.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class CreaPgGUI {
    private JPanel mainPanel;
    private JTextField campoNome;
    private JComboBox<Razza> razzaComboBox;
    private JComboBox<Classe> ClasseComboBox;
    private JPanel datiPgPanel;
    private JButton creaButton;
    private JLabel nome;
    private JLabel razza;
    private JLabel classe;

    /** Il Controller di sistema per delegare il salvataggio del nuovo personaggio. */
    private Controller controller;

    /** Il nome della campagna a cui il nuovo personaggio verrà indissolubilmente legato. */
    private String nomeCampagnaAttuale;

    /** La campagna in cui inserire il personaggio. **/

    private Campagna campagnaAttiva;


    /**
     * Costruisce l'interfaccia di creazione del Personaggio Giocante, popolando
     * i menu a tendina e configurando l'ascoltatore per il pulsante di conferma.
     *
     * @param controller   Il {@link Controller} che comunicherà con il database.
     * @param nomeCampagna Il nome della campagna di destinazione.
     */
    public CreaPgGUI(Controller controller, String nomeCampagna, JFrame frameChiamante) {
        this.controller = controller;
        this.nomeCampagnaAttuale = nomeCampagna;
        campagnaAttiva = controller.getCampagnaAttiva();

        JFrame frame = new JFrame("Creazione Personaggio - Campagna: " + nomeCampagna);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        popolaMenuATendina();

        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeInserito = campoNome.getText().trim();

                if (nomeInserito.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Inserisci un nome per il tuo eroe!",
                            "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (razzaComboBox.getSelectedItem() == null || ClasseComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Il Master non ha ancora creato Razze o Classi per questa campagna!",
                            "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Razza razzaSelezionata = (Razza)razzaComboBox.getSelectedItem();
                Classe classeSelezionata = (Classe)ClasseComboBox.getSelectedItem();

                try {
                    controller.creaNuovoPersonaggio(nomeInserito, razzaSelezionata, classeSelezionata, campagnaAttiva);

                    JOptionPane.showMessageDialog(frame,
                            "Personaggio creato con successo! L'avventura di " + nomeInserito + " sta per iniziare.",
                            "Successo", JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();

                    CampagnaGiocatoreGUI campagnaGUI = new CampagnaGiocatoreGUI(controller,
                            (Giocatore)controller.getUtenteAttivo(), nomeCampagnaAttuale, frameChiamante);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Metodo privato che inizializza le opzioni disponibili per la creazione
     * del personaggio, interrogando il database (tramite Controller) per estrarre
     * unicamente le Classi e le Razze abilitate per questa specifica campagna.
     */
    private void popolaMenuATendina() {
        razzaComboBox.removeAllItems();
        ClasseComboBox.removeAllItems();

        List<Razza> razzePermesse = controller.getRazzePerCampagna(campagnaAttiva);
        List<Classe> classiPermesse = controller.getClassiPerCampagna(campagnaAttiva);

        for (Razza r : razzePermesse) {
            razzaComboBox.addItem(r);
        }
        for (Classe c : classiPermesse) {
            ClasseComboBox.addItem(c);
        }
    }
}