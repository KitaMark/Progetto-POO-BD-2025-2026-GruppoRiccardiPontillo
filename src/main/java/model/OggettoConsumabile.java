package model;

/**
 * Rappresenta un oggetto di tipo consumabile (es. pozioni).
 * Estende la classe Oggetto introducendo i valori di ripristino istantaneo di HP e Mana.
 */
public class OggettoConsumabile extends Oggetto {
    private int ripristinoHP;
    private int ripristinoMana;

    /**
     * Crea un nuovo oggetto consumabile impostandone costi, identificativi ed effetti di recupero.
     *
     * @param nome  il nome del consumabile.
     * @param costo il costo in oro dell'oggetto.
     * @param hp    la quantità di punti ferita ripristinati all'uso.
     * @param mana  la quantità di punti mana ripristinati all'uso.
     */
    public OggettoConsumabile(String nome, int costo, int hp, int mana){
        super(costo, nome);
        this.ripristinoHP = hp;
        this.ripristinoMana = mana;
    }

    /**
     * Costruttore utilizzato dal DAO per il caricamento dei dati dal database.
     *
     * @param id             l'identificativo univoco dell'oggetto nel database.
     * @param nome           il nome del consumabile.
     * @param costo          il costo d'acquisto espresso in oro.
     * @param tipo           la stringa che definisce la tipologia dell'oggetto (es. 'Consumabile').
     * @param ripristinoHp   i punti ferita (HP) che l'oggetto ripristina quando utilizzato.
     * @param ripristinoMana i punti mana che l'oggetto ripristina quando utilizzato.
     */
    public OggettoConsumabile(int id, String nome, int costo, String tipo,
                              int ripristinoHp, int ripristinoMana) {
        super(id, nome, costo, tipo);
        this.ripristinoHP = ripristinoHp;
        this.ripristinoMana = ripristinoMana;
    }

    /** @return la quantità di punti ferita ripristinati dall'oggetto. */
    public int getRipristinoHP() { return ripristinoHP; }

    /** @return la quantità di punti mana ripristinati dall'oggetto. */
    public int getRipristinoMana() { return ripristinoMana; }

}