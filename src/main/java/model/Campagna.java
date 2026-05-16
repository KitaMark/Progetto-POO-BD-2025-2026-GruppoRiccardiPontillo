package model;
import java.util.ArrayList;
public class Campagna {
       private String nomeCampagna;
       private int maxGiocatori;
       private boolean isIniziata;
       private ArrayList<Personaggio>  listaPersonaggi;
       private ArrayList<Giocatore> partecipanti;
       private Master master;

         public Campagna(String nomeCampagna, int maxGiocatori, Master master){
                this.nomeCampagna= nomeCampagna;
                this.maxGiocatori=maxGiocatori;
                this.isIniziata= false;
                this.listaPersonaggi= new ArrayList<Personaggio>();
                this.partecipanti= new ArrayList<>(partecipanti);
         }

              public int getMaxGiocatori(){
                  return this.maxGiocatori;
              }

              public boolean getIsIniziata(){
                 return this.isIniziata;
                  }

              public String getMaster(){
                return master.toString(); //ricorda di fare override in master
              }

              public ArrayList<Giocatore> getListaPartecipanti(){
                return partecipanti; //da gestire stampa fuori
              }

               public ArrayList<Personaggio> getListaPersonaggi(){
                   return listaPersonaggi; //gestire stampa
               }

              public void setMaxGiocatori( int nuovoMaxGiocatori){
                    this.maxGiocatori= nuovoMaxGiocatori;
              }

              public void inizia(){
                   this.isIniziata= true;
              } //non è possibile, una volta iniziata la campagna, riportarla allo stato non iniziata.




             }





