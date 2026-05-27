package model;

/**
 * Rappresenta un'abilità, incantesimo o potere speciale sbloccabile nel sistema di gioco.
 * Ha una funzione puramente descrittiva e il suo esito narrativo è gestito dal Master.
 */
public class Abilita {
    private String nome;
    private String descrizione;
    private Classe classe;

    /**
     * Crea una nuova abilità associandola a una specifica classe.
     *
     * @param nome        il nome dell'abilità.
     * @param descrizione la descrizione dell'effetto dell'abilità.
     * @param classe      la classe a cui appartiene l'abilità.
     */
    public Abilita(String nome, String descrizione, Classe classe){
        this.nome = nome;
        this.descrizione = descrizione;
        this.classe = classe;
    }

    /** @return il nome dell'abilità. */
    public String getNome() {
        return nome;
    }

    /** @return la descrizione dell'effetto dell'abilità. */
    public String getDescrizione() {
        return descrizione;
    }

    /** @return la classe associata all'abilità. */
    public Classe getClasse() {
        return classe;
    }

    /** @param nome il nuovo nome da impostare. */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /** @param descrizione la nuova descrizione dell'effetto da impostare. */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /** @param classe la nuova classe da associare. */
    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    /**
     * Restituisce la rappresentazione testuale dell'abilità.
     *
     * @return stringa formattata con i dettagli dell'oggetto.
     */
    @Override
    public String toString() {
        return String.format("Abilità: %s%n Effetto: %s%n Classe: %s%n", nome, descrizione, classe);
    }
}