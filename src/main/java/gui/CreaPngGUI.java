package gui;

import controller.Controller;
import model.Master;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Interfaccia grafica di popup riservata al {@link Master} per la creazione di un
 * Personaggio Non Giocante (PnG).
 * <p>
 * Offre due modalità di creazione: una di base, che genera il PnG utilizzando
 * i parametri standard di Razza e Classe, e una avanzata, che consente di
 * impostare manualmente Oro e Punti Statistica iniziali.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
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

    /** Il Controller di sistema per delegare la logica di creazione e salvataggio nel database. */
    private Controller controller;
    /** Il Master attualmente loggato che sta effettuando l'operazione. */
    private Master masterLoggato;
    /** Il nome della campagna in cui il nuovo PnG verrà inserito e salvato. */
    private String nomeCampagnaAttuale;
    /** Riferimento alla finestra di popup corrente, utilizzato per chiuderla dopo la creazione. */
    private JFrame frameAttuale;

    /**
     * Costruisce l'interfaccia di creazione del PnG, inizializzando i menu a tendina
     * e configurando la logica del selettore per sbloccare le opzioni avanzate.
     *
     * @param controller   Il {@link Controller} di riferimento.
     * @param master       L'oggetto {@link Master} che coordina la campagna.
     * @param nomeCampagna Il nome della campagna a cui associare il personaggio.
     * @param frame        Il {@link JFrame} (popup) all'interno del quale è ospitato questo pannello.
     */
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

    /**
     * Metodo privato  che popola i menu a tendina (Razze e Classi)
     * e imposta i limiti e lo stato iniziale dei selettori numerici per le statistiche.
     */
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

    /**
     * Restituisce il pannello principale della finestra di creazione, necessario
     * per il caricamento visivo all'interno del {@link JFrame}.
     *
     * @return Il {@link JPanel} contenente i campi di input e i pulsanti.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}