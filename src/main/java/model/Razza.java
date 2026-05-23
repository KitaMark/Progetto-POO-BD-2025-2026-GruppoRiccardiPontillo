package model;

public class Razza {
    private String nome;
    private Statistica modificatori;

    //costruttore per i bonus
    public Razza(int costituzione, int forza, int destrezza, int intelligenza,
                 int fede, int carisma, int fortuna, int hpMax, int manaMax, String nome){
        this.nome = nome;
        modificatori = new Statistica(costituzione, forza, destrezza, intelligenza,
         fede, carisma, fortuna, hpMax, manaMax);
    }

    public Razza(String nome, Statistica statistica){
        this.nome = nome;
        this.modificatori = new Statistica(statistica);
    }

    public Statistica getModificatori(){
        return modificatori;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setModificatori(Statistica modificatori) {
        this.modificatori = modificatori;
    }
}
