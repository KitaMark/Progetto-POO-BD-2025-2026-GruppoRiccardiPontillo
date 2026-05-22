package model;

public class Abilita {
    private String nome;
    private String descrizione;
    private Classe classe;

    public Abilita(String nome, String descrizione, Classe classe){
        this.nome = nome;
        this.descrizione = descrizione;
        this.classe = classe;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Classe getClasse() {
        return classe;
    }

    @Override
    public String toString() {
        return String.format("Abilità: %s%n Effetto: %s%n Classe%s%n", nome, descrizione, classe);
    }
}
