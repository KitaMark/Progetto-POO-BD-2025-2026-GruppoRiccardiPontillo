package gui;

import controller.Controller;
import model.Oggetto;
import model.OggettoConsumabile;
import model.OggettoEquipaggiabile;
import model.Personaggio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModificaInventarioGUI extends JDialog {
    private JPanel mainPanel;
    private JTable inventarioConsumabiliTable;
    private JButton aggiungiButton;
    private JButton rimuoviButton;
    private JPanel buttonPanel;
    private JPanel labelPanel;
    private JLabel zainoLabel;
    private JLabel zainoText;
    private JTabbedPane tabbedPane;
    private JPanel consumabiliPane;
    private JPanel equipaggiabiliPane;
    private JTable inventarioEquipaggiabiliTable;

    private Controller controller;
    private Personaggio pg;

    public ModificaInventarioGUI(JFrame frameChiamante, Controller controller, Personaggio personaggio){
        super(frameChiamante, "Modifica Zaino", true);
        this.controller = controller;
        this.pg = personaggio;
        super.setContentPane(mainPanel);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(true);

        zainoText.setText(pg.getNome());

        inizializzaTabellaConsumabili();
        inizializzaTabellaEquipaggiabili();
        aggiungiListeners();

        super.setMinimumSize(new Dimension(500, 400));
        super.pack();
        super.setLocationRelativeTo(frameChiamante); // Centra rispetto alla finestra del Master
        super.setVisible(true);
    }

    private void inizializzaTabellaConsumabili(){
        String[] colonne = {"Nome", "Quantità", "ID"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (OggettoConsumabile o : pg.getInventarioConsumabili().keySet()) {
            model.addRow(new Object[]{o.getNome(), pg.getInventarioConsumabili().get(o), o.getId()});
        }

        inventarioConsumabiliTable.setModel(model);

        // Nascondi la colonna ID
        TableColumn colonnaID = inventarioConsumabiliTable.getColumnModel().getColumn(2);
        inventarioConsumabiliTable.getColumnModel().removeColumn(colonnaID);

        // Configura la larghezza e l'altezza (200 per Nome, 100 per Quantità)
        configuraTabella(inventarioConsumabiliTable, new int[]{250, 100});
    }

    private void inizializzaTabellaEquipaggiabili(){
        String[] colonne = {"Nome", "ID"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (OggettoEquipaggiabile o : pg.getInventarioEquipaggiabili().keySet()) {
            model.addRow(new Object[]{o.getNome(), o.getId()});
        }

        inventarioEquipaggiabiliTable.setModel(model);

        // Nascondi la colonna ID
        TableColumn colonnaID = inventarioEquipaggiabiliTable.getColumnModel().getColumn(1);
        inventarioEquipaggiabiliTable.getColumnModel().removeColumn(colonnaID);

        // Configura la larghezza e l'altezza
        configuraTabella(inventarioEquipaggiabiliTable, new int[]{350});
    }

    /**
     * Configura la tabella impostando un layout pulito e l'altezza automatica.
     */
    private void configuraTabella(JTable tabella, int[] larghezzePreferite) {
        tabella.getTableHeader().setReorderingAllowed(false);
        tabella.getTableHeader().setResizingAllowed(false);
        tabella.setRowHeight(26);
        tabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumnModel columnModel = tabella.getColumnModel();
        for (int i = 0; i < larghezzePreferite.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(larghezzePreferite[i]);
        }

        // Calcola l'altezza necessaria per non mostrare spazi vuoti o scroll verticali superflui
        int altezzaHeader = tabella.getTableHeader().getPreferredSize().height;
        // Garantiamo un minimo di 2 righe di spazio anche se la tabella è vuota, per evitare che sparisca visivamente
        int righeDaMostrare = Math.max(2, tabella.getRowCount());
        int altezzaRighe = righeDaMostrare * tabella.getRowHeight();

        tabella.setPreferredScrollableViewportSize(new Dimension(tabella.getPreferredSize().width, altezzaHeader + altezzaRighe + 6));
    }

    /**
     * Pulisce una stringa di errore concatenata, estraendo solo il messaggio utile finale.
     */
    private String pulisciMessaggioErrore(String errore) {
        if (errore == null) return "Errore sconosciuto.";
        if (errore.contains(":")) {
            // Prende solo l'ultima parte della stringa dopo l'ultimo ":"
            return errore.substring(errore.lastIndexOf(":") + 1).trim();
        }
        return errore;
    }

    private void aggiungiListeners(){

        rimuoviButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tabSelezionata = tabbedPane.getSelectedIndex();
                int riga = -1;
                int idOggetto = -1;
                String nomeOggetto = "";

                if (tabSelezionata == 0) { // Consumabili
                    riga = inventarioConsumabiliTable.getSelectedRow();
                    if (riga != -1) {
                        nomeOggetto = (String) inventarioConsumabiliTable.getModel().getValueAt(riga, 0);
                        idOggetto = (int) inventarioConsumabiliTable.getModel().getValueAt(riga, 2); // Indice 2 nel model originale
                    }
                } else if (tabSelezionata == 1) { // Equipaggiabili
                    riga = inventarioEquipaggiabiliTable.getSelectedRow();
                    if (riga != -1) {
                        nomeOggetto = (String) inventarioEquipaggiabiliTable.getModel().getValueAt(riga, 0);
                        idOggetto = (int) inventarioEquipaggiabiliTable.getModel().getValueAt(riga, 1); // Indice 1 nel model originale
                    }
                }

                if (riga == -1) {
                    JOptionPane.showMessageDialog(ModificaInventarioGUI.this, "Seleziona un oggetto da rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int conferma = JOptionPane.showConfirmDialog(ModificaInventarioGUI.this,
                        "Sei sicuro di voler rimuovere '" + nomeOggetto + "' dall'inventario?", "Conferma rimozione",
                        JOptionPane.YES_NO_OPTION);

                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.rimuoviOggettoDaInventario(pg, idOggetto);
                        inizializzaTabellaConsumabili();
                        inizializzaTabellaEquipaggiabili();
                    } catch (RuntimeException ex) {
                        String msgPulito = pulisciMessaggioErrore(ex.getMessage());
                        JOptionPane.showMessageDialog(ModificaInventarioGUI.this, msgPulito, "Errore Rimozione", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        aggiungiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] catalogo =  controller.getCampagnaAttiva().getCatalogoOggetti().toArray();
                if(catalogo.length == 0){
                    JOptionPane.showMessageDialog(ModificaInventarioGUI.this, "Nessun oggetto trovato per questa campagna.", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                Oggetto scelta = (Oggetto) JOptionPane.showInputDialog(ModificaInventarioGUI.this,
                        "Seleziona un oggetto da donare a " + pg.getNome() + ":",
                        "Assegna oggetto",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        catalogo,
                        catalogo[0]);

                if(scelta != null){
                    try {
                        controller.assegnaOggettoMaster(pg.getId(), scelta.getId(), pg.isPg());
                        JOptionPane.showMessageDialog(ModificaInventarioGUI.this, "'" + scelta.getNome() + "' aggiunto con successo!", "Zaino Aggiornato", JOptionPane.INFORMATION_MESSAGE);
                        inizializzaTabellaConsumabili();
                        inizializzaTabellaEquipaggiabili();
                    } catch (RuntimeException ex) {
                        String msgPulito = pulisciMessaggioErrore(ex.getMessage());
                        JOptionPane.showMessageDialog(ModificaInventarioGUI.this, msgPulito, "Operazione Negata", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
    }
}