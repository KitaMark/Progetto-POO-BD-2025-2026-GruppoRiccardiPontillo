package model;

import java.util.ArrayList;

public class Classe {
    private String nome;
    private ArrayList<Abilita> abilitaSbloccabili;
    private ArrayList<Oggetto> equipaggiamentoIniziale;

    public Classe(String nome) {
        this.nome = nome;
        abilitaSbloccabili = new ArrayList<>();
        equipaggiamentoIniziale = new ArrayList<>();
    }

    public String getNome() { return nome; }

    public ArrayList<Abilita> getAbilitaSbloccabili() { return abilitaSbloccabili; }

    public ArrayList<Oggetto> getEquipaggiamentoIniziale() { return equipaggiamentoIniziale; }

    public void addAbilita(Abilita abilita) { abilitaSbloccabili.add(abilita); }

    public void addOggetto(Oggetto oggetto) { equipaggiamentoIniziale.add(oggetto); }

    public boolean removeAbilita(Abilita abilita){
       return abilitaSbloccabili.remove(abilita);
    }

    public boolean removeOggetto(Oggetto oggetto){
        if(equipaggiamentoIniziale.contains(oggetto)){
            equipaggiamentoIniziale.remove(oggetto);
            return true;
        }
        return false;
    }


}
