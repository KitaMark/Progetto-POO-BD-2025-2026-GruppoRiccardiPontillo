package gui;

import controller.Controller;
import model.Oggetto;
import model.OggettoConsumabile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Rappresenta l'interfaccia grafica (Editor) dedicata al Master per l'espansione
 * del Catalogo del Negozio all'interno di una specifica campagna.
 * <p>
 * Sfrutta un'interfaccia a schede ({@link JTabbedPane}) per separare logicamente
 * la creazione di {@code OggettoConsumabile} (es. Pozioni) dalla creazione di
 * {@code OggettoEquipaggiabile} (es. Armi, Armature). L'interfaccia non esegue
 * validazioni complesse ma delega la logica di business e il salvataggio al
 * {@link Controller}, limitandosi a catturare e mostrare all'utente eventuali eccezioni.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class EditorOggettiGui {

    private JPanel panel1;
    private JTable table1;
    private JButton indietroButton;
    private JLabel lblTitolo;
    private JTabbedPane tabbedPane1;

    private JTextField textField1; // Nome
    private JTextField textField2; // Costo
    private JTextField textField3; // Ripristino Hp
    private JTextField textField4; // Ripristino Mana
    private JButton aggiungiConsumabileButton;

    private JTextField textField5;  // Nome
    private JTextField textField6;  // Costo
    private JTextField textField7;  // Forza
    private JTextField textField8;  // Destrezza
    private JTextField textField9;  // Costituzione
    private JTextField textField10; // Intelligenza
    private JTextField textField11; // Fede
    private JTextField textField12; // Carisma
    private JTextField textField13; // Fortuna
    private JTextField textField14; // HpMax
    private JTextField textField15; // ManaMax
    private JButton aggiungiEquipaggiamentoButton;



    /** Il Controller di riferimento per la logica applicativa. */
    private Controller controller;

    /** Il frame corrente che ospita l'interfaccia. */
    private JFrame frameAttuale;

    /**
     * Costruisce l'interfaccia dell'Editor degli Oggetti.
     * Mappa i campi di input, inizializza la tabella riassuntiva e gestisce
     * l'invio dei dati smistando le chiamate al Controller a seconda
     * della scheda attiva.
     *
     * @param controller     Il {@link Controller} di sistema per l'interazione col DB.
     * @param frameChiamante Il {@link JFrame} della finestra di Regia da cui si proviene.
     */
    public EditorOggettiGui(Controller controller, JFrame frameChiamante) {
        this.controller = controller;

        // Configurazione della finestra
        frameAttuale = new JFrame("Catalogo Oggetti - " + controller.getCampagnaAttiva().getNome());
        frameAttuale.setContentPane(panel1);
        frameAttuale.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameAttuale.setSize(950, 700);
        frameAttuale.setLocationRelativeTo(null);

        inizializzaTabella();
        impostaZeriConsumabile();
        impostaZeriEquip();


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose();
                frameChiamante.setVisible(true);
            }
        });


        aggiungiConsumabileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = textField1.getText();
                    int costo = parseIntSicuro(textField2.getText(), "Costo");
                    int hp = parseIntSicuro(textField3.getText(), "Ripristino Hp");
                    int mana = parseIntSicuro(textField4.getText(), "Ripristino Mana");

                    // Delega al controller che lancerà eccezioni in caso di dati mancanti
                    controller.creaNuovoConsumabile(nome, costo, hp, mana);

                    JOptionPane.showMessageDialog(frameAttuale, "Consumabile aggiunto al catalogo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    // Sincronizzazione visiva post-salvataggio
                    inizializzaTabella();
                    textField1.setText("");
                    impostaZeriConsumabile();

                } catch (exception.DatiMancantiException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore di Formato", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        aggiungiEquipaggiamentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = textField5.getText();
                    int costo = parseIntSicuro(textField6.getText(), "Costo");
                    int forz = parseIntSicuro(textField7.getText(), "Forza");
                    int des = parseIntSicuro(textField8.getText(), "Destrezza");
                    int cos = parseIntSicuro(textField9.getText(), "Costituzione");
                    int intell = parseIntSicuro(textField10.getText(), "Intelligenza");
                    int fede = parseIntSicuro(textField11.getText(), "Fede");
                    int car = parseIntSicuro(textField12.getText(), "Carisma");
                    int fort = parseIntSicuro(textField13.getText(), "Fortuna");
                    int hpMax = parseIntSicuro(textField14.getText(), "HpMax");
                    int manaMax = parseIntSicuro(textField15.getText(), "ManaMax");

                    // Delega al controller la creazione dell'oggetto complesso
                    controller.creaNuovoEquipaggiamento(nome, costo, cos, forz, des, intell, fede, car, fort, hpMax, manaMax);

                    JOptionPane.showMessageDialog(frameAttuale, "Equipaggiamento aggiunto al catalogo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    // Sincronizzazione visiva post-salvataggio
                    inizializzaTabella();
                    textField5.setText("");
                    impostaZeriEquip();

                } catch (exception.DatiMancantiException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore Database", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frameAttuale, ex.getMessage(), "Errore di Formato", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frameChiamante.setVisible(false);
        frameAttuale.setVisible(true);
    }

    /**
     * Analizza una stringa testuale e tenta di convertirla in intero in modo sicuro.
     * Se la stringa è vuota o nulla, restituisce 0 (valore neutro predefinito).
     *
     * @param testo     Il valore stringa estratto dal JTextField.
     * @param nomeCampo Il nome logico del campo, utilizzato per generare un messaggio di errore chiaro.
     * @return L'intero parsato correttamente.
     * @throws Exception Se il testo contiene caratteri alfabetici o simboli non validi.
     */
    private int parseIntSicuro(String testo, String nomeCampo) throws Exception {
        if (testo == null || testo.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(testo.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Il campo '" + nomeCampo + "' deve contenere un numero intero valido.");
        }
    }

    /**
     * Azzera i campi testuali numerici della scheda "Nuovo Consumabile".
     */
    private void impostaZeriConsumabile() {
        textField2.setText("0");
        textField3.setText("0");
        textField4.setText("0");
    }

    /**
     * Azzera i campi testuali numerici della scheda "Nuovo Equipaggiamento".
     */
    private void impostaZeriEquip() {
        textField6.setText("0");
        textField7.setText("0");
        textField8.setText("0");
        textField9.setText("0");
        textField10.setText("0");
        textField11.setText("0");
        textField12.setText("0");
        textField13.setText("0");
        textField14.setText("0");
        textField15.setText("0");
    }

    /**
     * Inizializza il modello dati della JTable di riepilogo, impedendone la
     * modifica manuale delle celle.
     * <p>
     * Effettua una richiesta al Controller per recuperare la lista di Oggetti
     * del Catalogo e li smista per tipologia, applicando un blocco if-else
     * per discriminare le istanze.
     * </p>
     */
    private void inizializzaTabella() {
        String[] colonne = {"Nome Oggetto", "Tipo", "Costo"};

        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table1.setModel(model);
        table1.getTableHeader().setReorderingAllowed(false);

        for (Oggetto oggetto : controller.getCatalogoNegozio()) {
            String tipo;

            if (oggetto instanceof OggettoConsumabile) {
                tipo = "Consumabile";
            } else {
                tipo = "Equipaggiamento";
            }

            model.addRow(new Object[]{oggetto.getNome(), tipo, oggetto.getCosto()});
        }
    }
}