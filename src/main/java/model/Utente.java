package model;

import java.util.Objects;

/**
 * Classe astratta base per la rappresentazione di un utente nel sistema.
 * Gestisce le credenziali di accesso di base (email, username e password).
 * @see Giocatore
 * @see Master
 */
public abstract class Utente {
    private int id; //identificativo univoco del db
    private String email;
    private String username;
    private String password;

    /**
     * Costruttore per l'inizializzazione dei dati dell'utente.
     *
     * @param email    l'indirizzo email dell'utente.
     * @param username lo username dell'utente.
     * @param password la password di accesso.
     */
    public Utente(String email, String username, String password){
        this.email = email;
        this.username = username;
        this.password = password;
    }


    /**
     * Costruttore creato appositamente per il dao
     *
     * @param id       identificativo univoco dell'utente nel database
     * @param email    l'indirizzo email dell'utente.
     * @param username lo username dell'utente.
     * @param password la password di accesso.
     */
    public Utente(int id, String email, String username, String password){
        this.id=id;
        this.email = email;
        this.username = username;
        this.password = password;
    }


    /** @return l'identificativo dell'utente */
    public int getId(){
        return this.id;
    }

    /** @return lo username dell'utente. */
    public String getUsername(){
        return this.username;
    }

    /** @return l'indirizzo email dell'utente. */
    public String getEmail() {
        return this.email;
    }

    /** @return la password dell'utente. */
    public String getPassword() {
        return this.password;
    }

    /** @param nuovoUsername il nuovo username da impostare. */
    public void setUsername(String nuovoUsername) {
        this.username = nuovoUsername;
    }

    /** @param nuovaPassword la nuova password da impostare. */
    public void setPassword(String nuovaPassword){
        this.password = nuovaPassword;
    }

    /** @param nuovaEmail la nuova email da impostare. */
    public void setEmail(String nuovaEmail) {
        this.email = nuovaEmail;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce una rappresentazione testuale dei dati identificativi dell'utente.
     *
     * @return stringa formattata con email e username.
     */
    @Override
    public String toString(){
        return String.format("Utente [email: %s, username: %s]", this.email, this.username);
    }

    /**
     * Confronta due utenti. Due utenti sono considerati uguali se hanno lo stesso username,
     * poiché lo username è un campo UNIQUE a livello di database, evitiamo di usare l'id poiché prima del
     * salvataggio nel database esso è uguale a 0 e si potrebbero creare problemi.
     */
    @Override
    public boolean equals(Object o) {
        // se è la stessa identica istanza in memoria, restituisci true
        if (this == o) return true;

        // Controllo null e classe: se è nullo o di classe diversa, restituisci false
        if (o == null || getClass() != o.getClass()) return false;

        //Confronto logico basato sulla chiave naturale (username)
        Utente utente = (Utente) o;
        return Objects.equals(username, utente.username);
    }

    /**
     * Genera l'hash code basato sulla chiave naturale (username).
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}