package gui;

import controller.Controller;
import model.Giocatore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiocatoreGUI {
    // Le tue variabili esatte dell'UI Designer
    private JLabel benvenutoGiocatore;
    private JButton LogoutButton;
    private JTable tableCampagnaGiocatore;
    private JButton entraButton;
    private JButton iscrivitiButton;
    private JPanel bottoniPanel;
    private JPanel mainPanel; // Aggiungi questo nome al pannello principale (lo sfondo di tutto) nel Designer!

    private Controller controller;
    private Giocatore giocatoreLoggato;
    private JFrame frameAttuale;

    public GiocatoreGUI(Controller controller, Giocatore giocatore, JFrame frame) {
        this.controller = controller;
        this.giocatoreLoggato = giocatore;
        this.frameAttuale = frame;


        benvenutoGiocatore.setText("Benvenuto Giocatore, [" + giocatoreLoggato.getUsername() + "]");

        // tabella campagna (segue un po la stessa logica per il master)
        inizializzaTabella();


        LogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frameAttuale,
                        "Vuoi davvero effettuare il logout?", "Conferma Uscita",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    controller.logout();
                    frameAttuale.dispose(); // Chiude la dashboard del giocatore
                    Home.main(null); // Riapre la schermata di login
                }
            }
        });

        iscrivitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeCampagna = JOptionPane.showInputDialog(frameAttuale,
                        "Inserisci il nome della Campagna a cui vuoi unirti:");

                // Controlliamo che l'utente non abbia premuto 'Annulla' o inserito testo vuoto
                if (nomeCampagna != null && !nomeCampagna.trim().isEmpty()) {
                    try {
                        controller.iscrivitiCampagna(nomeCampagna);
                        JOptionPane.showMessageDialog(frameAttuale,
                                "Ti sei iscritto con successo alla campagna!",
                                "Successo", JOptionPane.INFORMATION_MESSAGE);
                        // In futuro: aggiornare la tabella per far comparire la nuova iscrizione
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale,
                                ex.getMessage(), "Errore Iscrizione", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        entraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagnaGiocatore.getSelectedRow();

                //controllo per gestire caso in cui master dimentichi di selezionare la campagna in cui entrare
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona una campagna per entrarvi.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return; // Blocca tutto
                }

                String nomeCampagnaSelezionata = tableCampagnaGiocatore.getValueAt(rigaSelezionata, 0).toString();

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

        tableCampagnaGiocatore.setModel(model);


        model.addRow(new Object[]{"La Miniera Perduta", 5, "In Corso"});//riga finta finche non implementato DAO
    }

    // Fondamentale per farla visualizzare nel JFrame
    public JPanel getMainPanel() {
        return mainPanel;
    }
}