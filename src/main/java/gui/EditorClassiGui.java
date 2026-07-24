package gui;

import controller.Controller;
import model.Classe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica (Editor) dedicata al Master per la gestione
 * e la creazione delle Classi (es. Guerriero, Mago) all'interno di una campagna.
 * <p>
 * Questa finestra permette di visualizzare l'elenco delle classi già esistenti
 * tramite una JTable e fornisce un modulo testuale per l'inserimento di nuove
 * classi definendone nome e descrizione (lore). I dati raccolti vengono poi
 * delegati al {@link Controller} per la persistenza nel database.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class EditorClassiGui {
    private JPanel mainPanel;
    private JTable tableClassi;
    private JButton indietroButton;
    private JLabel lblTitle;
    private JTextField txtNome;
    private JTextArea txtDescrizione; // Campo ora correttamente mappato dal form!
    private JButton aggiungiButton;

    /** Il Controller di riferimento per l'orchestrazione delle operazioni di business. */
    private Controller controller;

    /** Il frame corrente che ospita l'interfaccia dell'editor. */
    private JFrame frameAttuale;

    /**
     * Costruisce l'interfaccia dell'Editor delle Classi.
     * Inizializza i componenti visivi, popola la tabella con i dati attuali
     * e imposta i listener per il salvataggio e la navigazione.
     *
     * @param controller     Il {@link Controller} di sistema.
     * @param frameChiamante Il {@link JFrame} della finestra di Regia, utilizzato per la navigazione.
     */
    public EditorClassiGui(Controller controller, JFrame frameChiamante) {
        this.controller = controller;

        frameAttuale = new JFrame("Editor Classi - " + controller.getCampagnaAttiva().getNome());
        frameAttuale.setContentPane(mainPanel);
        frameAttuale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAttuale.setSize(800, 600);
        frameAttuale.setLocationRelativeTo(null);

        inizializzaTabella();

        // Ritorna alla Regia
        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose();
                frameChiamante.setVisible(true);
            }
        });

        // Salva la nuova Classe catturando le eccezioni
        aggiungiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = txtNome.getText();
                    String descrizione = txtDescrizione.getText(); // Legge la descrizione inserita

                    controller.creaNuovaClasse(nome, descrizione);

                    JOptionPane.showMessageDialog(frameAttuale, "Classe salvata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    inizializzaTabella();
                    txtNome.setText("");
                    txtDescrizione.setText(""); // Svuota il campo dopo il salvataggio

                } catch (exception.DatiMancantiException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frameChiamante.setVisible(false);
        frameAttuale.setVisible(true);
    }

    /**
     * Inizializza l'intestazione e il modello dati della JTable, inibendone
     * la modifica manuale delle celle da parte dell'utente.
     * Recupera la lista di Classi associate alla Campagna attiva e le inietta nella tabella.
     */
    private void inizializzaTabella() {
        String[] colonne = {"Nome Classe", "Descrizione"};
        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableClassi.setModel(model);
        tableClassi.getTableHeader().setReorderingAllowed(false);

        for (Classe classe : controller.getClassiPerCampagna(controller.getCampagnaAttiva())) {
            model.addRow(new Object[]{classe.getNome(), classe.getDescrizione()});
        }
    }
}