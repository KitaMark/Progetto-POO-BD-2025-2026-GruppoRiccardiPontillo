package gui;

import controller.Controller;
import exception.DatiMancantiException;
import model.Classe;
import model.Master;
import model.Razza;
import model.Statistica;

import javax.swing.*;
import java.awt.*;
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
    private JComboBox<Razza> razzaComboBox;
    private JComboBox<Classe> ClasseComboBox;
    private JCheckBox avanzateCheckBox;
    private JLabel oroLabel;
    private JSpinner oroSpinner;
    private JLabel puntiStatLabel;
    private JSpinner puntiStatSpinner; // Corrisponde ai Punti Statistica
    private JButton creaButton;
    private JLabel forzaLabel;
    private JSpinner forzaSpinner;
    private JSpinner destrezzaSpinner;
    private JLabel destrezzaLabel;
    private JLabel costituzioneLabel;
    private JSpinner costituzioneSpinner;
    private JLabel intelligenzaLabel;
    private JSpinner intelligenzaSpinner;
    private JSpinner fortunaSpinner;
    private JSpinner carismaSpinner;
    private JSpinner fedeSpinner;
    private JSpinner manaMaxSpinner;
    private JSpinner hpMaxSpinner;
    private JLabel hpLabel;
    private JLabel manaLabel;
    private JPanel campiPanel;
    private JLabel fedeLabel;
    private JLabel carismaLabel;
    private JLabel fortunaLabel;
    private JPanel impostazioniAvanzatePanel;

    /** Il Controller di sistema per delegare la logica di creazione e salvataggio nel database. */
    private Controller controller;
    /** Il Master attualmente loggato che sta effettuando l'operazione. */
    private Master masterLoggato;


    /**
     * Costruisce l'interfaccia di creazione del PnG, inizializzando i menu a tendina
     * e configurando la logica del selettore per sbloccare le opzioni avanzate.
     *
     * @param controller   Il {@link Controller} di riferimento.
     * @param master       L'oggetto {@link Master} che coordina la campagna.
     */
    public CreaPngGUI(Controller controller, Master master, JFrame frameChiamante) {
        this.controller = controller;
        this.masterLoggato = master;

        inizializzaComponenti();

        JDialog frame = new JDialog(frameChiamante, "Creazione Png", true); //in modo da avere un comportamento da popup
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);



        avanzateCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isAttivo = avanzateCheckBox.isSelected();
                setComponentiAvanzatiVisibili(isAttivo);
            }
        });


        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String nomeInserito = campoNome.getText().trim();
                    Razza razzaSelezionata = (Razza)razzaComboBox.getSelectedItem();
                    Classe classeSelezionata = (Classe)ClasseComboBox.getSelectedItem();
                    if (avanzateCheckBox.isSelected()) {
                        int oroIniziale = (int) oroSpinner.getValue();
                        int puntiIniziali = (int) puntiStatSpinner.getValue();
                        int forza = (int) forzaSpinner.getValue();
                        int destrezza = (int) destrezzaSpinner.getValue();
                        int costituzione = (int) costituzioneSpinner.getValue();
                        int intelligenza = (int) intelligenzaSpinner.getValue();
                        int carisma = (int) carismaSpinner.getValue();
                        int fede = (int) fedeSpinner.getValue();
                        int fortuna = (int) fortunaSpinner.getValue();
                        int hpMax = (int) hpMaxSpinner.getValue();
                        int manaMax = (int) manaMaxSpinner.getValue();
                        Statistica stat = new Statistica(costituzione, forza, destrezza, intelligenza, fede, carisma, fortuna, hpMax, manaMax);
                        controller.creaPnG(nomeInserito, razzaSelezionata, classeSelezionata, oroIniziale, puntiIniziali, stat);
                    } else {
                        controller.creaPnG(nomeInserito, razzaSelezionata, classeSelezionata);
                    }

                    JOptionPane.showMessageDialog(frame, "PnG creato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // Chiude il popup

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //poiché si tratta di una finestra modale e non di un JFrame, queste istruzioni vanno alla fine: da
        //queste istruzioni in poi il flusso viene "bloccato"; se messe all'inizio, bloccherebbero la configurazione
        //dei listener.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Metodo privato  che popola i menu a tendina (Razze e Classi)
     * e imposta i limiti e lo stato iniziale dei selettori numerici per le statistiche.
     */
    private void inizializzaComponenti() {
        // Popoliamo le Razze
        for(Razza razza : controller.getCampagnaAttiva().getListaRazze()){
            razzaComboBox.addItem(razza);
        }

        // Popoliamo le Classi
        for(Classe classe : controller.getCampagnaAttiva().getListaClassi()){
            ClasseComboBox.addItem(classe);
        }

        oroSpinner.setModel(new SpinnerNumberModel(0, 0, 9999, 1));
        puntiStatSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        carismaSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        costituzioneSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        destrezzaSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        fedeSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        fortunaSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        forzaSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        intelligenzaSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        hpMaxSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        manaMaxSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));

        setComponentiAvanzatiVisibili(false);

    }

    private void setComponentiAvanzatiVisibili(boolean visibile) {
        impostazioniAvanzatePanel.setVisible(visibile);

        // Forza il contenitore principale a ricalcolare gli spazi vuoti
        mainPanel.revalidate();
        mainPanel.repaint();

        // Se il JDialog è già visibile, ridimensiona la finestra alla nuova dimensione ottimale
        Window window = SwingUtilities.getWindowAncestor(mainPanel);
        if (window != null) {
            window.pack();
        }
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