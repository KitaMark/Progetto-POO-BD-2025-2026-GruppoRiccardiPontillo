package model;
import java.util.ArrayList;

public class Master extends Utente{
     private Campagna campagnaAttiva;
     private ArrayList<Personaggio> listaPnG;


        public Master(String userName, String password, String email){
                 super(userName,password,email);
                 campagnaAttiva= null; //si riempie quando chiamiamo il metodo creaCampagna
                 listaPng = new ArrayList<Personaggio>();
          }


   // @Override
    //public Personaggio creaPersonaggio(String nomePersonaggio, Razza razzaScelta, Classe classeScelta, Campagna campagnaPartecipa) {
           //eccezione poichè il master non cres i PG.
    //}

         public void creaCampagna( String nomeCampagna, int maxGiocatori){
                  campagnaAttiva = new Campagna(nomeCampagna, maxGiocatori, this);
                 } //diversamente dalla traccia, non restituisce un riferimento a campagna ma aggiorna un campo interno alla classe

         public void assegnaPuntiStatistica( Personaggio personaggio, int puntiAssegnati){
                   if(//personaggioInCampagna(personaggio)){
                       //assegna punti. metodo di ricerca da definire.
                   }
                   else{
                       throw IllegalArgumentException("personaggio non esistente.")
                   }
                }


         public boolean rimuoviPersonaggio(Personaggio pgDaRimuovere){
             if( (this.campagnaAttiva != null) && (this.campagnaAttiva.getListaPersonaggiPartecipanti().contains(pgDaRimuovere)) ){
                     this.campagnaAttiva.getListaPersonaggiPartecipanti().remove(pgDaRimuovere);
                       return true;
             }
                    return false;
         } //da controllare


         public boolean modificaStatistichePG(Personaggio pg, Statistica statModificate) {
          if ((this.campagnaAttiva != null) && (this.campagnaAttiva.getListaPersonaggiPartecipanti().contains(pg))) {

               pg.getStatisticaBase().sommaStatistiche(statModificate);
               pg.aggiornaStatoPG(); // Un metodo interno al PG che ricalcola statsFinali e Livello, oggetto hp e mana
                    return true;
                       }
                              return false;
                                          } //da controllare

          }

}





