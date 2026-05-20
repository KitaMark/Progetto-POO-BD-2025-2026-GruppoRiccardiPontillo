package model;
import java.util.ArrayList;

public class Master extends Utente {
    private Campagna campagnaAttiva;
    private ArrayList<Personaggio> listaPnG;

    public Master(String userName, String password, String email) {
        super(userName, password, email);
        this.campagnaAttiva = null;
        this.listaPnG = new ArrayList<Personaggio>();
    }

    public void creaCampagna(String nomeCampagna, int maxGiocatori) {
        this.campagnaAttiva = new Campagna(nomeCampagna, maxGiocatori, this);
    }


    @Override
    public void creaPersonaggio(String nomePG, Razza razzaScelta, Classe classeScelta, Campagna campagnaGiocante) {
        // Il Master non ha il limite di "un solo personaggio", quindi non facciamo quel controllo.

        Statistica statsBase = new Statistica();
        Personaggio nuovoPng= new Personaggio(nomePG, razzaScelta, classeScelta, statsBase, false, campagnaGiocante);

        this.listaPnG.add(nuovoPng);
    }


    public void assegnaPuntiStatistica(Personaggio personaggio, int puntiAssegnati) {
        if (personaggio != null) {
            // logica da definire
        } else {
            throw new IllegalArgumentException("personaggio non esistente."); // Aggiunto 'new'
        }
    }

    public boolean rimuoviPersonaggio(Personaggio pgDaRimuovere) {
        if ((this.campagnaAttiva != null) && (this.campagnaAttiva.getListaPartecipanti().contains(pgDaRimuovere))) {
            this.campagnaAttiva.getListaPartecipanti().remove(pgDaRimuovere);
            return true;
        }
        return false;
    }

    public boolean modificaStatistichePG(Personaggio pg, Statistica statModificate) {
        if ((this.campagnaAttiva != null) && (this.campagnaAttiva.getListaPartecipanti().contains(pg))) {
            pg.getStatisticaBase().sommaStatistiche(statModificate);
            pg.aggiornaStatoPG();
            return true;
        }
        return false;
    }
}