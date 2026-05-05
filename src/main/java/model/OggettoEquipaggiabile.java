package model;

public class OggettoEquipaggiabile extends Oggetto{
         private Statistica requisiti;
         private boolean equipaggiato;
         private Statistica bonus;


           public OggettoEquipaggiabile(String nomeOggetto, int costo, Statistica requisiti,  Statistica bonus){
                      super(nomeOggetto, costo);
                      this.equipaggiato= false;

               this.requisiti = new Statistica(
                       requisiti.getForza(),
                       requisiti.getDestrezza(),
                       requisiti.getCostituzione(),
                       requisiti.getIntelligenza(),
                       requisiti.getFede(),
                       requisiti.getCarisma(),
                       requisiti.getFortuna()
               );

                      this.bonus=bonus;

           }
}
