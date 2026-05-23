package model;

public class OggettoEquipaggiabile extends Oggetto {
    private Statistiche requisiti;
    private Statistiche bonus;

    public OggettoEquipaggiabile(String nome, int costo, Statistiche requisiti, Statistiche bonus) {
        super(costo, nome);
        this.requisiti = new Statistiche(requisiti);
        this.bonus = new Statistiche(bonus);
    }

    public Statistiche getBonus() { return bonus; }
    public Statistiche getRequisiti() { return requisiti; }

    public void setRequisiti(Statistiche requisiti) {
        this.requisiti = requisiti;
    }

    public void setBonus(Statistiche bonus) {
        this.bonus = bonus;
    }

    @Override
    public String toString() {
        return "Equipaggiabile: " + "\n" + super.toString() +
                String.format("Requisiti:%n") + requisiti.toString() + ("Bonus:%n") + bonus.toString();
    }
}
