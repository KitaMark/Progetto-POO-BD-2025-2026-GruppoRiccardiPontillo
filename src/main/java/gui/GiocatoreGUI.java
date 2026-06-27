package gui;

import controller.Controller;
import model.Campagna;
import model.Giocatore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica principale (Dashboard) riservata agli utenti
 * con ruolo di {@link Giocatore}.
 * <p>
 * Mostra all'utente l'elenco delle campagne a cui partecipa e gli permette di
 * iscriversi a nuove avventure. Implementa inoltre una logica di smistamento:
 * quando un giocatore accede a una campagna, la schermata valuta se egli possiede
 * già un personaggio. In caso negativo, lo reindirizza alla finestra di creazione
 * del PG; in caso positivo, apre la scheda di gioco vera e propria.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class GiocatoreGUI {
    // Le tue variabili esatte dell'UI Designer
    private JLabel benvenutoGiocatore;
    private JButton LogoutButton;
    private JTable tableCampagnaGiocatore;
    private JButton entraButton;
    private JButton iscrivitiButton;
    private JPanel bottoniPanel;
    private JPanel mainPanel;

    /** Il Controller di riferimento per delegare le operazioni di business. */
    private Controller controller;

    /**
     * Costruisce l'interfaccia della Dashboard del Giocatore, inizializza la tabella
     * delle campagne e configura gli ascoltatori di eventi per l'interazione.
     *
     * @param controller Il {@link Controller} che orchestra le chiamate di sistema.
     */
    public GiocatoreGUI(Controller controller) {
        this.controller = controller;
        JFrame frame = new JFrame("Dashboard Giocatore - " + controller.getUtenteAttivo().getUsername());
        frame.setContentPane(getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Impostiamo una dimensione pulita e leggibile
        frame.setLocationRelativeTo(null); // Centra la finestra nello schermo
        frame.setVisible(true);

        benvenutoGiocatore.setText("Benvenuto, " + controller.getUtenteAttivo().getUsername() + "! [Giocatore]");

        inizializzaTabella();

        LogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frame,
                        "Vuoi davvero effettuare il logout?", "Conferma Uscita",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    controller.logout();
                    frame.dispose();
                    Home.main(null);
                }
            }
        });

        iscrivitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeCampagna = JOptionPane.showInputDialog(frame, "Inserisci il nome della Campagna a cui vuoi unirti:");

                if (nomeCampagna != null && !nomeCampagna.trim().isEmpty()) {
                    try {
                        controller.iscrivitiCampagna(nomeCampagna);
                        JOptionPane.showMessageDialog(frame, "Ti sei iscritto con successo alla campagna!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                        inizializzaTabella();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore Iscrizione", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        entraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagnaGiocatore.getSelectedRow();

                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona una campagna per entrarvi.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeCampagnaSelezionata = tableCampagnaGiocatore.getValueAt(rigaSelezionata, 0).toString();

                try {
                    boolean haGiaIlPersonaggio = controller.visualizzaCampagna(nomeCampagnaSelezionata);
                    Giocatore giocatore = (Giocatore) controller.getUtenteAttivo();

                    frame.dispose();

                    if (!haGiaIlPersonaggio) {
                        JFrame creaPgFrame = new JFrame("Creazione Personaggio - Campagna: " + nomeCampagnaSelezionata);
                        CreaPgGUI creaPgGUI = new CreaPgGUI(controller, giocatore, nomeCampagnaSelezionata, creaPgFrame);
                        creaPgFrame.setContentPane(creaPgGUI.getMainPanel());
                        creaPgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        creaPgFrame.setSize(600, 450);
                        creaPgFrame.setLocationRelativeTo(null);
                        creaPgFrame.setVisible(true);
                    } else {
                        JFrame campagnaFrame = new JFrame("Scheda Personaggio - Campagna: " + nomeCampagnaSelezionata);
                        CampagnaGiocatoreGUI campagnaGUI = new CampagnaGiocatoreGUI(controller, giocatore, nomeCampagnaSelezionata, campagnaFrame);
                        campagnaFrame.setContentPane(campagnaGUI.getMainPanel());
                        campagnaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        campagnaFrame.setSize(900, 600);
                        campagnaFrame.setLocationRelativeTo(null);
                        campagnaFrame.setVisible(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Errore nel caricamento: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    /**
     * Metodo  privato che definisce l'intestazione della JTable e ne inibisce
     * la modifica diretta delle celle tramite doppio clic.
     */
    private void inizializzaTabella() {
        String[] colonne = {"Nome Campagna", "Max Giocatori", "Stato"};

        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCampagnaGiocatore.setModel(model);
        tableCampagnaGiocatore.getTableHeader().setReorderingAllowed(false);
        tableCampagnaGiocatore.getTableHeader().setResizingAllowed(false);

        Giocatore giocatore = (Giocatore) controller.getUtenteAttivo();

        if (giocatore.getListaPartecipazioni() != null) {
            for (Campagna campagna : giocatore.getListaPartecipazioni().keySet()) {
                String stato = campagna.isIniziata() ? "In Corso" : "Non Iniziata";
                model.addRow(new Object[]{
                        campagna.getNome(),
                        campagna.getMaxGiocatori(),
                        stato
                });
            }
        }
    }

    /**
     * Restituisce il pannello contenitore principale della Dashboard.
     *
     * @return Il {@link JPanel} utilizzato dal frame per visualizzare i contenuti.
     */
     public JPanel getMainPanel() {
        return mainPanel;
    }
      }