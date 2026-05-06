package model;


public class Utente {
      private String userName;
      private String password;
      private String email;

        public Utente(String userName, String password, String email) {
                this.userName = userName;
                this.password = password;
                this.email = email;
        }

           public String getUserName(){
                return this.userName;
           }

           public String getPassword(){
                return this.password;
           }

           public String getEmail(){
                return this.email;
           }

           public void setUserName(String nuovoUserName) {
                this.userName = nuovoUserName;
          }

          public void setPassword(String nuovaPassword){
                this.password= nuovaPassword;
          }


           public Personaggio creaPersonaggio(String nomePersonaggio, Razza razzaScelta, Classe classeScelta, Campagna campagnaPartecipa){
             if((campagnaPartecipa.getIsIniziata() == true) || (campagnaPartecipa.getListaPersonaggiPartecipanti().size() >= campagnaPartecipa.getMaxGiocatori())){
                          //fare eccezzione (penso sia migliore)
                             }

                     Statistica statBase = new Statistica();
                     Personaggio nuovoPG = new Personaggio(nomePersonaggio, razzaScelta, classeScelta,statBase,true, campagnaPartecipa);
                               return nuovoPG;
                      }



    }



