package model;

/**
 * Classe astratta base per la rappresentazione di un oggetto di gioco.
 * Definisce i parametri strutturali comuni quali il costo monetario e il nome.
 */
public abstract class Oggetto {
    protected String nome;
    protected int costo;

    /**
     * Costruttore per l'inizializzazione delle proprietà comuni dell'oggetto.
     *
     * @param costo il costo d'acquisto dell'oggetto espresso in oro.
     * @param nome  il nome identificativo dell'oggetto.
     */
    public Oggetto(int costo, String nome){
        this.costo = costo;
        this.nome = nome;
    }

    /** @return il costo in oro dell'oggetto. */
    public int getCosto() { return costo; }

    /** @return il nome dell'oggetto. */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la rappresentazione in formato stringa delle proprietà dell'oggetto.
     *
     * @return stringa formattata contenente nome e costo.
     */
    @Override
    public String toString() {
        return String.format("Nome: %s%n Costo: %d%n", nome, costo);
    }
}