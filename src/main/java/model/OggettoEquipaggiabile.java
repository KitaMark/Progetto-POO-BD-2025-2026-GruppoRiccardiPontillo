package model;

/**
 * Rappresenta un oggetto equipaggiabile dal personaggio (es. armi o armature).
 * Memorizza i requisiti di attributo minimi richiesti per l'utilizzo e i bonus statistici applicati.
 */
public class OggettoEquipaggiabile extends Oggetto {
    private Statistica requisiti;
    private Statistica bonus;

    /**
     * Crea un nuovo oggetto equipaggiabile eseguendo una copia profonda delle statistiche passate.
     *
     * @param nome      il nome dell'equipaggiabile.
     * @param costo     il costo in oro dell'oggetto.
     * @param requisiti le soglie minime di attributo necessarie per equipaggiarlo.
     * @param bonus     i modificatori statistici applicati al personaggio quando indossato.
     */
    public OggettoEquipaggiabile(String nome, int costo, Statistica requisiti, Statistica bonus) {
        super(costo, nome);
        this.requisiti = new Statistica(requisiti);
        this.bonus = new Statistica(bonus);
    }

    /**
     *costruttore per il dao
     */
    public OggettoEquipaggiabile(int id, String nome, int costo, String tipo,
                                 Statistica requisiti, Statistica bonus) {
        super(id, nome, costo, tipo);
        this.requisiti = requisiti;
        this.bonus = bonus;
    }

    /** @return l'oggetto Statistica contenente i bonus applicati. */
    public Statistica getBonus() { return bonus; }

    /** @return l'oggetto Statistica contenente i requisiti minimi di utilizzo. */
    public Statistica getRequisiti() { return requisiti; }

    /**
     * Restituisce la descrizione testuale dettagliata dell'oggetto, inclusi requisiti e modificatori associati.
     *
     * @return stringa strutturata dell'equipaggiabile in formato multilinea.
     */
    @Override
    public String toString() {
        return "Equipaggiabile: " + "\n" + super.toString() +
                String.format("Requisiti:%n") + requisiti.toString() + ("Bonus:%n") + bonus.toString();
    }
}