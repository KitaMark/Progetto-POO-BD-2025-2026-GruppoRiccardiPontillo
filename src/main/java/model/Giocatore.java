package model;

public class Giocatore extends Utente{

       public Giocatore(String userName, String password, String email){
                  super(userName,password,email);
       }


       public boolean iscrivitiCampagna( Campagna campagnaScelta){
           return (campagnaScelta.getIsIniziata() == true) || (campagnaScelta.getListaPersonaggiPartecipanti().size() >= campagnaScelta.getMaxGiocatori());
       }


}
