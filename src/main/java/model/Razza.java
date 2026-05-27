package model;

/**
 * Rappresenta la razza biologica di un personaggio (es. Elfo, Nano).
 * Determina i modificatori attitudinali innati applicati permanentemente alle statistiche base.
 */
public class Razza {
    private String nome;
    private Statistica modificatori;

    /**
     * Crea una razza specificando singolarmente i valori numerici di ciascun modificatore di tratto.
     *
     * @param costituzione bonus o malus alla costituzione.
     * @param forza        bonus o malus alla forza.
     * @param destrezza    bonus o malus alla destrezza.
     * @param intelligenza bonus o malus all'intelligenza.
     * @param fede         bonus o malus alla fede.
     * @param carisma      bonus o malus al carisma.
     * @param fortuna      bonus o malus alla fortuna.
     * @param hpMax        modificatore alla salute massima.
     * @param manaMax      modificatore alla riserva di mana massima.
     * @param nome         il nome identificativo della razza.
     */
    public Razza(int costituzione, int forza, int destrezza, int intelligenza,
                 int fede, int carisma, int fortuna, int hpMax, int manaMax, String nome){
        this.nome = nome;
        this.modificatori = new Statistica(costituzione, forza, destrezza, intelligenza,
                fede, carisma, fortuna, hpMax, manaMax);
    }

    /**
     * Crea una razza associando direttamente un oggetto Statistica esistente come modificatore.
     *
     * @param nome       il nome della razza.
     * @param statistica l'oggetto Statistica da cui copiare i modificatori.
     */
    public Razza(String nome, Statistica statistica){
        this.nome = nome;
        this.modificatori = new Statistica(statistica);
    }

    /** @return i modificatori statistici innati associati alla razza. */
    public Statistica getModificatori(){
        return modificatori;
    }

    /** @return il nome della razza. */
    public String getNome() {
        return nome;
    }

    /** @param nome il nuovo nome da impostare per la razza. */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /** @param modificatori il nuovo oggetto Statistica da impostare come modificatore di razza. */
    public void setModificatori(Statistica modificatori) {
        this.modificatori = modificatori;
    }
}