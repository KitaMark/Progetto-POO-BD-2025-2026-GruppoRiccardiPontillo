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


    //GETTER
    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Classe getClasse() {
        return classe;
    }

    //SETTER
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    @Override
    public String toString() {
        return String.format("Abilità: %s%n Effetto: %s%n Classe%s%n", nome, descrizione, classe);
    }
}
