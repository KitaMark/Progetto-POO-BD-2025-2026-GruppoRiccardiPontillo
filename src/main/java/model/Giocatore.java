package model;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

public class Giocatore extends Utente {
    private HashMap<Campagna, Personaggio> listaPartecipazioni;


    public Giocatore(String email, String username, String password) {
        super(email, username, password);
        this.listaPartecipazioni = new HashMap<>();
    }

    //GETTER

    public Map<Campagna, Personaggio> getListaPartecipazioni() {
        return Collections.unmodifiableMap(listaPartecipazioni);
    }

   /* //SETTER
    public void setListaPartecipazioni(HashMap<Campagna, Personaggio> listaPartecipazioni) {
        this.listaPartecipazioni = listaPartecipazioni;
    }*/ //da eliminare


}