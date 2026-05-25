package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home {
    private JPanel mainPanel;
    private JLabel username;
    private JLabel email;
    private JLabel password;
    private JTextField campoUsername;
    private JTextField campoEmail;
    private JRadioButton masterRadioButton;
    private JRadioButton giocatoreRadioButton;
    private JLabel RuoloUtente;
    private JButton registrati;
    private JButton Accedi;
    private JPasswordField campoPassword;
    private static JFrame frameHome;
    private Controller controller;
    private JFrame frameAttuale;

    public static void main(String[] args) {
        frameHome = new JFrame("GestionaleGDRLogin");
        frameHome.setContentPane(new Home(frameHome).mainPanel);
        frameHome.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameHome.pack();
        frameHome.setVisible(true);


    }

    public Home(JFrame frame) {
        controller = new Controller();
        this.frameAttuale = frame;

        Accedi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = campoUsername.getText().trim();
                String email = campoEmail.getText().trim();
                String password = new String(campoPassword.getPassword());

                String identificativo = "";

                //visto che per accedere basta o la mail o l'username con il controllo diciamo
                // all'identificativo che campo usare
                if (username.isEmpty() == false) {
                    identificativo = username;
                } else if (email.isEmpty() == false) {
                    identificativo = email;
                }

                try {
                    Utente utenteLoggato = controller.faiLogin(identificativo, password);
                    JOptionPane.showMessageDialog(null, "Benvenuto, " + utenteLoggato.getUsername() + "!", "Login avvenuto con successo", JOptionPane.INFORMATION_MESSAGE);

                    frameAttuale.dispose(); //vedere se distruggere completamente o renderlo invisibile per poi riaprirlo


                    if (utenteLoggato instanceof Master) {
                        JFrame masterFrame = new JFrame("Dashboard Master - " + utenteLoggato.getUsername());
                        MasterGUI masterGUI = new MasterGUI(controller, (Master) utenteLoggato, masterFrame);

                        masterFrame.setContentPane(masterGUI.getMainPanel());
                        masterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        masterFrame.setSize(800, 600); // Imposta una grandezza iniziale comoda
                        masterFrame.setLocationRelativeTo(null); // Centra sullo schermo
                        masterFrame.setVisible(true);
                    } else {
                        JFrame giocatoreFrame = new JFrame("Dashboard Giocatore - " + utenteLoggato.getUsername());
                        GiocatoreGUI giocatoreGUI = new GiocatoreGUI(controller, (Giocatore) utenteLoggato, giocatoreFrame);
                        giocatoreFrame.setContentPane(giocatoreGUI.getMainPanel());
                        giocatoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        giocatoreFrame.setSize(800, 600);
                        giocatoreFrame.setLocationRelativeTo(null); // Centra lo schermo
                        giocatoreFrame.setVisible(true); // Rende visibile la finestra                    }
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore di Accesso", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        registrati.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username= campoUsername.getText().trim(); //uso trim per eliminare spazi e evitare bug ed errori evitare errori nel db
                String password= new String(campoPassword.getPassword()); //poichè fatto con jPassword
                String email= campoEmail.getText().trim();

                boolean isMaster= masterRadioButton.isSelected();

                if ((masterRadioButton.isSelected() ==false) && (giocatoreRadioButton.isSelected() ==false)) {
                    JOptionPane.showMessageDialog(null, "Seleziona un tipo di utente (Master o Giocatore) per registrarti.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return; // Blocca l'esecuzione
                }

                try {

                    controller.registraUtente(username, password, email, isMaster);
                    JOptionPane.showMessageDialog(null, "Registrazione effettuata! Ora puoi fare il Login.", "Successo", JOptionPane.INFORMATION_MESSAGE);


                }catch (Exception ex) {
                    // Cattura le eccezioni del controller
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Errore di Registrazione", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }


}
