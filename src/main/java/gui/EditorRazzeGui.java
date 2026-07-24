package gui;

import controller.Controller;
import model.Razza;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica (Editor) dedicata al Master per la gestione
 * e la creazione delle Razze personalizzate all'interno di una specifica campagna.
 * <p>
 * Permette di visualizzare le razze già esistenti in una tabella riassuntiva e fornisce
 * un modulo per l'inserimento di nuove razze, definendone il nome, la descrizione
 * e i vari modificatori alle statistiche (Forza, Destrezza, HP, ecc.). I dati vengono
 * poi inviati al {@link Controller} per il salvataggio persistente nel database.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class EditorRazzeGui {
    private JPanel mainPanel;
    private JTable table1;
    private JTextField txtNome;
    private JTextArea txtDescrizione;
    private JTextField txtModForza;
    private JTextField txtModDestrezza;
    private JTextField txtModCostituzione;
    private JTextField txtModIntelligenza;
    private JTextField txtModFede;
    private JTextField txtModCarisma;
    private JTextField txtModFortuna;
    private JTextField txtModHpMax;
    private JTextField txtModManaMax;
    private JButton aggiungiButton;
    private JButton indietroButton;
    private JLabel lblTitolo;


    /** Il Controller di riferimento per delegare le logiche di business e il salvataggio nel DB. */
    private Controller controller;

    /** Il frame corrente che ospita l'interfaccia dell'editor. */
    private JFrame frameAttuale;

    /**
     * Costruisce l'interfaccia dell'Editor delle Razze, inizializzando i componenti
     * visivi, popolando la tabella con i dati attuali e configurando gli ascoltatori
     * di eventi per i pulsanti di salvataggio e navigazione.
     *
     * @param controller     Il {@link Controller} di sistema per interagire con il database.
     * @param frameChiamante Il {@link JFrame} della finestra precedente (Regia), utilizzato per tornarvi.
     */
    public EditorRazzeGui(Controller controller, JFrame frameChiamante) {
        this.controller = controller;

        // Configurazione della finestra (JFrame)
        frameAttuale = new JFrame("Editor Razze - " + controller.getCampagnaAttiva().getNome());
        frameAttuale.setContentPane(mainPanel);
        frameAttuale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAttuale.setSize(850, 650);
        frameAttuale.setLocationRelativeTo(null); // Centra la finestra nello schermo

        // Impostazione del titolo se la label è stata creata nel Designer
        if (lblTitolo != null) {
            lblTitolo.setText("Editor delle Razze");
        }

        inizializzaTabella();
        impostaZeri();

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose();          // Chiude l'editor attuale
                frameChiamante.setVisible(true); // Fa riapparire la CampagnaMasterGUI
            }
        });


        aggiungiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validazione del nome (obbligatorio)
                    String nome = txtNome.getText();
                    if (nome == null || nome.trim().isEmpty()) {
                        throw new Exception("Il nome della razza è obbligatorio.");
                    }

                    int forz = parseMod(txtModForza.getText());
                    int des = parseMod(txtModDestrezza.getText());
                    int cos = parseMod(txtModCostituzione.getText());
                    int intell = parseMod(txtModIntelligenza.getText());
                    int fede = parseMod(txtModFede.getText());
                    int car = parseMod(txtModCarisma.getText());
                    int fort = parseMod(txtModFortuna.getText());
                    int hp = parseMod(txtModHpMax.getText());
                    int mana = parseMod(txtModManaMax.getText());

                    // Chiamata al Controller per l'inserimento nel Dominio e nel Database
                    controller.creaNuovaRazza(nome, txtDescrizione.getText(), forz, des, cos, intell, fede, car, fort, hp, mana);

                    JOptionPane.showMessageDialog(frameAttuale, "Razza salvata con successo nel Database!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    inizializzaTabella();
                    svuotaCampi();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, "I modificatori devono essere numeri interi validi.", "Errore Formato", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Creazione", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frameChiamante.setVisible(false);
        frameAttuale.setVisible(true);
    }

    /**
     * Metodo di supporto per convertire in modo sicuro l'input testuale in un valore intero.
     * Se il campo viene lasciato vuoto dal Master, garantisce l'assegnazione del valore neutro (0)
     * per evitare eccezioni di formato durante il salvataggio.
     *
     * @param text La stringa di testo letta dal JTextField del modificatore.
     * @return Il valore intero parsato, oppure 0 se la stringa è vuota o nulla.
     * @throws NumberFormatException Se la stringa contiene caratteri non numerici o lettere.
     */
    private int parseMod(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text.trim());
    }

    /**
     * Reimposta tutti i campi testuali relativi ai modificatori delle statistiche
     * al loro valore neutrale di default ("0").
     */
    private void impostaZeri() {
        txtModForza.setText("0");
        txtModDestrezza.setText("0");
        txtModCostituzione.setText("0");
        txtModIntelligenza.setText("0");
        txtModFede.setText("0");
        txtModCarisma.setText("0");
        txtModFortuna.setText("0");
        txtModHpMax.setText("0");
        txtModManaMax.setText("0");
    }

    /**
     * Pulisce completamente il modulo di inserimento, svuotando il nome,
     * la descrizione e richiamando {@link #impostaZeri()} per preparare
     * l'interfaccia a un eventuale nuovo inserimento.
     */
    private void svuotaCampi() {
        txtNome.setText("");
        txtDescrizione.setText("");
        impostaZeri();
    }

    /**
     * Inizializza l'intestazione e il modello dati della JTable, inibendone
     * la modifica manuale delle celle da parte dell'utente.
     * <p>
     * Effettua una richiesta al Controller per recuperare la lista di Razze
     * associate alla Campagna attiva e le inietta dinamicamente come righe della tabella.
     * </p>
     */
    private void inizializzaTabella() {
        String[] colonne = {"Nome", "For", "Des", "Cos", "Int", "Fed", "Car", "Fort", "HpMax", "ManaMax"};

        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(model);

        table1.getTableHeader().setReorderingAllowed(false);

        table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table1.getColumnModel().getColumn(0).setPreferredWidth(160); // Nome
        for (int i = 1; i < table1.getColumnModel().getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(45);
        }

        // Popolamento dinamico: accediamo a getModificatori() per leggere i valori da Statistica
        for (Razza r : controller.getRazzePerCampagna(controller.getCampagnaAttiva())) {
            model.addRow(new Object[]{
                    r.getNome(),
                    r.getModificatori().getForza(),
                    r.getModificatori().getDestrezza(),
                    r.getModificatori().getCostituzione(),
                    r.getModificatori().getIntelligenza(),
                    r.getModificatori().getFede(),
                    r.getModificatori().getCarisma(),
                    r.getModificatori().getFortuna(),
                    r.getModificatori().getHpMax(),
                    r.getModificatori().getManaMax()
            });
        }
    }
}