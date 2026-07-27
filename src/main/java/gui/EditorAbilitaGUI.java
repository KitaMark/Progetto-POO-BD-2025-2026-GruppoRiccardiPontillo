package gui;

import controller.Controller;
import exception.DatiMancantiException;
import model.Abilita;
import model.Classe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Rappresenta l'interfaccia grafica (Editor) dedicata al Master per la gestione
 * delle Abilità specifiche di una singola Classe di gioco.
 * <p>
 * Permette di visualizzare le abilità attualmente sbloccabili per la classe selezionata,
 * di rimuoverne di esistenti o di crearne di nuove definendone nome e descrizione.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class EditorAbilitaGUI {
    private JPanel mainPanel;
    private JButton indietroButton;
    private JTable abilitaTable;
    private JButton rimuoviAbilitàButton;
    private JTextField textFieldNome;
    private JTextArea textAreadescrizione;
    private JButton aggiungiButton;
    private JLabel nomeLabel;

    private Controller controller;
    private JFrame frameAttuale;
    private Classe classeSelezionata;

    /**
     * Costruisce l'interfaccia dell'Editor delle Abilità.
     *
     * @param controller        Il {@link Controller} di sistema.
     * @param frameChiamante    Il {@link JFrame} precedente (Editor Classi) da cui si proviene.
     * @param classeSelezionata La {@link Classe} di cui si vogliono gestire le abilità.
     */
    public EditorAbilitaGUI(Controller controller, JFrame frameChiamante, Classe classeSelezionata) {
        this.controller = controller;
        this.classeSelezionata = classeSelezionata;

        frameAttuale = new JFrame("Editor Abilità - Classe: " + classeSelezionata.getNome());
        frameAttuale.setContentPane(mainPanel);
        frameAttuale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAttuale.setSize(750, 500);
        frameAttuale.setLocationRelativeTo(null);

        controller.caricaAbilitaSbloccabiliPerClasse(classeSelezionata);
        inizializzaTabella();


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose();
                frameChiamante.setVisible(true);
            }
        });

        aggiungiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = textFieldNome.getText();
                    String descrizione = textAreadescrizione.getText();

                    controller.creaNuovaAbilita(classeSelezionata, nome, descrizione);

                    JOptionPane.showMessageDialog(frameAttuale, "Abilità aggiunta con successo alla classe " + classeSelezionata.getNome() + "!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    // Resetta i campi testuali e aggiorna la tabella
                    textFieldNome.setText("");
                    textAreadescrizione.setText("");
                    inizializzaTabella();

                } catch (DatiMancantiException | IllegalArgumentException ex) {
                    // Adesso sia i campi vuoti che i duplicati mostreranno il popup giallo "Attenzione" pulito
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    // Questo blocco scatterà SOLO per i veri errori critici di PostgreSQL (icona rossa)
                    JOptionPane.showMessageDialog(frameAttuale, "Errore di connessione o salvataggio: " + ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        rimuoviAbilitàButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = abilitaTable.getSelectedRow();

                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frameAttuale, "Seleziona un'abilità dalla tabella per poterla rimuovere.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeAbilita = abilitaTable.getValueAt(rigaSelezionata, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frameAttuale, "Sei sicuro di voler eliminare l'abilità '" + nomeAbilita + "'?", "Conferma Rimozione", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    try {
                        controller.rimuoviAbilitaDaClasse(classeSelezionata, nomeAbilita);
                        JOptionPane.showMessageDialog(frameAttuale, "Abilità rimossa con successo!");
                        inizializzaTabella();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        frameChiamante.setVisible(false);
        frameAttuale.setVisible(true);
    }

    /**
     * Inizializza e popola la tabella con le abilità della classe selezionata.
     */
    private void inizializzaTabella() {
        String[] colonne = {"Nome Abilità", "Descrizione"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        abilitaTable.setModel(model);
        abilitaTable.getTableHeader().setReorderingAllowed(false);
        abilitaTable.setRowHeight(25);

        List<Abilita> listaAbilita = classeSelezionata.getAbilitaSbloccabili();
        if (listaAbilita != null) {
            for (Abilita abilita : listaAbilita) {
                model.addRow(new Object[]{abilita.getNome(), abilita.getDescrizione()});
            }
        }
    }
}