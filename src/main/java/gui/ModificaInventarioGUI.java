package gui;

import controller.Controller;
import model.OggettoConsumabile;
import model.OggettoEquipaggiabile;
import model.Personaggio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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

        super.setMinimumSize(new Dimension(800, 600));
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private void inizializzaTabellaConsumabili(){
        String[] colonne = {"Nome", "Quantità", "ID"};
        DefaultTableModel model = new DefaultTableModel(null, colonne);
        inventarioConsumabiliTable.setModel(model);
        TableColumn colonna = inventarioConsumabiliTable.getColumnModel().getColumn(2);
        inventarioConsumabiliTable.getColumnModel().removeColumn(colonna);
        inventarioConsumabiliTable.getTableHeader().setResizingAllowed(false);
        inventarioConsumabiliTable.getTableHeader().setReorderingAllowed(false);
        for(OggettoConsumabile o : pg.getInventarioConsumabili().keySet()){
            model.addRow(new Object[] {o.getNome(), pg.getInventarioConsumabili().get(o), o.getId()});
        }
    }

    private void inizializzaTabellaEquipaggiabili(){
        String[] colonne = {"Nome", "ID"};
        DefaultTableModel model = new DefaultTableModel(null, colonne);
        inventarioEquipaggiabiliTable.setModel(model);
        TableColumn colonna = inventarioEquipaggiabiliTable.getColumnModel().getColumn(1);
        inventarioEquipaggiabiliTable.getColumnModel().removeColumn(colonna);
        inventarioEquipaggiabiliTable.getTableHeader().setResizingAllowed(false);
        inventarioEquipaggiabiliTable.getTableHeader().setReorderingAllowed(false);
        for(OggettoEquipaggiabile o : pg.getInventarioEquipaggiabili().keySet()){
            model.addRow(new Object[]{o.getNome(), o.getId()});
        }
    }

    private void aggiungiListeners(){

        rimuoviButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }
}
