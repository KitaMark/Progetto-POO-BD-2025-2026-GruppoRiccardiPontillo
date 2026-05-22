package model;

import java.util.HashMap;

public class Giocatore extends Utente{
    private HashMap<Campagna, Personaggio> listaPartecipazioni; //include campagne e pg associato del giocatore.

    public Giocatore(String nome, String email, String password){
        super(nome, email, password);
        listaPartecipazioni = new HashMap<>();
    }

    public boolean creaPersonaggio(Classe classe, Razza razza, String nome, Campagna campagna){
        Personaggio pg = new Personaggio(classe, razza, nome, true);
        if(listaPartecipazioni.containsKey(campagna) && listaPartecipazioni.get(campagna) == null){
            listaPartecipazioni.replace(campagna, pg);
            return true;
        }
        return false;
    }

    public void iscrivitiCampagna(Campagna campagna){
        if(campagna == null) throw new IllegalArgumentException("Campagna selezionata non valida.");
        listaPartecipazioni.put(campagna, null);
    }

    //TODO: da definire se gli altri metodi relativi alle operazioni sono da inserire qui o in controller
}
