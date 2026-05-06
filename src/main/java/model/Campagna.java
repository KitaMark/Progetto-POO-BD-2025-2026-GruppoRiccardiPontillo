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




}
