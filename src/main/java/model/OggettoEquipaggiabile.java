package model;

public class OggettoEquipaggiabile extends Oggetto{
         private Statistica requisiti;
         private boolean isEquipaggiato;
         private Statistica bonus;


           public OggettoEquipaggiabile(String nomeOggetto, int costo, Statistica requisiti,  Statistica bonus){
                      super(nomeOggetto, costo);
                      this.isEquipaggiato= false;

               this.requisiti = new Statistica(
                       requisiti.getForza(),
                       requisiti.getDestrezza(),
                       requisiti.getCostituzione(),
                       requisiti.getIntelligenza(),
                       requisiti.getFede(),
                       requisiti.getCarisma(),
                       requisiti.getFortuna()
               );

               this.bonus= bonus;


           }

            public Statistica getBonusStat(){
                 return this.bonus;
                       }

              public boolean getIsEquipaggiato(){
                    return this.isEquipaggiato;
                  }

              public void setIsEquipaggiato(boolean nuovoIsEquipaggiato){
                  this.isEquipaggiato= nuovoIsEquipaggiato;
              }

              public void setBonunStat(Statistica nuovoBonus){
                       this.bonus= nuovoBonus;
                     }





}
