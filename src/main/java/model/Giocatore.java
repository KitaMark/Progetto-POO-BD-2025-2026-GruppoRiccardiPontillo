package model;

import java.util.HashMap;

public class Giocatore extends Utente {
    private HashMap<Campagna, Personaggio> listaPartecipazioni;


    public Giocatore(String email, String username, String password) {
        super(email, username, password);
        this.listaPartecipazioni = new HashMap<>();
    }

    //GETTER

    public HashMap<Campagna, Personaggio> getListaPartecipazioni() {
        return listaPartecipazioni;
    }

    //SETTER
    public void setListaPartecipazioni(HashMap<Campagna, Personaggio> listaPartecipazioni) {
        this.listaPartecipazioni = listaPartecipazioni;
    }
}