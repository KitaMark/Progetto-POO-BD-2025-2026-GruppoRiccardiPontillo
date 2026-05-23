package gui;

import controller.Controller;
import model.Master;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CampagnaMasterGUI {
    // Variabili generate dal tuo GUI Designer
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

    private Controller controller;
    private Master masterLoggato;
    private String nomeCampagnaAttuale;
    private JFrame frameAttuale;

    public CampagnaMasterGUI(Controller controller, Master master, String nomeCampagna, JFrame frame) {
        this.controller = controller;
        this.masterLoggato = master;
        this.nomeCampagnaAttuale = nomeCampagna;
        this.frameAttuale = frame;

        this.nomeCampagna.setText("Campagna: " + nomeCampagnaAttuale);
        this.statoCampagna.setText("Stato: Non Iniziata"); // In futuro lo leggeremo dal DB

        inizializzaTabelle();


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose(); // Chiude la gestione della campagna

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
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un Personaggio da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePg = pgTable.getValueAt(riga, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frameAttuale, "Rimuovere definitivamente " + nomePg + "?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.rimuoviPGdaCampagna(nomePg);
                        JOptionPane.showMessageDialog(frameAttuale, "Personaggio rimosso.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });



        modificaStatisticaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un Personaggio dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
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
                    modificaFrame.setLocationRelativeTo(frameAttuale);
                    modificaFrame.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        assegnaPuntiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un Personaggio.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePg = pgTable.getValueAt(riga, 0).toString();
                String input = JOptionPane.showInputDialog(frameAttuale, "Quanti punti vuoi assegnare a " + nomePg + "?");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int punti = Integer.parseInt(input);
                        controller.assegnaPuntiStatistica(nomePg, punti);
                        JOptionPane.showMessageDialog(frameAttuale, "Punti assegnati con successo!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frameAttuale, "Inserisci un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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
                creaPngFrame.setLocationRelativeTo(frameAttuale);
                creaPngFrame.setVisible(true);
            }
        });


        rimuoviPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pngTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un PnG da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePng = pngTable.getValueAt(riga, 0).toString();
                try {
                    controller.rimuoviPnG(nomePng);
                    JOptionPane.showMessageDialog(frameAttuale, "PnG rimosso con successo.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        statoCampagnaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] opzioni = {"In Corso", "Conclusa"};
                int scelta = JOptionPane.showOptionDialog(frameAttuale,
                        "Scegli il nuovo stato della campagna:", "Cambia Stato",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opzioni, opzioni[0]);

                //fa il cambio stato campagna solo se il master ha premuto il pulsante
                if (scelta != -1) {
                    try {
                        controller.cambiaStatoCampagna(nomeCampagnaAttuale, opzioni[scelta]);
                        statoCampagna.setText("Stato: " + opzioni[scelta]); // Aggiorna l'etichetta in alto!
                        JOptionPane.showMessageDialog(frameAttuale, "Stato aggiornato a: " + opzioni[scelta]);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void inizializzaTabelle() {
        // Tabella PG
        String[] colonnePG = {"Nome PG", "Giocatore", "Razza", "Classe", "Livello"};
        DefaultTableModel modelPG = new DefaultTableModel(null, colonnePG) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pgTable.setModel(modelPG);
        modelPG.addRow(new Object[]{"Legolas", "Player1", "Elfo", "Cacciatore", 3});

        // Tabella PnG
        String[] colonnePnG = {"Nome PnG", "Ruolo/Razza", "Livello"};
        DefaultTableModel modelPnG = new DefaultTableModel(null, colonnePnG) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pngTable.setModel(modelPnG);
        modelPnG.addRow(new Object[]{"Oste Bob", "Umano", 1});
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}