package gui;

import controller.Controller;

import javax.swing.*;

public class SchedaPersonaggioGUI extends JDialog {
    private JPanel contentPane;
    private JLabel forzaLabel;
    private JLabel destrezzaLabel;
    private JLabel costituzioneLabel;
    private JLabel fortunaLabel;
    private JLabel fedeLabel;
    private JLabel carismaLabel;
    private JLabel intelligenzaLabel;
    private JProgressBar hpBar;
    private JProgressBar manaBar;
    private JLabel razzaLabel;
    private JLabel classeLabel;
    private JLabel nomeTesto;
    private JLabel razzaTesto;
    private JLabel classeTesto;
    private JLabel forzaTesto;
    private JLabel destrezzaTesto;
    private JTabbedPane tabbedPane1;
    private JLabel costituzioneTesto;
    private JLabel intelligenzaTesto;
    private JLabel carismaTesto;
    private JLabel fedeTesto;
    private JLabel fortunaTesto;
    private JLabel hpLabel;
    private JLabel manaLabel;
    private JPanel statPane;
    private JPanel inventarioPane;
    private JLabel nomeLabel;
    private JPanel abilitaPane;
    private JTable table1;
    private JLabel puntiStatisticaLabel;
    private JLabel puntiTesto;
    private JTabbedPane tabbedPane2;
    private JPanel equipaggiabiliPane;
    private JScrollPane equipaggiabiliScrollPane;
    private JTable equipaggiabiliTable;
    private JPanel slotConsumabiliPane;
    private JScrollPane consumabiliPane;
    private JTable consumabiliTable;

    private Controller controller;

    public SchedaPersonaggioGUI(JFrame frameChiamante, Controller controller, boolean isPg) {
        super(frameChiamante, "Scheda personaggio", true);
        this.controller = controller;

        super.setContentPane(contentPane);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(false);
        inserisciDati();
        super.pack();
        super.setVisible(true);


    }

    private void inserisciDati(){}
}
