package gui;

import controller.Controller;
import model.Giocatore;
import model.Master;
import model.Personaggio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
    private JButton modificaStatisticheButton;
    private JButton rimuoviPgButton;
    private JTable pngTable;
    private JPanel panelPgButton;
    private JPanel panelPngButton;
    private JButton rimuoviPngButton;
    private JButton creaPngButton;
    private JPanel PG;
    private JPanel PNG;
    private JPanel impostazioniCampagna;
    private JButton statoCampagnaButton;
    private JScrollPane pgScrollPane;
    private JScrollPane pngScrollPane;
    private JPanel partecipantiPanel;
    private JScrollPane partecipantiScrollPane;
    private JTable partecipantiTable;
    private JPanel partecipantiButtonPanel;
    private JButton rimuoviButton;
    private JPanel impostazioniButtonPanel;
    private JPanel impostazioniPanel;
    private JButton editorRazzeButton;
    private JButton editorClassiButton;
    private JButton catalogoOggettiButton;
    private JButton visualizzaDettagliPGButton;
    private JButton visualizzaDettagliButton1;

    /**
     * Il Controller di riferimento per orchestrare tutte le logiche di modifica e gestione della campagna.
     */
    private Controller controller;
    /**
     * Il riferimento al {@link JFrame} chiamante per poter tornare alla schermata precedente.
     */
    private JFrame frameChiamante;



    /**
     * Costruisce l'interfaccia di Regia del Master, inizializzando le etichette di stato,
     * le tabelle dei personaggi e attivando tutti i Listener per i privilegi amministrativi.
     *
     * @param controller     Il {@link Controller} di sistema.
     */
    public CampagnaMasterGUI(Controller controller, JFrame frameChiamante) {
        this.controller = controller;
        JFrame frame = new JFrame(controller.getCampagnaAttiva().getNome());
        frame.setContentPane(getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        this.nomeCampagna.setText("Campagna: " + controller.getCampagnaAttiva().getNome());
        String stato = controller.getCampagnaAttiva().isIniziata() ? "Stato: In corso" : "Stato: Non iniziata";
        this.statoCampagna.setText(stato);
        String testoStato = controller.getCampagnaAttiva().isIniziata()? "Concludi" : "Inizia campagna";
        statoCampagnaButton.setText(testoStato);

        inizializzaTabellaPG();
        inizializzaTabellaPnG();
        inizializzaTabellaPartecipanti();


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Chiude la gestione della campagna
                frameChiamante.setVisible(true);
            }
        });


        rimuoviPgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona un Personaggio da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePg = pgTable.getValueAt(riga, 0).toString();
                String proprietarioPg = pgTable.getValueAt(riga, 1).toString();

                int conferma = JOptionPane.showConfirmDialog(frame, "Rimuovere definitivamente " + nomePg + "?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.rimuoviPGdaCampagna(nomePg, proprietarioPg);
                        inizializzaTabellaPG();
                        JOptionPane.showMessageDialog(frame, "Hai rimosso "+nomePg+".");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        modificaStatisticheButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona un Personaggio dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomePg = pgTable.getValueAt(riga, 0).toString();
                int pgId = (Integer)pgTable.getModel().getValueAt(riga, 4);

                //non rendiamo il frame invisibile, poiché la gui chiamata si comporta come un popup.
                ModificaStatisticheGUI modificaGUI = new ModificaStatisticheGUI(controller, nomePg, pgId, true, frame);

            }
        });


        assegnaPuntiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona un personaggio.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomePg = pgTable.getValueAt(riga, 0).toString();
                String nomeProprietario = pgTable.getValueAt(riga, 1).toString(); //identifica il pg tramite il suo giocatore
                String input = JOptionPane.showInputDialog(frame, "Quanti punti vuoi assegnare a " + nomePg + "?");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int punti = Integer.parseInt(input);
                        controller.assegnaPuntiStatistica(nomePg, nomeProprietario, punti);
                        JOptionPane.showMessageDialog(frame, "Punti assegnati con successo!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Inserisci un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        creaPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaPngGUI creaPngGUI = new CreaPngGUI(controller, (Master)controller.getUtenteAttivo(), frame);
                inizializzaTabellaPnG(); //aggiorna
            }
        });


        rimuoviPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pngTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona un PnG da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idPng = (int)pngTable.getModel().getValueAt(riga, 3);
                try {
                    controller.rimuoviPnG(idPng);
                    JOptionPane.showMessageDialog(frame, "PnG rimosso con successo.");
                    inizializzaTabellaPnG();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        statoCampagnaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] opzioni = {"In Corso", "Conclusa"};
                int scelta = JOptionPane.showOptionDialog(frame,
                        "Scegli il nuovo stato della campagna:", "Cambia Stato",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opzioni, opzioni[0]);

                //fa il cambio stato campagna solo se il master ha premuto il pulsante
                if (scelta != -1) {
                    try {
                        controller.cambiaStatoCampagna(controller.getCampagnaAttiva().getNome(), opzioni[scelta]);
                        statoCampagna.setText("Stato: " + opzioni[scelta]); // Aggiorna l'etichetta in alto!
                        JOptionPane.showMessageDialog(frame, "Stato aggiornato a: " + opzioni[scelta]);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }


    /**
     * Metodo privato che definisce l'intestazione e i modelli dati per le tabelle
     * dei Personaggi Giocanti (PG) e dei Personaggi Non Giocanti (PnG), inibendone la modifica manuale.
     */
    private void inizializzaTabellaPG() {
        // Tabella PG
        String[] colonnePG = {"Nome", "Giocatore", "Razza", "Classe", "ID"}; //eliminato livello perchè non l'abbiamo piu inserito in Personaggio
        DefaultTableModel modelPG = new DefaultTableModel(null, colonnePG) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pgTable.setModel(modelPG);
        pgTable.getTableHeader().setReorderingAllowed(false);
        pgTable.getTableHeader().setResizingAllowed(false);
        TableColumn colonna = pgTable.getColumnModel().getColumn(4);
        pgTable.getColumnModel().removeColumn(colonna);
        if (controller.getCampagnaAttiva().getListaPG().isEmpty()) return;
        for (Personaggio pg : controller.getCampagnaAttiva().getListaPG()) {
            String nomeProprietario = "Sconosciuto"; //valore di sicurezza per evitare problemi
            for (Giocatore giocatore : controller.getCampagnaAttiva().getPartecipanti()) {
                if (giocatore.getListaPartecipazioni().containsValue(pg)) {
                    nomeProprietario = giocatore.getUsername();
                    break;
                }
            }
            modelPG.addRow(new Object[]{pg.getNome(), nomeProprietario,
                    pg.getRazza().getNome(), pg.getClasse().getNome(), pg.getId()});
        }
    }

    private void inizializzaTabellaPnG() {
        // Tabella PnG
        String[] colonnePnG = {"Nome", "Razza", "Classe", "ID"};
        DefaultTableModel modelPnG = new DefaultTableModel(null, colonnePnG) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pngTable.setModel(modelPnG);
        pngTable.getTableHeader().setReorderingAllowed(false);
        pngTable.getTableHeader().setResizingAllowed(false);
        TableColumn colonna = pngTable.getColumnModel().getColumn(3);
        pngTable.getColumnModel().removeColumn(colonna);
        for(Personaggio png : controller.getCampagnaAttiva().getListaPnG()){
            modelPnG.addRow(new Object[]{png.getNome(), png.getRazza(), png.getClasse(), png.getId()});
        }
    }

    private void inizializzaTabellaPartecipanti(){
        String[] colonne = {"Username", "Email", "ID"};
        DefaultTableModel modelPartecipanti = new DefaultTableModel(null, colonne){
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };
        partecipantiTable.setModel(modelPartecipanti);
        partecipantiTable.getTableHeader().setReorderingAllowed(false);
        partecipantiTable.getTableHeader().setResizingAllowed(false);
        TableColumn colonna = partecipantiTable.getColumnModel().getColumn(2);
        partecipantiTable.getColumnModel().removeColumn(colonna);
        for(Giocatore giocatore : controller.getCampagnaAttiva().getPartecipanti()){
            modelPartecipanti.addRow(new Object[]{giocatore.getUsername(), giocatore.getEmail(), giocatore.getId()});
        }
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