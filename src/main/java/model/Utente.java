package model;


public abstract class Utente {
      private String username;
      private String password;
      private String email;

      //costruttore, da utilizzare per le sottoclassi.
          public Utente(String email, String username, String password){
              this.email = email;
              this.username = username;
              this.password = password;
          }
      //metodi public
           public String getUsername(){
                return this.username;
           }

           public String getEmail(){
                return this.email;


           //sono stati aggiunti metodi setusername e setpassword per futura estensibilità del sistema,
               // nonostante esuli dalla specifica
           public void setUsername(String usernameLogin, String passwordLogin, String nuovoUsername) {
            if(controllaAccesso(usernameLogin, passwordLogin)) {
                this.username = nuovoUsername;
            }
          }

            public void setPassword(String usernameLogin, String passwordLogin, String nuovaPassword){
            if(controllaAccesso(usernameLogin, passwordLogin)){
                this.password= nuovaPassword;
            }
          }

          @Override
          public String toString(){
              return email + " " + username;
          }


          //servizi interni alla classe
          protected final boolean controllaAccesso(String username, String password){
            if(username == null || !(username.equals(this.username))){
                return false;
            }
            if(password == null || !(password.equals(this.password))){
                return false;
            }
            return true;
          }
}



