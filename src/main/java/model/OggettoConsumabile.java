package model;

public class OggettoConsumabile extends Oggetto{
      private int quantita;
      private int ripristinoHp;
      private int ripristinoMana;


          public OggettoConsumabile(String nomeOggetto, int costo, Statistica bonus, int quantita, int ripristinoHp, int ripristinoMana){
                  super(nomeOggetto, costo, bonus);

                   this.quantita= quantita; //chiedere se quantita da inizializzare a 1
                   this.ripristinoHp= ripristinoHp;
                   this.ripristinoMana= ripristinoMana;
          }

          public void usa(Personaggio pg) {
               pg.getStatisticaBase().sommaStatistiche(this.getBonusStat());

               pg.aggiornaStatoPG();//calcolo stato pg per mostrare i cambiamenti dopo l'uso dell'oggetto

               pg.getInventario().remove(this); // avendo usato l'oggetto esso va rimosso
    }

}
