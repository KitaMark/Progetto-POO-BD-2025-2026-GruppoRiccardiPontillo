package model;

public class Razza {
    private String nome;
    private Statistiche modificatori;

    //costruttore per i bonus
    public Razza(int costituzione, int forza, int destrezza, int intelligenza,
                 int fede, int carisma, int fortuna, int hpMax, int manaMax, String nome){
        this.nome = nome;
        modificatori = new Statistiche(costituzione, forza, destrezza, intelligenza,
         fede, carisma, fortuna, hpMax, manaMax);
    }

    public Razza(String nome, Statistiche statistiche){
        this.nome = nome;
        this.modificatori = new Statistiche(statistiche);
    }

    public Statistiche getModificatori(){
        return modificatori;
    }

    public String getNome() {
        return nome;
    }
}
