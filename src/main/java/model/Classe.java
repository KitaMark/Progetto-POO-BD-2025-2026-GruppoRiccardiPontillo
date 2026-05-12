package model;
import java.util.ArrayList;
public class Classe {
    private ArrayList<Abilita> abilitaSbloccabili;
    private ArrayList<OggettoEquipaggiabile> equipaggiamentoIniziale;


       public Classe(){
             this.abilitaSbloccabili= new ArrayList<>();
             this.equipaggiamentoIniziale= new ArrayList<>();
       }

          public void aggiungiAbilita(Abilita nuovaAbilita){
             this.abilitaSbloccabili.add(nuovaAbilita);
             }

          public void aggiungiEquipaggiamento(OggettoEquipaggiabile nuovoOggetto){
             this.equipaggiamentoIniziale.add(nuovoOggetto);         //vedere se implementare
          }


              public ArrayList<Abilita> getAbilitaSbloccabili() {
                  return abilitaSbloccabili;
                 }

              public ArrayList<OggettoEquipaggiabile> getEquipaggiamentoIniziale() {
                 return equipaggiamentoIniziale;
                }

              public void setAbilitaSbloccabili(ArrayList<Abilita> nuovoAbilitaSbloccabili){
                  this.abilitaSbloccabili= nuovoAbilitaSbloccabili;
              }

              public void setEquipaggiamentoIniziale(ArrayList<OggettoEquipaggiabile> nuovoEquipaggiamentoInizile){
                  this.equipaggiamentoIniziale= nuovoEquipaggiamentoInizile;
              }

}
