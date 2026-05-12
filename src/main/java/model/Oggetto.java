package model;

public class Oggetto {
       private String nomeOggetto;
       private int costo;
       private Statistica bonus;

         public Oggetto(String nomeOggetto, int costo, Statistica bonus){
                this.nomeOggetto= nomeOggetto;
                this.costo= costo;
                this.bonus= bonus;
         }


           public String getNomeOggetto(){
               return this.nomeOggetto;
           }

           public Statistica getBonusStat(){
           return this.bonus;
               }

           public int getCosto(){
               return this.costo;
           }

           public void setNomeOggetto(String nuovoNomeOggetto){
             this.nomeOggetto= nuovoNomeOggetto;
           }

          public void setBonunStat(Statistica nuovoBonus){
                  this.bonus= nuovoBonus;
                         }

           public void setCosto(int nuovoCosto){
               this.costo= nuovoCosto;
           }


}
