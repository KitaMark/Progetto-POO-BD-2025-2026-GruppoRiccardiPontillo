package model;

public class OggettoConsumabile extends Oggetto {
    private final int ripristinoHP;
    private final int ripristinoMana;
    //tolto quantita, la responsabilità è di personaggio.


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

    public void usa(Personaggio personaggio) {
        personaggio.ripristinaHP(ripristinoHP);
        personaggio.ripristinaMana(ripristinoMana);
    }

    @Override
    public String toString() {
        return "Consumabile: " + "\n" + super.toString() +
                String.format("Ripristino hp: %d%n Ripristino mana: %d%n", ripristinoHP, ripristinoMana);
    }
}
