package model;

public abstract class Oggetto {
    protected String nome;
    protected int costo;

    public Oggetto(int costo, String nome){

        this.costo = costo;
        this.nome = nome;
    }
    //la classe oggetto è modellata per poter operare con polimorfismo sia su
    // oggetti equipaggiabili che consumabili.

    public int getCosto() { return costo; }

    public String getNome() {
        return nome;
    }

    /*public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }
    da valutare
     */

    @Override
    public String toString() {
        return String.format("Nome: %s%n Costo: %d%n", nome, costo);
    }
}
