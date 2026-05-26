package model;

public class OggettoEquipaggiabile extends Oggetto {
    private Statistica requisiti;
    private Statistica bonus;

    public OggettoEquipaggiabile(String nome, int costo, Statistica requisiti, Statistica bonus) {
        super(costo, nome);
        this.requisiti = new Statistica(requisiti);
        this.bonus = new Statistica(bonus);
    }

    public Statistica getBonus() { return bonus; }
    public Statistica getRequisiti() { return requisiti; }



    @Override
    public String toString() {
        return "Equipaggiabile: " + "\n" + super.toString() +
                String.format("Requisiti:%n") + requisiti.toString() + ("Bonus:%n") + bonus.toString();
    }
}
