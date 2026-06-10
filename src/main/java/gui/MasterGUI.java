package gui;

import controller.Controller;
import model.Campagna;
import model.Master;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JLabel benvenutoMaster;
    private JTable tableCampagna;
    private JButton logoutButton;
    private JButton eliminaButton;
    private JButton creaButton;
    private JButton entraButton;
    private JPanel mainPanel;

    /** Il Controller di riferimento per delegare le logiche di business e le chiamate al database. */
    private Controller controller;

    /** L'entità dell'utente attualmente loggato che sta visualizzando questa interfaccia. */
    private Master masterLoggato;

    /** Riferimento al frame attuale, necessario per la chiusura (dispose) durante la navigazione. */
    private JFrame frameAttuale;


    /**
     * Costruisce l'interfaccia della Dashboard del Master, inizializzando i componenti
     * visivi, la tabella delle campagne e configurando gli ascoltatori di eventi.
     *
     * @param controller Il {@link Controller} che gestisce le operazioni del sistema.
     * @param master     L'oggetto {@link Master} corrispondente all'utente autenticato.
     * @param frame      Il {@link JFrame} all'interno del quale è ospitato questo pannello.
     */
    public MasterGUI(Controller controller, Master master, JFrame frame) {
        this.controller = controller;
        this.masterLoggato = master;
        this.frameAttuale = frame;

        // messaggio di benvenuto
        benvenutoMaster.setText("Benvenuto Master! [" + masterLoggato.getUsername() + "]");

        //  chiamata al metodo provvisorio che inizializza la table
        inizializzaTabella();


        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frameAttuale,
                        "Vuoi davvero fare logout?", "Conferma Uscita",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    controller.logout();
                    frameAttuale.dispose(); // Chiude questa dashboard
                    Home.main(null); // Riapre Home
                }
            }
        });

        // LOGICA CREA CAMPAGNA
        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeCampagna = JOptionPane.showInputDialog(frameAttuale, "Inserisci il nome della nuova Campagna:");

                // Controllo per evitare che l'utente clicchi 'Annulla' o inserisca campi vuoti
                if ((nomeCampagna != null) && (!nomeCampagna.trim().isEmpty())) {
                    try {
                        controller.creaCampagna(nomeCampagna, 5); // per ora
                        JOptionPane.showMessageDialog(frameAttuale, "Campagna creata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                        // ricarica table per farla comparire
                        DefaultTableModel model = (DefaultTableModel) tableCampagna.getModel();
                        model.addRow(new Object[] {nomeCampagna, 5, "Non iniziata"});
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Creazione", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona prima una campagna dalla tabella per eliminarla.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return; // Blocca tutto
                }

                // Prendiamo il nome dalla prima colonna (indice 0)
                String nomeDaEliminare = tableCampagna.getValueAt(rigaSelezionata, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frameAttuale,
                        "Sei sicuro di voler eliminare la campagna: " + nomeDaEliminare + "?",
                        "Conferma", JOptionPane.YES_NO_OPTION);

                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        //controlla se il master loggato corrisponde al master della campagna che si vuole eliminare
                        if(!controllaPrivilegiMaster(controller.cercaCampagna(nomeDaEliminare))){
                            JOptionPane.showMessageDialog(frameAttuale, "Non hai i permessi per eliminare la campagna.",
                                    "Errore", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(controller.eliminaCampagna(nomeDaEliminare)){
                            DefaultTableModel model = (DefaultTableModel)tableCampagna.getModel();
                            model.removeRow(rigaSelezionata);
                        }
                        JOptionPane.showMessageDialog(frameAttuale, "Campagna eliminata con successo.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, "Errore durante l'eliminazione: "+ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        entraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagna.getSelectedRow();

                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona una campagna per entrarvi.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeCampagnaSelezionata = tableCampagna.getValueAt(rigaSelezionata, 0).toString();
                try {
                    controller.entraNellaCampagna(nomeCampagnaSelezionata); //setta la campagna, se la trova, come campagna attiva
                    JFrame campagnaFrame = new JFrame("Regia Campagna: " + nomeCampagnaSelezionata);
                    CampagnaMasterGUI regiaGUI = new CampagnaMasterGUI(controller, masterLoggato, controller.getCampagnaAttiva(), frameAttuale);
                    frameAttuale.dispose();
                    campagnaFrame.setContentPane(regiaGUI.getMainPanel());
                    campagnaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    campagnaFrame.setSize(1000, 700);
                    campagnaFrame.setLocationRelativeTo(null);
                    campagnaFrame.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
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

        for(Campagna campagna : controller.getListaCampagne().keySet()) {
            String stato = campagna.isIniziata() ? "In corso" : "Non iniziata";
            model.addRow(new Object[]{campagna.getNome(), campagna.getMaxGiocatori(), stato});
        }
        //model.addRow(new Object[]{"La Miniera Perduta", 5, "In Corso"});//riga finta finche non implementato DAO
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

    /**
     * Controlla se l'utente loggato è master della campagna passata come parametro.
     * @param campagna la {@link Campagna} da controllare
     * @return {@code true} se è il master della campagna, altrimenti {@code false}.
     */
    private boolean controllaPrivilegiMaster(Campagna campagna){
            return masterLoggato.equals((controller.getListaCampagne().get(campagna)));
    }
}
