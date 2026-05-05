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



}