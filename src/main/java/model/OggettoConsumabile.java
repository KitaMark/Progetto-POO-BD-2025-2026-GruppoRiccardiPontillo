package model;

public class OggettoConsumabile extends Oggetto {
    private  int ripristinoHP;
    private  int ripristinoMana;


    public OggettoConsumabile(int costo, String nome, int hp, int mana){
        super(costo, nome);
        ripristinoHP = hp;
        ripristinoMana = mana;
    }


    public int getRipristinoMana() {
        return ripristinoMana;
    }

    public int getRipristinoHP() {
        return ripristinoHP;
    }


    @Override
    public String toString() {
        return "Consumabile: " + "\n" + super.toString() +
                String.format("Ripristino hp: %d%n Ripristino mana: %d%n", ripristinoHP, ripristinoMana);
    }
}
