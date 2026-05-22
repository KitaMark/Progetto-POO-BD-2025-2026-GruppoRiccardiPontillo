package model;


import java.util.Objects;

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

    public String getEmail() {
        return this.email;
    }

    //no getPassword per maggiore sicurezza.

    //sono stati aggiunti metodi setusername e setpassword per futura estensibilità del sistema,
    // nonostante esuli dalla specifica
    public void setUsername(String usernameLogin, String passwordLogin, String nuovoUsername) {
        if(login(usernameLogin, passwordLogin)) {
            this.username = nuovoUsername;
        }
    }

    public void setPassword(String usernameLogin, String passwordLogin, String nuovaPassword){
        if(login(usernameLogin, passwordLogin)){
            this.password= nuovaPassword;
        }
    }


    @Override
    public String toString(){
        return String.format("email: %s%n username: %s%n", this.getEmail(), this.getUsername());
    }

    protected final boolean login(String username, String password){
        if(!(Objects.equals(this.username, username))){
            return false;
        }
        if(!(Objects.equals(this.password, password))){
            return false;
        }
        return true;
    }


}



