package model;

public class OggettoEquipaggiabile extends Oggetto{
         private Statistica requisiti;
         private boolean isEquipaggiato;



           public OggettoEquipaggiabile(String nomeOggetto, int costo, Statistica requisiti,  Statistica bonus){
                      super(nomeOggetto, costo, bonus);
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


           }

              public boolean getIsEquipaggiato(){
                    return this.isEquipaggiato;
                  }

              public void setIsEquipaggiato(boolean nuovoIsEquipaggiato){
                  this.isEquipaggiato= nuovoIsEquipaggiato;
              }




}
