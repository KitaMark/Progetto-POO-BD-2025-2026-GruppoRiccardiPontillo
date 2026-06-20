package gui;

import controller.Controller;
import exception.AbilitaGiaAppresaException;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica principale (Scheda Personaggio) dedicata al
 * {@link Giocatore} durante lo svolgimento di una campagna.
 * <p>
 * Mette a disposizione del giocatore tutti gli strumenti necessari per l'interazione
 * nel mondo di gioco: visualizzazione e potenziamento delle statistiche (tramite la
 * spesa dei Punti progressione), gestione dell'inventario (equipaggiamento e consumabili),
 * apprendimento di nuove abilità di classe e l'interazione commerciale con il Negozio
 * per l'acquisto e la vendita di oggetti.
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
    private JTable inventarioTable; // Ora è il Negozio
    private JPanel buttonPanel1;
    private JButton acquistaButton;

    private JTable equipaggiamentoTable;
    private JButton vendiButton;
    private JButton rimuoviButton;
    private JButton equipaggiaButton;
    private JTable consumabiliTable;
    private JButton vendiButton1;
    private JButton usaButton;
    private JTable abilitaTable;
    private JButton imparaButton;

    /** Il Controller di riferimento per l'orchestrazione delle meccaniche di gioco. */
    private Controller controller;
    /** Il Giocatore attualmente autenticato che sta visualizzando la scheda del proprio PG. */
    private Giocatore giocatoreLoggato;
    /** Il nome della campagna in corso. */
    private String nomeCampagnaAttuale;
    /** Il frame corrente che ospita l'interfaccia della scheda personaggio. */
    private JFrame frameAttuale;


    /**
     * Costruisce l'interfaccia della Scheda Personaggio, inizializzando le tabelle
     * informative e configurando tutti i Listener per le azioni di gioco (crescita,
     * equipaggiamento, acquisti, uso di consumabili).
     *
     * @param controller   Il {@link Controller} di sistema.
     * @param giocatore    L'oggetto {@link Giocatore} associato.
     * @param nomeCampagna Il nome della campagna attualmente visualizzata.
     * @param frame        Il {@link JFrame} principale che ospita il pannello.
     */
    public CampagnaGiocatoreGUI(Controller controller, Giocatore giocatore, String nomeCampagna, JFrame frame) {
        this.controller = controller;
        this.giocatoreLoggato = giocatore;
        this.nomeCampagnaAttuale = nomeCampagna;
        this.frameAttuale = frame;

        inizializzaTabelle();

        tornaAllaSchermataPrecedenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose(); // Chiude la scheda personaggio attuale

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
                    inizializzaTabelle(); // ricarica negozio e zaino
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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
                } catch (Exception ex) {
                    String messaggioPulito = ex.getMessage().split("\n")[0]
                            .replace("ERRORE: ", "")
                            .replace("Requisiti insufficienti: ", "");

                    JOptionPane.showMessageDialog(frameAttuale, messaggioPulito, "Requisiti Insufficienti", JOptionPane.ERROR_MESSAGE);
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
                    inizializzaTabelle(); //  Cambia "Sì" in "No" nella tabella
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        vendiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = equipaggiamentoTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un oggetto da vendere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = equipaggiamentoTable.getValueAt(riga, 0).toString();
                try {
                    controller.vendiOggetto(nomeOggetto, nomeCampagnaAttuale);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai venduto: " + nomeOggetto);
                    inizializzaTabelle(); // Toglie l'oggetto dalla tabella e aumenta l'oro
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
                    JOptionPane.showMessageDialog(frameAttuale, "Hai utilizzato: " + nomeOggetto);
                    inizializzaTabelle(); //Scala la quantità e aggiorna HP/Mana
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        vendiButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = consumabiliTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un consumabile da vendere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = consumabiliTable.getValueAt(riga, 0).toString();
                try {
                    controller.vendiOggetto(nomeOggetto, nomeCampagnaAttuale);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai venduto: " + nomeOggetto);
                    inizializzaTabelle(); //  Scala la quantità e aumenta l'oro
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        imparaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = abilitaTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un'abilità da imparare.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeAbilita = abilitaTable.getValueAt(riga, 0).toString();
                try {
                    controller.imparaAbilita(nomeAbilita, nomeCampagnaAttuale);
                    inizializzaTabelle();
                    JOptionPane.showMessageDialog(frameAttuale, "Hai appreso una nuova abilità: " + nomeAbilita);
                } catch (Exception ex) {
                    // Cattura TUTTE le tue eccezioni (GiaAppresa, NonSbloccabile, NonSelezionata)
                    // e mostra il messaggio personalizzato che hai scritto nel Controller
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Apprendimento", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Metodo  privato che definisce l'intestazione e i modelli dati per
     * tutte le tabelle presenti nella scheda (Statistiche, Negozio, Equipaggiamento,
     * Consumabili, Abilità), rendendole non modificabili direttamente.
     */
    private void inizializzaTabelle() {
        Personaggio pg = giocatoreLoggato.getPersonaggioInCampagna(controller.getCampagnaAttiva());
        if (pg == null) return;

        // Tabella Statistiche
        String[] colonneStat = {"Statistica", "Valore Attuale"};
        DefaultTableModel modelStat = new DefaultTableModel(null, colonneStat) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statisticheTable.setModel(modelStat);
        statisticheTable.getTableHeader().setReorderingAllowed(false);
        statisticheTable.getTableHeader().setResizingAllowed(false);

        // Inseriamo i dati  del personaggio
        modelStat.addRow(new Object[]{"Oro", pg.getOro()});
        modelStat.addRow(new Object[]{"Punti Spendibili", pg.getPuntiStatistica()});
        modelStat.addRow(new Object[]{"HP Correnti", pg.getHpCorrenti() + " / " + pg.getStatisticheBase().getHpMax()});
        modelStat.addRow(new Object[]{"Mana Corrente", pg.getManaCorrente() + " / " + pg.getStatisticheBase().getManaMax()});
        modelStat.addRow(new Object[]{"Forza", pg.getStatisticheBase().getForza()});
        modelStat.addRow(new Object[]{"Destrezza", pg.getStatisticheBase().getDestrezza()});


        // Tabella Equipaggiamento
        String[] colonneEquip = {"Nome Oggetto", "Bonus", "Equipaggiato"};
        DefaultTableModel modelEquip = new DefaultTableModel(null, colonneEquip) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        equipaggiamentoTable.setModel(modelEquip);
        equipaggiamentoTable.getTableHeader().setReorderingAllowed(false);
        equipaggiamentoTable.getTableHeader().setResizingAllowed(false);

        for (OggettoEquipaggiabile oggettoEquipaggiabile : pg.getInventarioEquipaggiabili().keySet()) {
            String stato = pg.getInventarioEquipaggiabili().get(oggettoEquipaggiabile) ? "Sì" : "No";
            modelEquip.addRow(new Object[]{oggettoEquipaggiabile.getNome(), "+" + oggettoEquipaggiabile.getBonus().getForza() + " Forza", stato});
        }


        //Tabella Consumabili
        String[] colonneCons = {"Nome Oggetto", "Ripristina HP", "Ripristina Mana", "Quantità"};
        DefaultTableModel modelCons = new DefaultTableModel(null, colonneCons) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        consumabiliTable.setModel(modelCons);
        consumabiliTable.getTableHeader().setReorderingAllowed(false);
        consumabiliTable.getTableHeader().setResizingAllowed(false);

        for (OggettoConsumabile oggettoConsumabile : pg.getInventarioConsumabili().keySet()) {
            int quantita = pg.getInventarioConsumabili().get(oggettoConsumabile);

            String hpText = oggettoConsumabile.getRipristinoHP() == 0 ? "-" : String.valueOf(oggettoConsumabile.getRipristinoHP());
            String manaText = oggettoConsumabile.getRipristinoMana() == 0 ? "-" : String.valueOf(oggettoConsumabile.getRipristinoMana());

            modelCons.addRow(new Object[]{oggettoConsumabile.getNome(), hpText, manaText, quantita});
        }


        //Tabella Negozio
        String[] colonneInv = {"Oggetto in Vendita", "Tipo", "Costo (Oro)"};
        DefaultTableModel modelInv = new DefaultTableModel(null, colonneInv) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventarioTable.setModel(modelInv);
        inventarioTable.getTableHeader().setReorderingAllowed(false);
        inventarioTable.getTableHeader().setResizingAllowed(false);


        for (Oggetto o : controller.getCatalogoNegozio()) {
            modelInv.addRow(new Object[]{o.getNome(), o.getTipo(), o.getCosto()});
        }

        // Tabella Abilità
        String[] colonneAbilita = {"Nome Abilità", "Descrizione", "Appresa"};
        DefaultTableModel modelAbilita = new DefaultTableModel(null, colonneAbilita) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        abilitaTable.setModel(modelAbilita);
        abilitaTable.getTableHeader().setReorderingAllowed(false);
        abilitaTable.getTableHeader().setResizingAllowed(false);

        if (pg.getClasse().getAbilitaSbloccabili() != null) {
            for (Abilita abilita : pg.getClasse().getAbilitaSbloccabili()) {
                // Controlla se il personaggio ha già questa abilità nella sua lista
                String appresa = pg.getListaAbilita().contains(abilita) ? "Sì" : "No";
                modelAbilita.addRow(new Object[]{abilita.getNome(), abilita.getDescrizione(), appresa});
            }
        }
    }

    /**
     * Restituisce il pannello principale della Scheda Personaggio.
     *
     * @return Il {@link JPanel} utilizzato per il rendering visivo dell'interfaccia.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}