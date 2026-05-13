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

             public Statistica getRequisiti() {
                    return requisiti;
                      }

               public void setIsEquipaggiato(boolean nuovoIsEquipaggiato){
                  this.isEquipaggiato= nuovoIsEquipaggiato;
              }

              public void setBonunStat(Statistica nuovoBonus){
                       this.bonus= nuovoBonus;
                     }

            public void setRequisiti(Statistica nuovoRequisiti) {
                   this.requisiti = nuovoRequisiti;
                      }


              @Override
               public void controllaEquipaggiamento(Statistica statBasePG) {
              if ((this.isEquipaggiato==true) && (statBasePG.soddisfaRequisito(this.requisiti) == false)) {
                this.isEquipaggiato = false;
                 }
              }



}
