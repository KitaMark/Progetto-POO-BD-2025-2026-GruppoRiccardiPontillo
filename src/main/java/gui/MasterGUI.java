package gui;

import controller.Controller;
import model.Master;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MasterGUI {
    private JLabel benvenutoMaster;
    private JTable tableCampagna;
    private JButton logoutButton;
    private JButton eliminaButton;
    private JButton creaButton;
    private JButton entraButton;
    private JPanel mainPanel;

    //riferimenti  Controller, all'utente e alla finestra
    private Controller controller;
    private Master masterLoggato;
    private JFrame frameAttuale;

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
                if ((nomeCampagna != null) && (nomeCampagna.trim().isEmpty()==false)) {
                    try {
                        controller.creaCampagna(nomeCampagna, 5); // per ora
                        JOptionPane.showMessageDialog(frameAttuale, "Campagna creata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                        // ricarica table per farla comparire
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
                        controller.eliminaCampagna(nomeDaEliminare);
                        JOptionPane.showMessageDialog(frameAttuale, "Campagna eliminata con successo.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, "Errore durante l'eliminazione.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        entraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagna.getSelectedRow();

                //controllo per gestire caso in cui master dimentichi di selezionare la campagna in cui entrare
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona una campagna per entrarvi.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return; // Blocca tutto
                }

                String nomeCampagnaSelezionata = tableCampagna.getValueAt(rigaSelezionata, 0).toString();

                try {
                    controller.entraNellaCampagna(nomeCampagnaSelezionata);
                    JOptionPane.showMessageDialog(frameAttuale, "Ingresso in corso nella campagna: " + nomeCampagnaSelezionata, "Accesso Campagna", JOptionPane.INFORMATION_MESSAGE);
                    // In futuro: nasconderemo questa finestra e apriremo quella della campagna
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    //Metodo privato per dare un'intestazione alle colonne della JTable.
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


        model.addRow(new Object[]{"La Miniera Perduta", 5, "In Corso"});//riga finta finche non implementato DAO
    }

    // Fondamentale per farla visualizzare nel JFrame
    public JPanel getMainPanel() {
        return mainPanel;
    }
}
