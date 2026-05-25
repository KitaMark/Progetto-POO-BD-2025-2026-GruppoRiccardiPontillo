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

    //GETTER
    public String getNome() { return nome; }

    public ArrayList<Abilita> getAbilitaSbloccabili() { return abilitaSbloccabili; }

    public ArrayList<Oggetto> getEquipaggiamentoIniziale() { return equipaggiamentoIniziale; }

    //SETTER
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAbilitaSbloccabili(ArrayList<Abilita> abilitaSbloccabili) {
        this.abilitaSbloccabili = abilitaSbloccabili;
    }

    public void setEquipaggiamentoIniziale(ArrayList<Oggetto> equipaggiamentoIniziale) {
        this.equipaggiamentoIniziale = equipaggiamentoIniziale;
    }

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
