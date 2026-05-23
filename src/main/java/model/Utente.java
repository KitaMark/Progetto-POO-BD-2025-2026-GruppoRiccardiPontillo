package model;

public abstract class Utente {
    private String email;
    private String username;
    private String password;

    // Costruttore utilizzato per le sottoclassi
    public Utente(String email, String username, String password){
        this.email = email;
        this.username = username;
        this.password = password;
    }

    //GETTER
    public String getUsername(){
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    // SETTER (futuri controlli li fa il Controller)
    public void setUsername(String nuovoUsername) {
        this.username = nuovoUsername;
    }

    public void setPassword(String nuovaPassword){
        this.password = nuovaPassword;
    }

    public void setEmail(String nuovaEmail) {
        this.email = nuovaEmail;
    }

    @Override
    public String toString(){
        return String.format("Utente [email: %s, username: %s]", this.email, this.username);
    }
}