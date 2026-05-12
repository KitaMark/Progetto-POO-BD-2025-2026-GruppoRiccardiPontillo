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
              Statistica statConsumabili = pg.getStatisticheFinali();//accediamo a stat hp e mana

                int nuoviHp = statConsumabili.getHpCorrenti() + this.ripristinoHp;

                 if (nuoviHp > statConsumabili.getMaxHp()){

                      nuoviHp = statConsumabili.getMaxHp();
                    }

                      statConsumabili.setHpCorrenti(nuoviHp);

                 int nuovoMana = statConsumabili.getManaCorrenti() + this.ripristinoMana;

                 if (nuovoMana > statConsumabili.getMaxMana()){

                      nuovoMana = statConsumabili.getMaxMana();
                  }

                      statConsumabili.setManaCorrenti(nuovoMana);

                                 this.quantita--; // Riduciamo di 1 l'uso

                             if (this.quantita <= 0) {
                               // Rimuoviamo l'oggetto solo se le cariche sono finite
                              pg.getInventario().remove(this);
                              }
                                     pg.aggiornaStatoPG();
                                }

}
