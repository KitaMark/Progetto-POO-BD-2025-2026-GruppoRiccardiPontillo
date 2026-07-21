package gui;

import controller.Controller;
import model.Campagna;
import model.Giocatore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Rappresenta l'interfaccia grafica principale (Dashboard) riservata agli utenti
 * con ruolo di {@link Giocatore}.
 * <p>
 * Mostra all'utente l'elenco delle campagne a cui partecipa e gli permette di
 * iscriversi a nuove avventure tramite un sistema a schede. Implementa una logica di
 * smistamento: quando un giocatore accede a una campagna, la schermata valuta se
 * possiede già un personaggio. In caso negativo, lo reindirizza alla creazione del PG;
 * in caso positivo, apre la scheda di gioco.
 * </p>
 *
 * @author Riccardi Carmine
 * @author Pontillo Salvatore
 */
public class GiocatoreGUI {

    // COMPONENTI GRAFICI GLOBALI
    private JPanel mainPanel;
    private JLabel benvenutoGiocatore;
    private JButton LogoutButton;
    private JTabbedPane tabbedPane1;

    // COMPONENTI TAB 1: "Le mie Campagne"
    private JTable tableCampagnaGiocatore;
    private JButton entraButton;

    // COMPONENTI TAB 2: "Campagne Disponibili"
    private JTable tableCampagneDisponibili;
    private JButton btnIscriviti;

    /** Il Controller di riferimento per delegare le operazioni di business. */
    private Controller controller;

    /**
     * Costruisce l'interfaccia della Dashboard del Giocatore, inizializza le tabelle
     * delle campagne e configura gli ascoltatori di eventi per l'interazione.
     *
     * @param controller Il {@link Controller} che orchestra le chiamate di sistema.
     */
    public GiocatoreGUI(Controller controller) {
        this.controller = controller;

        JFrame frame = new JFrame("Dashboard Giocatore - " + controller.getUtenteAttivo().getUsername());
        frame.setContentPane(getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Impostiamo una dimensione pulita e leggibile
        frame.setLocationRelativeTo(null); // Centra la finestra nello schermo

        benvenutoGiocatore.setText("Benvenuto, " + controller.getUtenteAttivo().getUsername() + "! [Giocatore]");

        // Inizializza entrambe le tabelle all'avvio
        inizializzaTabellaMieCampagne();
        inizializzaTabellaCampagneDisponibili();


        LogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(frame,
                        "Vuoi davvero effettuare il logout?", "Conferma Uscita",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (conferma == JOptionPane.YES_OPTION) {
                    controller.logout();
                    frame.dispose();
                    Home.main(null);
                }
            }
        });


        entraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tableCampagnaGiocatore.getSelectedRow();

                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona una campagna per entrarvi.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeCampagnaSelezionata = tableCampagnaGiocatore.getValueAt(rigaSelezionata, 0).toString();
                Campagna c = controller.cercaCampagna(nomeCampagnaSelezionata);

                if (controller.getRazzePerCampagna(c).isEmpty() || controller.getClassiPerCampagna(c).isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Il master non ha ancora definito le classi " +
                                    "e le razze per questa campagna. Riprova più tardi.",
                            "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    boolean haGiaIlPersonaggio = controller.recuperaDatiCampagna(nomeCampagnaSelezionata);
                    Giocatore giocatore = (Giocatore) controller.getUtenteAttivo();

                    frame.dispose();

                    if (!haGiaIlPersonaggio) {
                        new CreaPgGUI(controller, nomeCampagnaSelezionata, frame);
                    } else {
                        new CampagnaGiocatoreGUI(controller, giocatore, nomeCampagnaSelezionata, frame);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Errore nel caricamento: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        btnIscriviti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = tableCampagneDisponibili.getSelectedRow();

                if (riga == -1) {
                    JOptionPane.showMessageDialog(frame, "Seleziona una campagna a cui iscriverti.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nomeCampagna = tableCampagneDisponibili.getValueAt(riga, 0).toString();

                int conferma = JOptionPane.showConfirmDialog(frame,
                        "Vuoi iscriverti alla campagna \"" + nomeCampagna + "\"?", "Conferma Iscrizione",
                        JOptionPane.YES_NO_OPTION);

                if (conferma != JOptionPane.YES_OPTION) return;

                try {
                    controller.iscrivitiCampagna(nomeCampagna);
                    JOptionPane.showMessageDialog(frame, "Ti sei iscritto con successo alla campagna!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                    // Ricarica automaticamente le tabelle senza dover riaprire la GUI
                    inizializzaTabellaMieCampagne();
                    inizializzaTabellaCampagneDisponibili();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Errore Iscrizione", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * Inizializza la tabella del primo Tab, mostrando solo le campagne a cui
     * il giocatore è attualmente iscritto.
     */
    private void inizializzaTabellaMieCampagne() {
        String[] colonne = {"Nome Campagna", "Max Giocatori", "Stato"};

        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCampagnaGiocatore.setModel(model);
        tableCampagnaGiocatore.getTableHeader().setReorderingAllowed(false);
        tableCampagnaGiocatore.getTableHeader().setResizingAllowed(false);

        Giocatore giocatore = (Giocatore) controller.getUtenteAttivo();

        if (giocatore.getListaPartecipazioni() != null) {
            for (Campagna campagna : giocatore.getListaPartecipazioni().keySet()) {
                String stato = campagna.isIniziata() ? "Iniziata" : "Non Iniziata";
                model.addRow(new Object[]{
                        campagna.getNome(),
                        campagna.getMaxGiocatori(),
                        stato
                });
            }
        }
    }

    /**
     * Inizializza la tabella del secondo Tab, filtrando dinamicamente il catalogo
     * globale per mostrare solo le campagne "Non Iniziate" e a cui il giocatore
     * non ha ancora effettuato l'iscrizione.
     */
    private void inizializzaTabellaCampagneDisponibili() {
        String[] colonne = {"Nome Campagna", "Master", "Max Giocatori"};

        DefaultTableModel model = new DefaultTableModel(null, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCampagneDisponibili.setModel(model);
        tableCampagneDisponibili.getTableHeader().setReorderingAllowed(false);
        tableCampagneDisponibili.getTableHeader().setResizingAllowed(false);

        Giocatore giocatore = (Giocatore) controller.getUtenteAttivo();
        Map<Campagna, model.Master> tutteLeCampagne = controller.getListaCampagne();

        for (Map.Entry<Campagna, model.Master> entry : tutteLeCampagne.entrySet()) {
            Campagna campagna = entry.getKey();

            // Esclude le campagne già avviate
            if (campagna.isIniziata()) continue;

            // Esclude le Campagne a cui il giocatore è già iscritto
            boolean giaIscritto = giocatore.getListaPartecipazioni() != null
                    && giocatore.getListaPartecipazioni().containsKey(campagna);
            if (giaIscritto) continue;

            model.addRow(new Object[]{
                    campagna.getNome(),
                    entry.getValue().getUsername(),
                    campagna.getMaxGiocatori()
            });
        }
    }

    /**
     * Restituisce il pannello contenitore principale della Dashboard.
     *
     * @return Il {@link JPanel} utilizzato dal frame per visualizzare i contenuti.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
}