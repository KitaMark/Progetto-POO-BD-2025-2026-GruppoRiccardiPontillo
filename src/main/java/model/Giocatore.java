package model;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

/**
 * Rappresenta un utente con ruolo di Giocatore all'interno del sistema.
 * Mantiene il tracciamento delle campagne a cui partecipa e del rispettivo personaggio associato.
 */
public class Giocatore extends Utente {
    private HashMap<Campagna, Personaggio> listaPartecipazioni;

    /**
     * Crea un nuovo Giocatore registrando le credenziali di accesso.
     *
     * @param email    l'indirizzo email dell'account.
     * @param username lo username univoco dell'utente.
     * @param password la password di accesso.
     */
    public Giocatore(String email, String username, String password) {
        super(email, username, password);
        this.listaPartecipazioni = new HashMap<>();
    }

    /** @return una vista non modificabile della mappa delle campagne a cui partecipa con i relativi personaggi. */
    public Map<Campagna, Personaggio> getListaPartecipazioni() {
        return Collections.unmodifiableMap(listaPartecipazioni);
    }
}