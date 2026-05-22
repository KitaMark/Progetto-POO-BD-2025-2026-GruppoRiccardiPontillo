package gui;

import controller.Controller;
import model.Giocatore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CampagnaGiocatoreGUI {
    // Variabili generate dal tuo UI Designer
    private JButton tornaAllaSchermataPrecedenteButton;
    private JPanel mainPanel;
    private JPanel indietroButton;
    private JTabbedPane tabbedPane1;
    private JTable statisticheTable;
    private JPanel buttonPanel;
    private JButton aumentaStatButton;
    private JTable inventarioTable;
    private JPanel buttonPanel1;
    private JButton acquistaButton;

    private Controller controller;
    private Giocatore giocatoreLoggato;
    private String nomeCampagnaAttuale;
    private JFrame frameAttuale;

    public CampagnaGiocatoreGUI(Controller controller, Giocatore giocatore, String nomeCampagna, JFrame frame) {
        this.controller = controller;
        this.giocatoreLoggato = giocatore;
        this.nomeCampagnaAttuale = nomeCampagna;
        this.frameAttuale = frame;

        inizializzaTabelle();


        tornaAllaSchermataPrecedenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose(); // Chiude questa scheda

                JFrame giocatoreFrame = new JFrame("Dashboard Giocatore - " + giocatoreLoggato.getUsername());
                GiocatoreGUI giocatoreGUI = new GiocatoreGUI(controller, giocatoreLoggato, giocatoreFrame);

                giocatoreFrame.setContentPane(giocatoreGUI.getMainPanel());
                giocatoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                giocatoreFrame.setSize(800, 600);
                giocatoreFrame.setLocationRelativeTo(null);
                giocatoreFrame.setVisible(true);
            }
        });


        aumentaStatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = statisticheTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona una statistica dalla tabella per aumentarla.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeStatistica = statisticheTable.getValueAt(riga, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frameAttuale,
                        "Vuoi spendere 1 Punto per aumentare " + nomeStatistica + "?",
                        "Conferma", JOptionPane.YES_NO_OPTION);

                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.aumentaStatistica(nomeStatistica);
                        JOptionPane.showMessageDialog(frameAttuale, "Statistica potenziata con successo!");
                        // In futuro: aggiorneremo il valore numerico nella riga della tabella
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        acquistaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = inventarioTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un oggetto per comprarlo.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeOggetto = inventarioTable.getValueAt(riga, 0).toString();

                try {
                    controller.compraOggetto(nomeOggetto);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai acquistato: " + nomeOggetto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    private void inizializzaTabelle() {
        // --- Tabella Statistiche ---
        String[] colonneStat = {"Statistica", "Valore Attuale"};
        DefaultTableModel modelStat = new DefaultTableModel(null, colonneStat) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Sola lettura
        };
        statisticheTable.setModel(modelStat);

        // Dati finti provvisori
        modelStat.addRow(new Object[]{"Forza", 15});
        modelStat.addRow(new Object[]{"Destrezza", 12});

        // --- Tabella Inventario ---
        String[] colonneInv = {"Oggetto", "Tipo", "Costo (Oro)"};
        DefaultTableModel modelInv = new DefaultTableModel(null, colonneInv) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Sola lettura
        };
        inventarioTable.setModel(modelInv);

        // Dati finti provvisori
        modelInv.addRow(new Object[]{"Spada Lunga", "Arma", 15});
        modelInv.addRow(new Object[]{"Pozione di Cura", "Consumabile", 5});
    }

    // Fondamentale per far caricare l'interfaccia nel JFrame
    public JPanel getMainPanel() {
        return mainPanel;
    }
}