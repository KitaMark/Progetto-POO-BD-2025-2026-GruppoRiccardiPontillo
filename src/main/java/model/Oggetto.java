package model;

/**
 * Classe astratta base per la rappresentazione di un oggetto di gioco.
 * Definisce i parametri strutturali comuni quali il costo monetario e il nome.
 */
public abstract class Oggetto {
    private int id;//identificativo nel db
    private String nome;
    private int costo;
    private String tipo; // 'Equipaggiamento' o 'Consumabile'
    private boolean equipaggiato;
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


    /**
     *  costruttore per il dao
     */
    public Oggetto(int id, String nome, int costo, String tipo) {
        this.id = id;
        this.nome = nome;
        this.costo = costo;
        this.tipo = tipo;
        this.equipaggiato = false;
    }


    /** @return il costo in oro dell'oggetto. */
    public int getCosto() { return costo; }

    /** @return il nome dell'oggetto. */
    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isEquipaggiato() {
        return equipaggiato;
    }

    public void setEquipaggiato(boolean equipaggiato) {
        this.equipaggiato = equipaggiato;
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

    @Override
    public boolean equals(Object o) {
        // Se è esattamente lo stesso oggetto in memoria, è uguale
        if (this == o) return true;

        // Se l'altro oggetto è nullo o di una classe diversa, non è uguale
        if (o == null || getClass() != o.getClass()) return false;

        //cast a Oggetto
        Oggetto oggetto = (Oggetto) o;

        // Due oggetti sono la stessa cosa se hanno lo stesso Identificativo (id)
        return this.id == oggetto.id;
    }

    @Override
    public int hashCode() {
        // Genera un codice univoco basato sull'ID dell'oggetto
        return java.util.Objects.hash(id);
    }
}