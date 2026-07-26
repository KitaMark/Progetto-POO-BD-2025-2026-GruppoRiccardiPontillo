package gui;

import controller.Controller;
import exception.AbilitaGiaAppresaException;
import exception.AbilitaNonSbloccabileException;
import exception.AbilitaNonSelezionataException;
import exception.PersonaggioNonTrovatoException;
import model.Abilita;
import model.Giocatore;
import model.Master;
import model.Personaggio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
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
    private JButton rimuoviPartecipantiButton;
    private JPanel impostazioniButtonPanel;
    private JPanel impostazioniPanel;
    private JButton editorRazzeButton;
    private JButton editorClassiButton;
    private JButton catalogoOggettiButton;
    private JButton visualizzaDettagliPGButton;
    private JButton visualizzaDettagliPngButton;
    private JButton modificaButton;

    // I due nuovi bottoni aggiunti nel pannello a griglia
    private JButton abilitaButton;
    private JButton oroButton;

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
     * @param frameChiamante Il {@link JFrame} della finestra precedente.
     */
    public CampagnaMasterGUI(Controller controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frameChiamante = frameChiamante;

        JFrame frame = new JFrame(controller.getCampagnaAttiva().getNome());
        frame.setContentPane(getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);

        this.nomeCampagna.setText("Campagna: " + controller.getCampagnaAttiva().getNome());
        String stato = controller.getCampagnaAttiva().isIniziata() ? "Stato: In corso" : "Stato: Non iniziata";
        this.statoCampagna.setText(stato);
        String testoStato = controller.getCampagnaAttiva().isIniziata() ? "Concludi" : "Inizia campagna";
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

        rimuoviPartecipantiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = partecipantiTable.getSelectedRow();
                if(riga == -1){
                    JOptionPane.showMessageDialog(frame, "Seleziona un Giocatore da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                }
                String nomeGiocatore = (String)partecipantiTable.getValueAt(riga, 0);
                int idGiocatore = (int)partecipantiTable.getModel().getValueAt(riga, 2);

                try{
                    controller.rimuoviGiocatoreDaCampagna(idGiocatore);
                    JOptionPane.showMessageDialog(frame,  "Il giocatore " + nomeGiocatore + " è stato rimosso.");
                    inizializzaTabellaPG();
                    inizializzaTabellaPartecipanti();
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
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

                int conferma = JOptionPane.showConfirmDialog(frame, "Vuoi davvero rimuovere " + nomePg + "?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.rimuoviPGdaCampagna(nomePg, proprietarioPg);
                        inizializzaTabellaPG();
                        JOptionPane.showMessageDialog(frame, "Personaggio rimosso con successo.");
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
                int pgId = (Integer) pgTable.getModel().getValueAt(riga, 4);

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
                int idPg = (int)pgTable.getModel().getValueAt(riga, 4);
                String input = JOptionPane.showInputDialog(frame, "Quanti punti vuoi assegnare a " + nomePg + "?");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int punti = Integer.parseInt(input);
                        controller.assegnaPuntiStatistica(idPg, true, punti);
                        JOptionPane.showMessageDialog(frame, "Punti assegnati con successo!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Inserisci un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        oroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameChiamante, "Seleziona un personaggio dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomePg = pgTable.getValueAt(riga, 0).toString();
                int idPg = (int) pgTable.getModel().getValueAt(riga, 4);

                Object[] opzioni = {"Assegna Oro", "Sottrai Oro"};
                int scelta = JOptionPane.showOptionDialog(frameChiamante,
                        "Cosa vuoi fare con i fondi di " + nomePg + "?",
                        "Gestione Oro",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opzioni,
                        opzioni[0]);

                if (scelta == JOptionPane.CLOSED_OPTION) return;

                boolean isAssegna = (scelta == JOptionPane.YES_OPTION);
                String azioneText = isAssegna ? "aggiungere" : "sottrarre";

                String input = JOptionPane.showInputDialog(frameChiamante, "Quanto oro vuoi " + azioneText + "?\n(Inserisci un valore numerico)");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int quantitaOro = Integer.parseInt(input);

                        if (isAssegna) {
                            controller.assegnaOroMaster(idPg, quantitaOro);
                            JOptionPane.showMessageDialog(frameChiamante, "Hai assegnato " + quantitaOro + " monete d'oro a " + nomePg + ".", "Borsa aggiornata", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            controller.sottraiOroMaster(idPg, quantitaOro);
                            JOptionPane.showMessageDialog(frameChiamante, "Hai sottratto " + quantitaOro + " monete d'oro a " + nomePg + ".", "Borsa aggiornata", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frameChiamante, "Inserisci un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameChiamante, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        abilitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameChiamante, "Seleziona un personaggio.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int idPg = (int) pgTable.getModel().getValueAt(riga, 4);

                try {
                    Personaggio pg = controller.cercaPersonaggio(idPg, true);

                    controller.caricaAbilitaSbloccabiliPerClasse(pg.getClasse());

                    Object[] opzioniAbilita = new Object[pg.getClasse().getAbilitaSbloccabili().size()];
                    for (int i = 0; i < pg.getClasse().getAbilitaSbloccabili().size(); i++) {
                        opzioniAbilita[i] = pg.getClasse().getAbilitaSbloccabili().get(i).getNome();
                    }

                    if (opzioniAbilita.length == 0) {
                        JOptionPane.showMessageDialog(frameChiamante, "Nessuna abilità trovata per questa classe.", "Avviso", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    String scelta = (String) JOptionPane.showInputDialog(
                            frameChiamante,
                            "Seleziona un'abilità:",
                            "Assegna Abilità",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opzioniAbilita,
                            opzioniAbilita[0]
                    );

                    if (scelta != null) {
                        controller.assegnaAbilitaMaster(idPg, scelta);
                        JOptionPane.showMessageDialog(frameChiamante, "Abilità appresa con successo!");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameChiamante, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        creaPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreaPngGUI creaPngGUI = new CreaPngGUI(controller, (Master) controller.getUtenteAttivo(), frame);
                inizializzaTabellaPnG();
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
                int idPng = (int) pngTable.getModel().getValueAt(riga, 3);
                try {
                    controller.rimuoviPnG(idPng);
                    JOptionPane.showMessageDialog(frame, "PnG rimosso con successo.");
                    inizializzaTabellaPnG();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        visualizzaDettagliPngButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pngTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Nessun png selezionato.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int id = (int) pngTable.getModel().getValueAt(riga, 3);
                try {
                    Personaggio daVisualizzare = controller.cercaPersonaggio(id, false);
                    controller.leggiInventarioPersonaggio(daVisualizzare);
                    controller.leggiAbilitaPersonaggio(daVisualizzare);
                    SchedaPersonaggioGUI schedapg = new SchedaPersonaggioGUI(frame, controller, false, daVisualizzare);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        statoCampagnaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.cambiaStatoCampagna();
                    String nuovoStato = controller.getCampagnaAttiva().isIniziata() ? "In corso" : "Non iniziata";
                    statoCampagna.setText("Stato: " + nuovoStato);
                    String testoStato = controller.getCampagnaAttiva().isIniziata() ? "Concludi" : "Inizia campagna";
                    statoCampagnaButton.setText(testoStato);
                    JOptionPane.showMessageDialog(frame, "Stato aggiornato: " + nuovoStato);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        visualizzaDettagliPGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pgTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Nessun personaggio selezionato.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int id = (int) pgTable.getModel().getValueAt(riga, 4);
                try {
                    Personaggio daVisualizzare = controller.cercaPersonaggio(id, true);
                    controller.leggiInventarioPersonaggio(daVisualizzare);
                    controller.leggiAbilitaPersonaggio(daVisualizzare);
                    SchedaPersonaggioGUI schedapg = new SchedaPersonaggioGUI(frame, controller, true, daVisualizzare);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = pngTable.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona un PnG dalla tabella prima di cliccare Modifica.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomePng = pngTable.getValueAt(riga, 0).toString();

                int pngId = (Integer) pngTable.getModel().getValueAt(riga, 3);

                new ModificaStatisticheGUI(controller, nomePng, pngId, false, frame);
            }
        });



        editorRazzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditorRazzeGui(controller, frame);
            }
        });

        editorClassiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditorClassiGui(controller, frame);
            }
        });

        catalogoOggettiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditorOggettiGui(controller, frame);
            }
        });

    }

    /**
     * Metodo privato che definisce l'intestazione e i modelli dati per le tabelle
     * dei Personaggi Giocanti (PG) e dei Personaggi Non Giocanti (PnG), inibendone la modifica manuale.
     */
    private void inizializzaTabellaPG() {
        String[] colonnePG = {"Nome", "Giocatore", "Razza", "Classe", "ID"};
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
            String nomeProprietario = "Sconosciuto";
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