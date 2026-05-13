package model;

public class Master extends Utente{
     private Campagna campagnaAttiva;


        public Master(String userName, String password, String email){
                 super(userName,password,email);
                 this.campagnaAttiva= null; //si riempie quando chiamiamo il metodo creaCampagna
          }


   // @Override
    //public Personaggio creaPersonaggio(String nomePersonaggio, Razza razzaScelta, Classe classeScelta, Campagna campagnaPartecipa) {
           //eccezione poichè il master non cres i PG.
    //}

         public Campagna creaCampagna( String nomeCampagna, int maxGiocatori){
                  return  new Campagna(nomeCampagna, maxGiocatori);
                 }

         public void assegnaPuntiStatistica( Personaggio pgRicevente,int puntiAssegnati){
                   pgRicevente.addPuntiStatistica(puntiAssegnati);

                }


         public boolean rimuoviPersonaggio(Personaggio pgDaRimuovere){
             if( (this.campagnaAttiva != null) && (this.campagnaAttiva.getListaPersonaggiPartecipanti().contains(pgDaRimuovere)) ){
                     this.campagnaAttiva.getListaPersonaggiPartecipanti().remove(pgDaRimuovere);
                       return true;
             }
                    return false;
         }


         public boolean modificaStatistichePG(Personaggio pg, Statistica statModificate) {
          if ((this.campagnaAttiva != null) && (this.campagnaAttiva.getListaPersonaggiPartecipanti().contains(pg))) {

               pg.getStatisticaBase().sommaStatistiche(statModificate);
               pg.aggiornaStatoPG(); // Un metodo interno al PG che ricalcola statsFinali e Livello, oggetto hp e mana
                    return true;
                       }
                              return false;
                                          }



          public void cambiaAndamentoCampagna(boolean nuovoAndamento){
               if(campagnaAttiva != null){
                     campagnaAttiva.setIniziata(nuovoAndamento);
               }
          }

}





