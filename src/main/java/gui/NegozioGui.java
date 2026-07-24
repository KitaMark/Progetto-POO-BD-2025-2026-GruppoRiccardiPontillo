package gui;

import controller.Controller;
import exception.OggettoNonSelezionatoException;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

/**
 * Rappresenta l'interfaccia grafica del Negozio dedicata alle compravendite.
 * <p>
 * Mette a disposizione una vista divisa in due pannelli per l'acquisto e la vendita
 * degli oggetti, mostrando dinamicamente il catalogo della bottega e lo zaino del giocatore.
 * </p>
 */
public class NegozioGui {
    private JPanel mainPanel;
    private JTable bottegaTable;
    private JButton acquistaButton;
    private JTable inventarioTable;
    private JButton vendiButton;
    private JLabel moneteLabel;

    // Nuovi componenti aggiunti
    private JButton tornaIndietroButton;
    private JSplitPane splitPane;

    private Controller controller;
    private String nomeCampagnaAttuale;
    private JFrame frameAttuale;

    /**
     * Costruisce l'interfaccia del Negozio, inizializza le tabelle e configura i listener.
     *
     * @param controller   Il {@link Controller} di sistema.
     * @param nomeCampagna Il nome della campagna attualmente visualizzata.
     */
    public NegozioGui(Controller controller, String nomeCampagna) {
        this.controller = controller;
        this.nomeCampagnaAttuale = nomeCampagna;

        this.frameAttuale = new JFrame("Bottega del Mercante");
        this.frameAttuale.setContentPane(mainPanel);
        this.frameAttuale.setSize(850, 550);
        this.frameAttuale.setLocationRelativeTo(null);
        this.frameAttuale.setVisible(true);

        if (splitPane != null) {
            splitPane.setEnabled(false);
            splitPane.setResizeWeight(0.5);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    splitPane.setDividerLocation(0.5);
                }
            });
        }

        if (tornaIndietroButton != null) {
            tornaIndietroButton.setPreferredSize(new Dimension(140, 30));
            tornaIndietroButton.setMinimumSize(new Dimension(140, 30));
            tornaIndietroButton.setMaximumSize(new Dimension(140, 30));
        }

        // Popolamento iniziale
        aggiornaTabelleEMonete();

        tornaIndietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chiude la finestra del negozio e lascia sotto la scheda del PG
                frameAttuale.dispose();
            }
        });

        acquistaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = bottegaTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un oggetto dal mercante per comprarlo.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = bottegaTable.getValueAt(riga, 0).toString();
                try {
                    controller.compraOggetto(nomeOggetto);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai acquistato: " + nomeOggetto);
                    aggiornaTabelleEMonete();
                } catch (OggettoNonSelezionatoException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Acquisto", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, "Impossibile completare la transazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        vendiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = inventarioTable.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un oggetto dal tuo zaino da vendere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeOggetto = inventarioTable.getValueAt(riga, 0).toString();
                try {
                    controller.vendiOggetto(nomeOggetto, nomeCampagnaAttuale);
                    JOptionPane.showMessageDialog(frameAttuale, "Hai venduto: " + nomeOggetto);
                    aggiornaTabelleEMonete();
                } catch (OggettoNonSelezionatoException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Vendita", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, "Impossibile completare la transazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Ricarica dinamicamente i dati dal database popolando sia il negozio che l'inventario.
     * <p>
     * Aggiorna anche l'etichetta delle monete possedute per riflettere le transazioni.
     * </p>
     */
    private void aggiornaTabelleEMonete() {
        Giocatore giocatore = (Giocatore) controller.getUtenteAttivo();
        Personaggio pg = giocatore.getPersonaggioInCampagna(controller.getCampagnaAttiva());


        controller.leggiInventarioPersonaggio(pg);

        // Aggiorna l'etichetta delle monete con il valore del PG
        if (moneteLabel != null) {
            moneteLabel.setText("Monete possedute: " + pg.getOro());
        }

        //TABELLA BOTTEGA DEL MERCANTE-
        String[] colonneBottega = {"Nome Oggetto", "Tipo", "Costo (Compra)"};
        DefaultTableModel modelBottega = new DefaultTableModel(null, colonneBottega) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bottegaTable.setModel(modelBottega);
        bottegaTable.getTableHeader().setReorderingAllowed(false);

        List<Oggetto> catalogo = controller.getCatalogoNegozio();
        for (Oggetto o : catalogo) {
            String tipo = (o instanceof OggettoConsumabile) ? "Consumabile" : "Equipaggiamento";
            modelBottega.addRow(new Object[]{o.getNome(), tipo, o.getCosto()});
        }

        //TABELLA ZAINO  GIOCATORE
        String[] colonneZaino = {"Nome Oggetto", "Tipo/Stato", "Ricavo (Vendi)"};
        DefaultTableModel modelZaino = new DefaultTableModel(null, colonneZaino) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        inventarioTable.setModel(modelZaino);
        inventarioTable.getTableHeader().setReorderingAllowed(false);

        //Equipaggiabili (Calcolando metà del prezzo)
        for (Map.Entry<OggettoEquipaggiabile, Boolean> entry : pg.getInventarioEquipaggiabili().entrySet()) {
            OggettoEquipaggiabile eq = entry.getKey();
            String stato = entry.getValue() ? "Equipaggiato" : "Nello Zaino";
            modelZaino.addRow(new Object[]{eq.getNome(), stato, eq.getCosto() / 2});
        }

        //Consumabili (Calcolando metà del prezzo e mostrando la quantità)
        for (Map.Entry<OggettoConsumabile, Integer> entry : pg.getInventarioConsumabili().entrySet()) {
            OggettoConsumabile cons = entry.getKey();
            int quantita = entry.getValue();
            modelZaino.addRow(new Object[]{cons.getNome(), "Consumabile (x" + quantita + ")", cons.getCosto() / 2});
        }
    }

    public JFrame getFrame() {
        return frameAttuale;
    }
}