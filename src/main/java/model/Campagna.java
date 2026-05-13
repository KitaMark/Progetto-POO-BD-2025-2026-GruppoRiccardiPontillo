package model;
import java.util.ArrayList;
public class Campagna {
       private String nomeCampagna;
       private int maxGiocatori;
       private boolean isIniziata;
       private ArrayList<Personaggio>  listaPersonaggiPartecipanti;

         public Campagna(String nomeCampagna, int maxGiocatori){
                this.nomeCampagna= nomeCampagna;
                this.maxGiocatori=maxGiocatori;
                this.isIniziata= false;
                this.listaPersonaggiPartecipanti= new ArrayList<>();
         }

              public int getMaxGiocatori(){
                  return this.maxGiocatori;
              }

              public boolean getIsIniziata(){
                 return this.isIniziata;
                  }

              public void setMaxGiocatori( int nuovoMaxGiocatori){
                    this.maxGiocatori= nuovoMaxGiocatori;
              }

              public void setIniziata( boolean nuovoIsIniziata){
                   this.isIniziata= nuovoIsIniziata;
              }

              public ArrayList<Personaggio> getListaPersonaggiPartecipanti(){
                     return listaPersonaggiPartecipanti;
                }


             public void aggiungiPG(Personaggio pgDaAggiungere){
                 if((this.isIniziata==false) && (this.listaPersonaggiPartecipanti.size() < this.maxGiocatori)) {
                     listaPersonaggiPartecipanti.add(pgDaAggiungere);
                 }
             }

             public boolean Istroppi(){
                  return this.listaPersonaggiPartecipanti.size()>= maxGiocatori;
                }


             public void concludiCampagna(){
               if(this.isIniziata== true){
                    this.isIniziata= false;   //campagna finita
               }

             }




}
