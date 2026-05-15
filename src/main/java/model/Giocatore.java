package model;
import java.util.ArrayList;
public class Giocatore extends Utente{
       ArrayList<Personaggio> listaPG;
       ArrayList<Campagna> listaCampagne;

       public Giocatore(String username, String password, String email){

           super(username,password,email);
           listaPG = new ArrayList<Personaggio>();
           listaCampagne = new ArrayList<Campagna>();
       }

       public void iscrivitiCampagna(Campagna campagna){
           if(campagna == null){
               throw new IllegalArgumentException("Campagna non esistente");
           }
           else{
               listaCampagne.add(campagna);
               campagna.getListaPartecipanti().add(this);
           }
       }


}
