package model;

public class OggettoConsumabile extends Oggetto{
      private int quantita;
      private int ripristinoHp;
      private int ripristinoMana;


          public OggettoConsumabile(String nomeOggetto, int costo, int quantita, int ripristinoHp, int ripristinoMana){
                  super(nomeOggetto, costo);

                   this.quantita= quantita; //chiedere se quantita da inizializzare a 1
                   this.ripristinoHp= ripristinoHp;
                   this.ripristinoMana= ripristinoMana;
          }
}
