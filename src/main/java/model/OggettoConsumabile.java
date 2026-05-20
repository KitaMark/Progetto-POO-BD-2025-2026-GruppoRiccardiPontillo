package model;

public class OggettoConsumabile extends Oggetto{
      private int quantita;
      private int ripristinoHp;
      private int ripristinoMana;


          public OggettoConsumabile(String nomeOggetto, int costo,  int quantita, int ripristinoHp, int ripristinoMana){
                  super(nomeOggetto, costo);

                   this.quantita= quantita; //chiedere se quantita da inizializzare a 1
                   this.ripristinoHp= ripristinoHp;
                   this.ripristinoMana= ripristinoMana;
          }

           //vedere se va bene
           public void usa(Personaggio pg) {
               Statistica statBase = pg.getStatisticaBase();
               Statistica statFinali = pg.getStatisticheFinali();

               int nuoviHp = statBase.getHpCorrenti() + this.ripristinoHp;
               if (nuoviHp > statFinali.getMaxHp()){
                   nuoviHp = statFinali.getMaxHp();
               }
               statBase.setHpCorrenti(nuoviHp);

               int nuovoMana = statBase.getManaCorrenti() + this.ripristinoMana;
               if (nuovoMana > statFinali.getMaxMana()){
                   nuovoMana = statFinali.getMaxMana();
               }
               statBase.setManaCorrenti(nuovoMana);

               this.quantita--;
               if (this.quantita <= 0) {
                   pg.getInventario().remove(this);
               }

               pg.aggiornaStatoPG();
           }

}
