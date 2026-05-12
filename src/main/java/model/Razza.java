package model;

public class Razza {
         private Statistica modificatori;

           public Razza(Statistica modificatori){
                 this.modificatori= modificatori;
           }


           public Statistica getModificatoriRazza(){
                  return this.modificatori;
           }

           public void setModificatoriRazza( Statistica nuoviModificatori){
                   this.modificatori= nuoviModificatori;
           }

}
