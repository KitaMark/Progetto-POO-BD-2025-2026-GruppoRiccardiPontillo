package model;

public class Oggetto {
       private String nomeOggetto;
       private int costo;

         public Oggetto(String nomeOggetto, int costo){
                this.nomeOggetto= nomeOggetto;
                this.costo= costo;
         }


           public String getNomeOggetto(){
               return this.nomeOggetto;
           }

           public int getCosto(){
               return this.costo;
           }

           public void setNomeOggetto(String nuovoNomeOggetto){
             this.nomeOggetto= nuovoNomeOggetto;
           }

           public void setCosto(int nuovoCosto){
               this.costo= nuovoCosto;
           }


}
