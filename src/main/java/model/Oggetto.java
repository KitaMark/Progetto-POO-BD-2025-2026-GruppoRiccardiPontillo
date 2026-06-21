package model;

/**
 * Classe astratta base per la rappresentazione di un oggetto di gioco.
 * Definisce i parametri strutturali comuni quali il costo monetario e il nome.
 */
public abstract class Oggetto {
    private int id;
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
     * Costruttore d'appoggio per l'oggetto creato per il database
     *
     * @param id    l'identificativo univoco dell'oggetto nel database.
     * @param nome  il nome dell'oggetto.
     * @param costo il costo d'acquisto dell'oggetto espresso in oro.
     * @param tipo  la stringa che definisce la tipologia dell'oggetto (es. 'Equipaggiamento' o 'Consumabile').
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

    /** @return l'ID dell'oggetto generato dal database. */
    public int getId() {
        return id;
    }


    /**
     * Imposta l'identificativo univoco dell'oggetto.
     *
     * @param id il nuovo ID da assegnare all'oggetto.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** @return una stringa che rappresenta il tipo (es. "Equipaggiamento" o "Consumabile").*/
    public String getTipo() {
        return tipo;
    }


    /**
     * Imposta la tipologia dell'oggetto.
     *
     * @param tipo la stringa che definisce il tipo dell'oggetto.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /** @return {@code true} se l'oggetto è equipaggiato, {@code false} se si trova solo nell'inventario. */
    public boolean isEquipaggiato() {
        return equipaggiato;
    }


    /**
     * Imposta lo stato di equipaggiamento dell'oggetto.
     *
     * @param equipaggiato {@code true} per equipaggiare l'oggetto, {@code false} per riporlo.
     */
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



    /**
     * Confronta questo oggetto con un altro per stabilirne l'uguaglianza-.
     * <p>
     * Due istanze di {@link Oggetto} sono considerate identiche se condividono lo stesso
     * identificativo univoco (ID) proveniente dal database, indipendentemente dall'istanza in memoria.
     * </p>
     *
     * @param o L'oggetto da confrontare.
     * @return {@code true} se gli oggetti condividono lo stesso ID, {@code false} altrimenti.
     */
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

    /**
     * Genera un codice hash univoco per l'oggetto, basato esclusivamente sul suo identificativo (ID).
     * <p>
     * Questo metodo è strettamente necessario per garantire il corretto posizionamento
     * e recupero dell'oggetto all'interno di strutture dati basate su hash, come le {@code HashMap}.
     * </p>
     *
     * @return il codice hash calcolato a partire dall'ID.
     */
    @Override
    public int hashCode() {
        // Genera un codice univoco basato sull'ID dell'oggetto
        return java.util.Objects.hash(id);
    }
}