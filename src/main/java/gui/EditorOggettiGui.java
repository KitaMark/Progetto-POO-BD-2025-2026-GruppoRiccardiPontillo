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

    private JPanel mainPanel;
    private JTable tabellaOggetti;
    private JButton indietroButton;
    private JLabel lblTitolo;
    private JTabbedPane schedeTabbedPane;

    // Componenti per Oggetto Consumabile
    private JTextField txtNomeConsumabile;
    private JTextField txtCostoConsumabile;
    private JTextField txtRipristinoHp;
    private JTextField txtRipristinoMana;
    private JButton aggiungiConsumabileButton;

    // Componenti per Oggetto Equipaggiabile
    private JTextField txtNomeEquipaggiamento;
    private JTextField txtCostoEquipaggiamento;
    private JTextField txtForza;
    private JTextField txtDestrezza;
    private JTextField txtCostituzione;
    private JTextField txtIntelligenza;
    private JTextField txtFede;
    private JTextField txtCarisma;
    private JTextField txtFortuna;
    private JTextField txtHpMax;
    private JTextField txtManaMax;
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
        frameAttuale.setContentPane(mainPanel);
        frameAttuale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                    String nome = txtNomeConsumabile.getText();
                    int costo = parseIntSicuro(txtCostoConsumabile.getText(), "Costo");
                    int hp = parseIntSicuro(txtRipristinoHp.getText(), "Ripristino Hp");
                    int mana = parseIntSicuro(txtRipristinoMana.getText(), "Ripristino Mana");

                    controller.creaNuovoConsumabile(nome, costo, hp, mana);

                    JOptionPane.showMessageDialog(frameAttuale, "Consumabile aggiunto al catalogo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    inizializzaTabella();
                    txtNomeConsumabile.setText("");
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
                    String nome = txtNomeEquipaggiamento.getText();
                    int costo = parseIntSicuro(txtCostoEquipaggiamento.getText(), "Costo");
                    int forz = parseIntSicuro(txtForza.getText(), "Forza");
                    int des = parseIntSicuro(txtDestrezza.getText(), "Destrezza");
                    int cos = parseIntSicuro(txtCostituzione.getText(), "Costituzione");
                    int intell = parseIntSicuro(txtIntelligenza.getText(), "Intelligenza");
                    int fede = parseIntSicuro(txtFede.getText(), "Fede");
                    int car = parseIntSicuro(txtCarisma.getText(), "Carisma");
                    int fort = parseIntSicuro(txtFortuna.getText(), "Fortuna");
                    int hpMax = parseIntSicuro(txtHpMax.getText(), "HpMax");
                    int manaMax = parseIntSicuro(txtManaMax.getText(), "ManaMax");

                    controller.creaNuovoEquipaggiamento(nome, costo, cos, forz, des, intell, fede, car, fort, hpMax, manaMax);

                    JOptionPane.showMessageDialog(frameAttuale, "Equipaggiamento aggiunto al catalogo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    inizializzaTabella();
                    txtNomeEquipaggiamento.setText("");
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
        txtCostoConsumabile.setText("0");
        txtRipristinoHp.setText("0");
        txtRipristinoMana.setText("0");
    }

    /**
     * Azzera i campi testuali numerici della scheda "Nuovo Equipaggiamento".
     */
    private void impostaZeriEquip() {
        txtCostoEquipaggiamento.setText("0");
        txtForza.setText("0");
        txtDestrezza.setText("0");
        txtCostituzione.setText("0");
        txtIntelligenza.setText("0");
        txtFede.setText("0");
        txtCarisma.setText("0");
        txtFortuna.setText("0");
        txtHpMax.setText("0");
        txtManaMax.setText("0");
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

        tabellaOggetti.setModel(model);
        tabellaOggetti.getTableHeader().setReorderingAllowed(false);

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