package gui;

import controller.Controller;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica principale (Scheda Personaggio) dedicata al
 * {@link Giocatore} durante lo svolgimento di una campagna.
 * <p>
 * Mette a disposizione del giocatore tutti gli strumenti necessari per l'interazione
 * nel mondo di gioco: visualizzazione e potenziamento delle statistiche,
 * gestione dell'equipaggiamento, e l'accesso alla finestra del Negozio.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class CampagnaGiocatoreGUI {
    private JButton tornaAllaSchermataPrecedenteButton;
    private JPanel mainPanel;
    private JPanel indietroButton;
    private JTabbedPane tabbedPane1;
    private JTable statisticheTable;
    private JPanel buttonPanel;
    private JButton aumentaStatButton;
    private JLabel puntiLabel; // Aggiunta la nuova etichetta per i punti

    private JTable equipaggiamentoTable;
    private JButton rimuoviButton;
    private JButton equipaggiaButton;

    private JTable consumabiliTable;
    private JButton usaButton;

    private JTable abilitaTable;
    private JLabel CampagnanomeJlabel;

    private JButton negozioButton;

    private Controller controller;
    private Giocatore giocatoreLoggato;
    private String nomeCampagnaAttuale;
    private JFrame frameAttuale;

    /**
     * Costruisce la schermata principale della campagna per il giocatore.
     * Inizializza i componenti grafici, carica le tabelle con i dati attuali
     * e collega i listener ai vari bottoni di interazione.
     *
     * @param controller      L'istanza del controller di sistema.
     * @param giocatore       Il giocatore loggato che sta visualizzando la scheda.
     * @param nomeCampagna    Il nome della campagna attualmente in corso.
     * @param frameChiamante  Il frame precedente da cui si proviene (utile per la navigazione).
     */
    public CampagnaGiocatoreGUI(Controller controller, Giocatore giocatore, String nomeCampagna, JFrame frameChiamante) {
        this.controller = controller;
        this.giocatoreLoggato = giocatore;
        this.nomeCampagnaAttuale = nomeCampagna;

        this.frameAttuale = new JFrame("Scheda Personaggio - Campagna: " + nomeCampagna);
        this.frameAttuale.setContentPane(mainPanel);
        this.frameAttuale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CampagnanomeJlabel.setText("Campagna: " + nomeCampagnaAttuale);

        inizializzaTabelle();
        collegaListener();

        // pack() dimensiona la finestra in base al contenuto reale delle tabelle
        // (calcolato in configuraTabella), cosi' tutte le righe sono visibili senza scroll.
        this.frameAttuale.pack();
        this.frameAttuale.setMinimumSize(this.frameAttuale.getSize());
        this.frameAttuale.setLocationRelativeTo(null);
        this.frameAttuale.setVisible(true);
    }

    /**
     * Collega tutti i listener dei pulsanti della schermata.
     * Separato dal costruttore per tenere l'inizializzazione dei dati
     * e il collegamento degli eventi in due responsabilità distinte.
     */
    private void collegaListener() {
        negozioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NegozioGui negozio = new NegozioGui(controller, nomeCampagnaAttuale);

                negozio.getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        Personaggio pg = giocatoreLoggato.getPersonaggioInCampagna(controller.getCampagnaAttiva());
                        controller.leggiInventarioPersonaggio(pg);
                        inizializzaTabelle();
                        frameAttuale.pack();
                    }
                });
            }
        });

        tornaAllaSchermataPrecedenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose();
                new GiocatoreGUI(controller);
            }
        });

        aumentaStatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = statisticheTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona una statistica dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeStatistica = statisticheTable.getValueAt(riga, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frameAttuale, "Vuoi spendere 1 Punto per aumentare " + nomeStatistica + "?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.aumentaStatistica(nomeStatistica);
                        JOptionPane.showMessageDialog(frameAttuale, "Statistica potenziata con successo!");
                        inizializzaTabelle();
                        frameAttuale.pack();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        equipaggiaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = equipaggiamentoTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un'arma o armatura da equipaggiare.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = equipaggiamentoTable.getValueAt(riga, 0).toString();
                try {
                    controller.equipaggiaOggetto(nomeOggetto, nomeCampagnaAttuale);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai equipaggiato: " + nomeOggetto);
                    inizializzaTabelle();
                    frameAttuale.pack();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Requisiti Insufficienti", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        rimuoviButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = equipaggiamentoTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un oggetto da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = equipaggiamentoTable.getValueAt(riga, 0).toString();
                try {
                    controller.rimuoviEquipaggiamento(nomeOggetto, nomeCampagnaAttuale);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai rimosso: " + nomeOggetto);
                    inizializzaTabelle();
                    frameAttuale.pack();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        usaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = consumabiliTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona una pozione da usare.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = consumabiliTable.getValueAt(riga, 0).toString();
                try {
                    controller.usaConsumabile(nomeOggetto, nomeCampagnaAttuale);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai usato: " + nomeOggetto);
                    inizializzaTabelle();
                    frameAttuale.pack();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Svuota e ripopola tutti i modelli di tabella visibili nell'interfaccia
     * (Statistiche, Equipaggiamento, Consumabili, Abilità) in base allo stato
     * attuale del personaggio in memoria.
     */
    private void inizializzaTabelle() {
        Personaggio pg = giocatoreLoggato.getPersonaggioInCampagna(controller.getCampagnaAttiva());
        if (pg == null) return;

        // Aggiorna il testo dell'etichetta con i punti da spendere
        puntiLabel.setText("Punti da Spendere: " + pg.getPuntiStatistica());

        popolaTabellaStatistiche(pg);
        popolaTabellaEquipaggiamento(pg);
        popolaTabellaConsumabili(pg);
        popolaTabellaAbilita(pg);
    }

    private void popolaTabellaStatistiche(Personaggio pg) {
        String[] colonne = {"Statistica", "Valore Attuale"};
        DefaultTableModel model = creaModelloNonModificabile(colonne);

        // Rimossa la riga "Punti da spendere" dalla tabella
        model.addRow(new Object[]{"HP Correnti", pg.getHpCorrenti() + " / " + pg.getStatisticheFinali().getHpMax()});
        model.addRow(new Object[]{"Mana Corrente", pg.getManaCorrente() + " / " + pg.getStatisticheFinali().getManaMax()});
        model.addRow(new Object[]{"Costituzione", pg.getStatisticheFinali().getCostituzione()});
        model.addRow(new Object[]{"Forza", pg.getStatisticheFinali().getForza()});
        model.addRow(new Object[]{"Destrezza", pg.getStatisticheFinali().getDestrezza()});
        model.addRow(new Object[]{"Intelligenza", pg.getStatisticheFinali().getIntelligenza()});
        model.addRow(new Object[]{"Fede", pg.getStatisticheFinali().getFede()});
        model.addRow(new Object[]{"Carisma", pg.getStatisticheFinali().getCarisma()});
        model.addRow(new Object[]{"Fortuna", pg.getStatisticheFinali().getFortuna()});

        statisticheTable.setModel(model);
        configuraTabella(statisticheTable, new int[]{220, 160});
    }

    private void popolaTabellaEquipaggiamento(Personaggio pg) {
        String[] colonne = {"Nome Oggetto", "Bonus", "Equipaggiato"};
        DefaultTableModel model = creaModelloNonModificabile(colonne);

        for (OggettoEquipaggiabile oggetto : pg.getInventarioEquipaggiabili().keySet()) {
            boolean equipaggiato = pg.getInventarioEquipaggiabili().get(oggetto);
            String stato;
            if (equipaggiato) {
                stato = "Sì";
            } else {
                stato = "No";
            }
            model.addRow(new Object[]{oggetto.getNome(), formattaBonus(oggetto.getBonus()), stato});
        }

        equipaggiamentoTable.setModel(model);
        // Stringe la tabella equipaggiamento
        configuraTabella(equipaggiamentoTable, new int[]{200, 260, 90});
    }

    private void popolaTabellaConsumabili(Personaggio pg) {
        String[] colonne = {"Nome Oggetto", "Ripristina HP", "Ripristina Mana", "Quantità"};
        DefaultTableModel model = creaModelloNonModificabile(colonne);

        for (OggettoConsumabile oggetto : pg.getInventarioConsumabili().keySet()) {
            int quantita = pg.getInventarioConsumabili().get(oggetto);

            String hpText;
            if (oggetto.getRipristinoHP() == 0) {
                hpText = "-";
            } else {
                hpText = String.valueOf(oggetto.getRipristinoHP());
            }

            String manaText;
            if (oggetto.getRipristinoMana() == 0) {
                manaText = "-";
            } else {
                manaText = String.valueOf(oggetto.getRipristinoMana());
            }

            model.addRow(new Object[]{oggetto.getNome(), hpText, manaText, quantita});
        }

        consumabiliTable.setModel(model);
        // Stringe la tabella consumabili
        configuraTabella(consumabiliTable, new int[]{220, 110, 110, 80});
    }

    private void popolaTabellaAbilita(Personaggio pg) {
        String[] colonne = {"Nome Abilità", "Descrizione", "Appresa"};
        DefaultTableModel model = creaModelloNonModificabile(colonne);

        if (pg.getClasse().getAbilitaSbloccabili() != null) {
            for (Abilita abilita : pg.getClasse().getAbilitaSbloccabili()) {
                boolean appresa = pg.getListaAbilita().contains(abilita);
                String appresaText;
                if (appresa) {
                    appresaText = "Sì";
                } else {
                    appresaText = "No";
                }
                model.addRow(new Object[]{abilita.getNome(), abilita.getDescrizione(), appresaText});
            }
        }

        abilitaTable.setModel(model);
        configuraTabella(abilitaTable, new int[]{220, 420, 100});
    }

    /**
     * Crea un modello di tabella con colonne non modificabili dall'utente.
     */
    private DefaultTableModel creaModelloNonModificabile(String[] colonne) {
        return new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Configura la tabella in modo semplice, pulito e imposta l'altezza dello
     * JScrollPane che la contiene in base al numero di righe, in modo da
     * mostrare l'intera tabella senza necessità di scroll verticale.
     *
     * @param tabella             la tabella da configurare.
     * @param larghezzePreferite  le larghezze preferite per le colonne.
     */
    private void configuraTabella(JTable tabella, int[] larghezzePreferite) {
        tabella.getTableHeader().setReorderingAllowed(false);
        tabella.getTableHeader().setResizingAllowed(false);
        tabella.setRowHeight(26);
        tabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumnModel columnModel = tabella.getColumnModel();
        int larghezzaTotale = 0;
        for (int i = 0; i < larghezzePreferite.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(larghezzePreferite[i]);
            larghezzaTotale += larghezzePreferite[i];
        }

        int altezzaHeader = tabella.getTableHeader().getPreferredSize().height;
        int righeDaMostrare = Math.max(2, tabella.getRowCount());
        int altezzaRighe = righeDaMostrare * tabella.getRowHeight();

        int altezzaTotale = altezzaHeader + altezzaRighe + 20;

        java.awt.Dimension dimensioneEsatta = new java.awt.Dimension(larghezzaTotale, altezzaTotale);
        tabella.setPreferredScrollableViewportSize(dimensioneEsatta);

        java.awt.Container viewport = tabella.getParent();
        if (viewport instanceof JViewport) {
            java.awt.Container scrollPaneContainer = viewport.getParent();
            if (scrollPaneContainer instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) scrollPaneContainer;

                // Disabilita lo scroll per evitare la comparsa delle scrollbar
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                scrollPane.setPreferredSize(dimensioneEsatta);
                scrollPane.setMinimumSize(dimensioneEsatta);
            }
        }
    }

    /**
     * Prende in input un set di modificatori e restituisce una stringa user-friendly
     * pronta per essere stampata a schermo nella tabella equipaggiamento.
     *
     * @param b Le statistiche bonus conferite dall'oggetto.
     * @return Una stringa compatta (es: "+2 For +1 Des"), oppure "-" se l'oggetto non fornisce bonus.
     */
    private String formattaBonus(Statistica b) {
        StringBuilder bonus = new StringBuilder();
        if (b.getHpMax() != 0) bonus.append("+").append(b.getHpMax()).append(" HpMax  ");
        if (b.getManaMax() != 0) bonus.append("+").append(b.getManaMax()).append(" ManaMax  ");
        if (b.getCostituzione() != 0) bonus.append("+").append(b.getCostituzione()).append(" Cos  ");
        if (b.getForza() != 0) bonus.append("+").append(b.getForza()).append(" For  ");
        if (b.getDestrezza() != 0) bonus.append("+").append(b.getDestrezza()).append(" Des  ");
        if (b.getIntelligenza() != 0) bonus.append("+").append(b.getIntelligenza()).append(" Int  ");
        if (b.getFede() != 0) bonus.append("+").append(b.getFede()).append(" Fede  ");
        if (b.getCarisma() != 0) bonus.append("+").append(b.getCarisma()).append(" Car  ");
        if (b.getFortuna() != 0) bonus.append("+").append(b.getFortuna()).append(" Fort  ");

        if (bonus.length() == 0) {
            return "-";
        } else {
            return bonus.toString().trim();
        }
    }

    /**
     * @return il pannello principale della finestra.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}