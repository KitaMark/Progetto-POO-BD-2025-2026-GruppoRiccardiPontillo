package gui;

import controller.Controller;
import model.Campagna;
import model.Master;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta il pannello di controllo dedicato al Master
 * per la gestione attiva di una singola campagna.
 * <p>
 * Traduce visivamente tutti i privilegi amministrativi concessi al Master dal dominio di gioco.
 * Tramite questa interfaccia, il Master può visualizzare i partecipanti, espellere i Personaggi
 * Giocanti (PG), alterare arbitrariamente le loro statistiche per necessità narrative, assegnare
 * punti crescita, generare nuovi Personaggi Non Giocanti (PnG) e modificare lo stato operativo
 * della campagna.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class CampagnaMasterGUI {

    private JPanel mainPanel;
    private JLabel nomeCampagna;
    private JLabel statoCampagna;
    private JButton indietroButton;
    private JTabbedPane tabbedPane1;
    private JTable pgTable;
    private JButton assegnaPuntiButton;
    private JButton modificaStatisticaButton;
    private JButton rimuovoPgButton;
    private JTable pngTable;
    private JPanel panelPgButton;
    private JPanel panelPngButton;
    private JButton rimuoviPngButton;
    private JButton creaPngButton;
    private JPanel PG;
    private JPanel PNG;
    private JPanel impostazioniCampagna;
    private JButton statoCampagnaButton;

    /** Il Controller di riferimento per orchestrare tutte le logiche di modifica e gestione della campagna. */
    private Controller controller;
    /** Il Master attualmente autenticato che esercita la regia su questa campagna. */
    private Master masterLoggato;
    /** La Campagna su cui effettuare le elaborazioni. */
    private Campagna campagnaAttiva;
    /** Il nome univoco della campagna attualmente in corso di gestione. */
    private String nomeCampagnaAttuale;
    /** Riferimento al frame attuale */
    private JFrame frameHome;


    /**
     * Costruisce l'interfaccia di Regia del Master, inizializzando le etichette di stato,
     * le tabelle dei personaggi e attivando tutti i Listener per i privilegi amministrativi.
     *
     * @param controller   Il {@link Controller} di sistema.
     * @param master       L'oggetto {@link Master} autenticato.
     * @param campagnaAttiva La {@link Campagna} da gestire.
     * @param frame        Il {@link JFrame} principale che contiene questo pannello.
     */
    public CampagnaMasterGUI(Controller controller, Master master, Campagna campagnaAttiva, JFrame frame) {
        this.controller = controller;
        this.masterLoggato = master;
        this.campagnaAttiva = campagnaAttiva;
        this.frameHome = frame;
        this.nomeCampagna.setText("Campagna: " + nomeCampagnaAttuale);
        this.statoCampagna.setText("Stato: Non Iniziata"); // In futuro lo leggeremo dal DB

        inizializzaTabelle();


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameHome.dispose(); // Chiude la gestione della campagna

                // Riapre la Dashboard del Master
                JFrame masterFrame = new JFrame("Dashboard Master - " + masterLoggato.getUsername());
                MasterGUI masterGUI = new MasterGUI(controller, masterLoggato, masterFrame);
                masterFrame.setContentPane(masterGUI.getMainPanel());
                masterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                masterFrame.setSize(800, 600);
                masterFrame.setLocationRelativeTo(null);
                masterFrame.setVisible(true);
            }
        });



        rimuovoPgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameHome, "Seleziona un Personaggio da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePg = pgTable.getValueAt(riga, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frameHome, "Rimuovere definitivamente " + nomePg + "?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.rimuoviPGdaCampagna(nomePg);
                        JOptionPane.showMessageDialog(frameHome, "Personaggio rimosso.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameHome, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });



        modificaStatisticaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameHome, "Seleziona un Personaggio dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomePg = pgTable.getValueAt(riga, 0).toString();

                try {
                    JFrame modificaFrame = new JFrame("Modifica Statistica - " + nomePg);
                    ModificaStatisticheGUI modificaGUI = new ModificaStatisticheGUI(controller, nomePg, modificaFrame);

                    modificaFrame.setContentPane(modificaGUI.getMainPanel());
                    // Usiamo DISPOSE_ON_CLOSE per chiudere solo questo popup e non tutto
                    modificaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    modificaFrame.setSize(400, 500);
                    modificaFrame.setLocationRelativeTo(frameHome);
                    modificaFrame.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameHome, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        assegnaPuntiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameHome, "Seleziona un Personaggio.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePg = pgTable.getValueAt(riga, 0).toString();
                String input = JOptionPane.showInputDialog(frameHome, "Quanti punti vuoi assegnare a " + nomePg + "?");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int punti = Integer.parseInt(input);
                        controller.assegnaPuntiStatistica(nomePg, punti);
                        JOptionPane.showMessageDialog(frameHome, "Punti assegnati con successo!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frameHome, "Inserisci un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameHome, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        creaPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame creaPngFrame = new JFrame("Nuovo PNG - Campagna: " + nomeCampagnaAttuale);
                CreaPngGUI creaPngGUI = new CreaPngGUI(controller, masterLoggato, nomeCampagnaAttuale, creaPngFrame);

                creaPngFrame.setContentPane(creaPngGUI.getMainPanel());
                // uso DISPOSE così se il Master chiude questo popup non si chiude l'intero gioco!
                creaPngFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                creaPngFrame.setSize(600, 500);
                creaPngFrame.setLocationRelativeTo(frameHome);
                creaPngFrame.setVisible(true);
            }
        });


        rimuoviPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pngTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameHome, "Seleziona un PnG da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePng = pngTable.getValueAt(riga, 0).toString();
                try {
                    controller.rimuoviPnG(nomePng);
                    JOptionPane.showMessageDialog(frameHome, "PnG rimosso con successo.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameHome, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        statoCampagnaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] opzioni = {"In Corso", "Conclusa"};
                int scelta = JOptionPane.showOptionDialog(frameHome,
                        "Scegli il nuovo stato della campagna:", "Cambia Stato",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opzioni, opzioni[0]);

                //fa il cambio stato campagna solo se il master ha premuto il pulsante
                if (scelta != -1) {
                    try {
                        controller.cambiaStatoCampagna(nomeCampagnaAttuale, opzioni[scelta]);
                        statoCampagna.setText("Stato: " + opzioni[scelta]); // Aggiorna l'etichetta in alto!
                        JOptionPane.showMessageDialog(frameHome, "Stato aggiornato a: " + opzioni[scelta]);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameHome, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }


    /**
     * Metodo privato che definisce l'intestazione e i modelli dati per le tabelle
     * dei Personaggi Giocanti (PG) e dei Personaggi Non Giocanti (PnG), inibendone la modifica manuale.
     */
    private void inizializzaTabelle() {
        // Tabella PG
        String[] colonnePG = {"Nome PG", "Giocatore", "Razza", "Classe", "Livello"};
        DefaultTableModel modelPG = new DefaultTableModel(null, colonnePG) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pgTable.setModel(modelPG);
        pgTable.getTableHeader().setReorderingAllowed(false);
        pgTable.getTableHeader().setResizingAllowed(false);
        modelPG.addRow(new Object[]{"Legolas", "Player1", "Elfo", "Cacciatore", 3});

        // Tabella PnG
        String[] colonnePnG = {"Nome PnG", "Ruolo/Razza", "Livello"};
        DefaultTableModel modelPnG = new DefaultTableModel(null, colonnePnG) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pngTable.setModel(modelPnG);
        pngTable.getTableHeader().setReorderingAllowed(false);
        pngTable.getTableHeader().setResizingAllowed(false);
        modelPnG.addRow(new Object[]{"Oste Bob", "Umano", 1});
    }


    /**
     * Restituisce il pannello principale dell'interfaccia di regia.
     *
     * @return Il {@link JPanel} utilizzato per il rendering visivo dei contenuti.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}