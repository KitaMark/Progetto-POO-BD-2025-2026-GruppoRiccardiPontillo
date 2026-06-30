package gui;

import controller.Controller;
import model.Personaggio;

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
    private JTabbedPane mainTab;
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
    private JTabbedPane inventarioTabbedPane;
    private JPanel equipaggiabiliPane;
    private JScrollPane equipaggiabiliScrollPane;
    private JTable equipaggiabiliTable;
    private JPanel slotConsumabiliPane;
    private JScrollPane consumabiliPane;
    private JTable consumabiliTable;

    private Controller controller;
    private Personaggio pgAttivo;

    public SchedaPersonaggioGUI(JFrame frameChiamante, Controller controller, boolean isPg, Personaggio pg) {
        super(frameChiamante, "Scheda "+pg.getNome()+ ((isPg)? "" : " - [PnG]"), true);
        this.controller = controller;
        this.pgAttivo = pg;
        super.setContentPane(contentPane);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(false);
        inizializzaDatiPrincipali();
        super.pack();
        super.setVisible(true);


    }

    private void inizializzaDatiPrincipali(){
        nomeTesto.setText(pgAttivo.getNome());
        razzaTesto.setText(pgAttivo.getRazza().toString());
        classeTesto.setText(pgAttivo.getClasse().toString());

        //metodi per inizializzare la JProgressBar
        hpBar.setMinimum(0);
        hpBar.setMaximum(pgAttivo.getStatisticheFinali().getHpMax());
        hpBar.setValue(pgAttivo.getHpCorrenti());
        //sovrascrive il testo standard della progress bar
        hpBar.setStringPainted(true);
        hpBar.setString(pgAttivo.getHpCorrenti() + "/"+pgAttivo.getStatisticheFinali().getHpMax());
        //rende la barra puramente estetica
        hpBar.setEnabled(false);

        manaBar.setMinimum(0);
        manaBar.setMaximum(pgAttivo.getStatisticheFinali().getManaMax());
        manaBar.setValue(pgAttivo.getManaCorrente());
        manaBar.setStringPainted(true);
        manaBar.setString(pgAttivo.getManaCorrente()+"/"+pgAttivo.getStatisticheFinali().getManaMax());
        manaBar.setEnabled(false);

        forzaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getForza()));
        destrezzaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getDestrezza()));
        costituzioneTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getCostituzione()));
        intelligenzaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getIntelligenza()));
        carismaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getCarisma()));
        fedeTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getFede()));
        fortunaTesto.setText(String.valueOf(pgAttivo.getStatisticheFinali().getFortuna()));
        puntiTesto.setText(String.valueOf(pgAttivo.getPuntiStatistica()));
    }
}
