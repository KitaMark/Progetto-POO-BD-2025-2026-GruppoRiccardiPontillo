package gui;

import controller.Controller;
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
    private JPanel mainPanel; // Aggiungi questo nome al pannello principale (lo sfondo di tutto) nel Designer!

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
        frame.pack();
        frame.setVisible(true); // Rende visibile la finestra



        benvenutoGiocatore.setText("Benvenuto, "+controller.getUtenteAttivo().getUsername()+"! [Giocatore]");

        // tabella campagna (segue un po la stessa logica per il master)
        inizializzaTabella();


        LogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frame,
                        "Vuoi davvero effettuare il logout?", "Conferma Uscita",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    controller.logout();
                    frame.dispose(); // Chiude la dashboard del giocatore
                    Home.main(null); // Riapre la schermata di login
                }
            }
        });

        iscrivitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeCampagna = JOptionPane.showInputDialog(frame,
                        "Inserisci il nome della Campagna a cui vuoi unirti:");

                // Controlliamo che l'utente non abbia premuto 'Annulla' o inserito testo vuoto
                if (nomeCampagna != null && !nomeCampagna.trim().isEmpty()) {
                    try {
                        controller.iscrivitiCampagna(nomeCampagna);
                        JOptionPane.showMessageDialog(frame,
                                "Ti sei iscritto con successo alla campagna!",
                                "Successo", JOptionPane.INFORMATION_MESSAGE);
                        // In futuro: aggiornare la tabella per far comparire la nuova iscrizione
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame,
                                ex.getMessage(), "Errore Iscrizione", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        entraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagnaGiocatore.getSelectedRow();

                // controllo per gestire caso in cui il giocatore dimentichi di selezionare la campagna in cui entrare
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona una campagna per entrarvi.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return; // Blocca tutto
                }

                String nomeCampagnaSelezionata = tableCampagnaGiocatore.getValueAt(rigaSelezionata, 0).toString();

                try {
                    controller.visualizzaCampagna(nomeCampagnaSelezionata);
                    frame.dispose(); // Chiude la dashboard in ogni caso


                    // CONTROLLO PERSONAGGIO (Simulazione)
                    // Imposta a 'false' per testare la form di creazione del PG.
                    // Imposta a 'true' per testare la scheda del PG (CampagnaGiocatoreGUI).
                    boolean haGiaIlPersonaggio = false;

                    if (!haGiaIlPersonaggio) {
                        // CASO 1: Il personaggio non esiste -> Apriamo CreaPgGUI
                        JFrame creaPgFrame = new JFrame("Creazione Personaggio - Campagna: " + nomeCampagnaSelezionata);
                        CreaPgGUI creaPgGUI = new CreaPgGUI(controller, (Giocatore)controller.getUtenteAttivo(), nomeCampagnaSelezionata, creaPgFrame);

                        creaPgFrame.setContentPane(creaPgGUI.getMainPanel());
                        creaPgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        creaPgFrame.setSize(600, 450);
                        creaPgFrame.setLocationRelativeTo(null); // Centra lo schermo
                        creaPgFrame.setVisible(true);

                    } else {
                        // CASO 2: Il personaggio esiste già -> Va alla scheda di gioco
                        JFrame campagnaFrame = new JFrame("Scheda Personaggio - Campagna: " + nomeCampagnaSelezionata);
                        CampagnaGiocatoreGUI campagnaGUI = new CampagnaGiocatoreGUI(controller, (Giocatore)controller.getUtenteAttivo(), nomeCampagnaSelezionata, campagnaFrame);

                        campagnaFrame.setContentPane(campagnaGUI.getMainPanel());
                        campagnaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        campagnaFrame.setSize(900, 600);
                        campagnaFrame.setLocationRelativeTo(null);
                        campagnaFrame.setVisible(true);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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

        // Usiamo un DefaultTableModel per non far modificare il testo all'utente con il doppio clic tramite metodo di Swing
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCampagnaGiocatore.setModel(model);
        tableCampagnaGiocatore.getTableHeader().setReorderingAllowed(false);
        tableCampagnaGiocatore.getTableHeader().setResizingAllowed(false);


        model.addRow(new Object[]{"La Miniera Perduta", 5, "In Corso"});//riga finta finche non implementato DAO
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