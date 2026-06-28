package gui;

import controller.Controller;
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

    /** Il Controller di sistema per delegare il salvataggio del nuovo personaggio. */
    private Controller controller;

    /** Il Giocatore attualmente loggato che sta creando il proprio PG. */
    private Giocatore giocatoreLoggato;

    /** Il nome della campagna a cui il nuovo personaggio verrà indissolubilmente legato. */
    private String nomeCampagnaAttuale;

    /** Riferimento alla finestra corrente, utilizzato per chiudere il popup terminata l'operazione. */
    private JFrame frameAttuale;


    /**
     * Costruisce l'interfaccia di creazione del Personaggio Giocante, popolando
     * i menu a tendina e configurando l'ascoltatore per il pulsante di conferma.
     *
     * @param controller   Il {@link Controller} che comunicherà con il database.
     * @param giocatore    L'oggetto {@link Giocatore} che sta effettuando l'azione.
     * @param nomeCampagna Il nome della campagna di destinazione.
     * @param frame        Il {@link JFrame} (popup) all'interno del quale è ospitato questo pannello.
     */
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

                if (nomeInserito.isEmpty()) {
                    JOptionPane.showMessageDialog(frameAttuale,
                            "Inserisci un nome per il tuo eroe!",
                            "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (razzaComboBox.getSelectedItem() == null || ClasseComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(frameAttuale,
                            "Il Master non ha ancora creato Razze o Classi per questa campagna!",
                            "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String razzaSelezionata = razzaComboBox.getSelectedItem().toString();
                String classeSelezionata = ClasseComboBox.getSelectedItem().toString();

                try {
                    controller.creaNuovoPersonaggio(nomeInserito, razzaSelezionata, classeSelezionata, nomeCampagnaAttuale);

                    JOptionPane.showMessageDialog(frameAttuale,
                            "Personaggio creato con successo! L'avventura di " + nomeInserito + " sta per iniziare.",
                            "Successo", JOptionPane.INFORMATION_MESSAGE);

                    frameAttuale.dispose();

                    JFrame campagnaFrame = new JFrame("Scheda Personaggio - Campagna: " + nomeCampagnaAttuale);
                    CampagnaGiocatoreGUI campagnaGUI = new CampagnaGiocatoreGUI(
                            controller,
                            giocatoreLoggato,
                            nomeCampagnaAttuale,
                            campagnaFrame
                    );

                    campagnaFrame.setContentPane(campagnaGUI.getMainPanel());
                    campagnaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    campagnaFrame.setSize(900, 600);
                    campagnaFrame.setLocationRelativeTo(null); // Centra la schermata dell'inventario/statistiche
                    campagnaFrame.setVisible(true); // Mostra la plancia operativa del giocatore

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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

        List<Razza> razzePermesse = controller.getRazzePerCampagna(nomeCampagnaAttuale);
        List<Classe> classiPermesse = controller.getClassiPerCampagna(nomeCampagnaAttuale);

        for (Razza r : razzePermesse) {
            razzaComboBox.addItem(r.getNome());
        }
        for (Classe c : classiPermesse) {
            ClasseComboBox.addItem(c.getNome());
        }
    }

    /**
     * Restituisce il pannello principale della finestra di creazione, necessario
     * per l'inserimento visivo all'interno del {@link JFrame}.
     *
     * @return Il {@link JPanel} contenente l'intero form di registrazione del PG.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}