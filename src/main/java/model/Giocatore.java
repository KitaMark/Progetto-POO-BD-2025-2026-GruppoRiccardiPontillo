package model;

import java.util.HashMap;
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

    /**
     * Costruttore d'appoggio creato appositamente per il DAO.
     *
     * @param id       identificativo dell'utente nel database.
     * @param email    l'indirizzo email dell'account.
     * @param username lo username univoco dell'utente.
     * @param password la password di accesso.
     */
    public Giocatore(int id, String email, String username, String password) {
        super(id, email, username, password);
        this.listaPartecipazioni = new HashMap<>();
    }

    /** @return una vista modificabile della mappa delle campagne a cui partecipa con i relativi personaggi. */
    public Map<Campagna, Personaggio> getListaPartecipazioni() {
        return listaPartecipazioni;
    }

    /**
     * Associa al giocatore l'elenco completo delle sue partecipazioni alle campagne.
     * <p>
     * Questo metodo viene richiamato dal Controller subito dopo la fase di Login. Permette di trasferire i dati del giocatore
     * (quali campagne sta giocando e con quali personaggi) recuperati dal Database tramite il DAO
     * direttamente all'interno dell'oggetto in memoria, rendendoli immediatamente
     * disponibili per le tabelle della Dashboard.
     * </p>
     *
     * @param listaPartecipazioni Una mappa che associa ogni {@link Campagna} al relativo {@link Personaggio} creato dal giocatore.
     */
    public void setListaPartecipazioni(HashMap<Campagna, Personaggio> listaPartecipazioni) {
        this.listaPartecipazioni = listaPartecipazioni;
    }

    /**
     * Recupera il personaggio associato a una specifica campagna.
     *
     * @param campagna la campagna in cui cercare.
     * @return Il Personaggio associato, oppure {@code null} se non trovato.
     */
    public Personaggio getPersonaggioInCampagna(Campagna campagna) {
        return listaPartecipazioni.get(campagna);
    }

    /**
     * Metodo di utilità per il DAO per associare una campagna e il relativo personaggio.
     *
     * @param campagna    la campagna a cui il giocatore partecipa.
     * @param personaggio il personaggio associato alla campagna.
     */
    public void addPartecipazioneDati(Campagna campagna, Personaggio personaggio) {
        if (this.listaPartecipazioni == null) {
            this.listaPartecipazioni = new HashMap<>();
        }
        this.listaPartecipazioni.put(campagna, personaggio);
    }
}