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

    // Componenti per Oggetto Equipaggiabile: Variabili Base
    private JTextField txtNomeEquipaggiamento;
    private JTextField txtCostoEquipaggiamento;
    private JButton aggiungiEquipaggiamentoButton;

    // Componenti per Oggetto Equipaggiabile: BONUS
    private JTextField txtBonusForza;
    private JTextField txtBonusDestrezza;
    private JTextField txtBonusCostituzione;
    private JTextField txtBonusIntelligenza;
    private JTextField txtBonusFede;
    private JTextField txtBonusCarisma;
    private JTextField txtBonusFortuna;
    private JTextField txtBonusHpMax;
    private JTextField txtBonusManaMax;

    // Componenti per Oggetto Equipaggiabile: REQUISITI MINIMI
    private JTextField txtReqForza;
    private JTextField txtReqDestrezza;
    private JTextField txtReqCostituzione;
    private JTextField txtReqIntelligenza;
    private JTextField txtReqFede;
    private JTextField txtReqCarisma;
    private JTextField txtReqFortuna;
    private JTextField txtReqHpMax;
    private JTextField txtReqManaMax;

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

        // Ritorna alla Regia
        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAttuale.dispose();
                frameChiamante.setVisible(true);
            }
        });

        // Salvataggio Consumabile
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

        // Salvataggio Equipaggiamento (Bonus + Requisiti)
        aggiungiEquipaggiamentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = txtNomeEquipaggiamento.getText();
                    int costo = parseIntSicuro(txtCostoEquipaggiamento.getText(), "Costo");

                    // Estrazione dei Bonus
                    int bForz = parseIntSicuro(txtBonusForza.getText(), "Bonus Forza");
                    int bDes = parseIntSicuro(txtBonusDestrezza.getText(), "Bonus Destrezza");
                    int bCos = parseIntSicuro(txtBonusCostituzione.getText(), "Bonus Costituzione");
                    int bIntell = parseIntSicuro(txtBonusIntelligenza.getText(), "Bonus Intelligenza");
                    int bFede = parseIntSicuro(txtBonusFede.getText(), "Bonus Fede");
                    int bCar = parseIntSicuro(txtBonusCarisma.getText(), "Bonus Carisma");
                    int bFort = parseIntSicuro(txtBonusFortuna.getText(), "Bonus Fortuna");
                    int bHpMax = parseIntSicuro(txtBonusHpMax.getText(), "Bonus HpMax");
                    int bManaMax = parseIntSicuro(txtBonusManaMax.getText(), "Bonus ManaMax");

                    // Estrazione dei Requisiti Minimi
                    int rForz = parseIntSicuro(txtReqForza.getText(), "Requisito Forza");
                    int rDes = parseIntSicuro(txtReqDestrezza.getText(), "Requisito Destrezza");
                    int rCos = parseIntSicuro(txtReqCostituzione.getText(), "Requisito Costituzione");
                    int rIntell = parseIntSicuro(txtReqIntelligenza.getText(), "Requisito Intelligenza");
                    int rFede = parseIntSicuro(txtReqFede.getText(), "Requisito Fede");
                    int rCar = parseIntSicuro(txtReqCarisma.getText(), "Requisito Carisma");
                    int rFort = parseIntSicuro(txtReqFortuna.getText(), "Requisito Fortuna");
                    int rHpMax = parseIntSicuro(txtReqHpMax.getText(), "Requisito HpMax");
                    int rManaMax = parseIntSicuro(txtReqManaMax.getText(), "Requisito ManaMax");

                    // Chiamata al Controller espanso
                    controller.creaNuovoEquipaggiamento(nome, costo,
                            bCos, bForz, bDes, bIntell, bFede, bCar, bFort, bHpMax, bManaMax,
                            rCos, rForz, rDes, rIntell, rFede, rCar, rFort, rHpMax, rManaMax);

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
     * Azzera i campi testuali numerici (sia Bonus che Requisiti) della scheda "Nuovo Equipaggiamento".
     */
    private void impostaZeriEquip() {
        txtCostoEquipaggiamento.setText("0");

        txtBonusForza.setText("0");
        txtBonusDestrezza.setText("0");
        txtBonusCostituzione.setText("0");
        txtBonusIntelligenza.setText("0");
        txtBonusFede.setText("0");
        txtBonusCarisma.setText("0");
        txtBonusFortuna.setText("0");
        txtBonusHpMax.setText("0");
        txtBonusManaMax.setText("0");

        txtReqForza.setText("0");
        txtReqDestrezza.setText("0");
        txtReqCostituzione.setText("0");
        txtReqIntelligenza.setText("0");
        txtReqFede.setText("0");
        txtReqCarisma.setText("0");
        txtReqFortuna.setText("0");
        txtReqHpMax.setText("0");
        txtReqManaMax.setText("0");
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