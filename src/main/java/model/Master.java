package model;

import java.util.Objects;

/**
 * Rappresenta un utente con privilegi di Master.
 * Ha la responsabilità di creare, moderare e gestire una singola campagna di gioco attiva.
 */
public class Master extends Utente {
    private Campagna campagna;

    /**
     * Crea un nuovo utente Master registrando le credenziali di accesso.
     *
     * @param email    l'indirizzo email dell'account.
     * @param username lo username univoco dell'utente.
     * @param password la password di accesso.
     */
    public Master(String email, String username, String password) {
        super(email, username, password);
        this.campagna = null;
    }

    /**
     * costruttore d'appoggio creato per il dao
     *
     * @param id identificativo univoco del Master nel database
     * @param email l'indirizzo email dell'account.
     * @param username lo username univoco dell'utente.
     * @param password la password di accesso.
     */
    public Master(int id, String email, String username, String password) {
        super(id, email, username, password);
        this.campagna = null;
    }



    /** @return la campagna attualmente gestita dal Master, o {@code null} se assente. */
    public Campagna getCampagna() {
        return campagna;
    }

    /** @param campagna la campagna di gioco da associare al Master. */
    public void setCampagna(Campagna campagna) {
        this.campagna = campagna;
    }
}