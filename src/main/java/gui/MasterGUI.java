package gui;

import controller.Controller;
import model.Campagna;
import model.Master;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Rappresenta l'interfaccia grafica principale (Dashboard) riservata agli utenti
 * con ruolo di {@link Master}.
 * <p>
 * Consente al Master di visualizzare l'elenco delle proprie campagne, crearne
 * di nuove stabilendo un limite di giocatori, eliminarle o accedervi.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class MasterGUI {
    //costante
    private static final int maxGiocatoriDefault = 5;

    private JLabel benvenutoMaster;
    private JTable tableCampagna;
    private JButton logoutButton;
    private JButton eliminaButton;
    private JButton creaButton;
    private JButton visualizzaButton;
    private JPanel mainPanel;

    /** Il Controller di riferimento per delegare le logiche di business e le chiamate al database. */
    private Controller controller;

    /**
     * Costruisce l'interfaccia della Dashboard del Master, inizializzando i componenti
     * visivi, la tabella delle campagne e configurando gli ascoltatori di eventi.
     *
     * @param controller Il {@link Controller} che gestisce le operazioni del sistema.
     */

    public MasterGUI(Controller controller) {

        this.controller = controller;

        JFrame frame = new JFrame("Dashboard Master - " + controller.getUtenteAttivo().getUsername());
        benvenutoMaster.setText("Benvenuto, "+controller.getUtenteAttivo().getUsername()+"! [Master]");
        inizializzaTabella();
        frame.setContentPane(getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        logoutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frame,
                        "Vuoi davvero fare logout?", "Conferma Uscita",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    controller.logout();
                    frame.dispose(); // Chiude questa dashboard
                    Home.main(null); // Riapre Home
                }
            }
        });

        // LOGICA CREA CAMPAGNA
        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nomeCampagna = JOptionPane.showInputDialog(frame, "Inserisci il nome della nuova Campagna:");

                // Controllo per evitare che l'utente clicchi 'Annulla' o inserisca campi vuoti
                if ((nomeCampagna != null) && (!nomeCampagna.trim().isEmpty())) {
                    try {
                        controller.creaCampagna(nomeCampagna, maxGiocatoriDefault); // per ora
                        JOptionPane.showMessageDialog(frame, "Campagna creata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                        // ricarica table per farla comparire
                        DefaultTableModel model = (DefaultTableModel) tableCampagna.getModel();
                        model.addRow(new Object[] {nomeCampagna, maxGiocatoriDefault, "Non iniziata"});
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore Creazione", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // LOGICA ELIMINA CAMPAGNA
        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagna.getSelectedRow();

                //controllo per gestire caso in cui master dimentichi di selezionare la campagna da eliminare
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona prima una campagna dalla tabella per eliminarla.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return; // Blocca tutto
                }


                String nomeDaEliminare = tableCampagna.getValueAt(rigaSelezionata, 0).toString();
                int conferma = JOptionPane.showConfirmDialog(frame,
                        "Sei sicuro di voler eliminare la campagna: " + nomeDaEliminare + "?",
                        "Conferma", JOptionPane.YES_NO_OPTION);

                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        if(controller.eliminaCampagna(nomeDaEliminare)){
                            DefaultTableModel model = (DefaultTableModel)tableCampagna.getModel();
                            model.removeRow(rigaSelezionata);
                        }
                        JOptionPane.showMessageDialog(frame, "Campagna eliminata con successo.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Errore durante l'eliminazione: "+ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        visualizzaButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagna.getSelectedRow();

                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frame, "Devi selezionare una campagna!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeCampagnaSelezionata = tableCampagna.getValueAt(rigaSelezionata, 0).toString();

                try {
                    controller.visualizzaCampagna(nomeCampagnaSelezionata); //setta la campagna, se la trova, come campagna attiva
                    frame.setVisible(false);
                    CampagnaMasterGUI regiaGUI = new CampagnaMasterGUI(controller, frame);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }

            }

        });

        //Ogni volta che il frame torna visibile aggiorna la tabella.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                inizializzaTabella();
            }
        });

    }

    /**
     * Metodo privato per impostare l'intestazione e il modello dati
     * della JTable delle campagne, rendendo le celle non modificabili al doppio clic.
     */
    private void inizializzaTabella() {
        String[] colonne = {"Nome Campagna", "Max Giocatori", "Stato"};

        // Usiamo un DefaultTableModel per non far modificare il testo all'utente con il doppio clic tramite metodo di Swing
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCampagna.setModel(model);
        tableCampagna.getTableHeader().setReorderingAllowed(false);
        tableCampagna.getTableHeader().setResizingAllowed(false);

        for(Campagna campagna : controller.getListaCampagne().keySet()){
            if(controller.getListaCampagne().get(campagna).equals(controller.getUtenteAttivo())){
                String stato = campagna.isIniziata() ? "In corso" : "Non iniziata";
                model.addRow(new Object[]{campagna.getNome(),
                        campagna.getMaxGiocatori(), stato});
                break;
            }
        }

    }

    /**
     * Restituisce il pannello principale della Dashboard, fondamentale per
     * l'inserimento all'interno del {@link JFrame} chiamante.
     *
     * @return Il {@link JPanel} contenente l'intera interfaccia.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}