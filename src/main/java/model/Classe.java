package model;
import java.util.ArrayList;
public class Classe {
    private ArrayList<Abilita> abilitaSbloccabili;
    private ArrayList<Oggetto> equipaggiamentoIniziale;


       public Classe(){
             this.abilitaSbloccabili= new ArrayList<>();
             this.equipaggiamentoIniziale= new ArrayList<>();
       }

          public void aggiungiAbilita(Abilita nuovaAbilita){
             this.abilitaSbloccabili.add(nuovaAbilita);
             }

          public void aggiungiEquipaggiamento(Oggetto nuovoOggetto){
             this.equipaggiamentoIniziale.add(nuovoOggetto);         //vedere se implementare
          }


              public ArrayList<Abilita> getAbilitaSbloccabili() {
                  return abilitaSbloccabili;
                 }

               public ArrayList<Oggetto> getEquipaggiamentoIniziale() {
                 return equipaggiamentoIniziale;
                }
}
