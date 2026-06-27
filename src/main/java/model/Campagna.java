package model;

import java.util.ArrayList;

/**
 * Rappresenta una campagna di gioco.
 * Gestisce lo stato della sessione, il Master responsabile, i giocatori partecipanti
 * e le liste dei personaggi giocanti (PG) e non giocanti (PnG).
 */
public class Campagna {
    private int id; //identificativo della campagna nel db
    private String nome;
    private int maxGiocatori;
    private boolean isIniziata;
    private ArrayList<Giocatore> partecipanti;
    private Master master;
    private ArrayList<Personaggio> listaPG;
    private ArrayList<Personaggio> listaPnG;

    /**
     * Crea una nuova campagna di gioco in stato non iniziata.
     *
     * @param nome         il nome della campagna.
     * @param maxGiocatori il numero massimo di giocatori ammessi.
     * @param master       il Master che gestisce la campagna.
     */
    public Campagna(String nome, int maxGiocatori, Master master){
        this.nome = nome;
        this.maxGiocatori = maxGiocatori;
        this.master = master;
        this.isIniziata = false;
        this.partecipanti = new ArrayList<>();
        this.listaPG = new ArrayList<>();
        this.listaPnG = new ArrayList<>();
    }

    /**
     * Crea una nuova campagna di gioco in stato non iniziata.
     *
     * @param id l'identificativo univoco della campagna nel db
     * @param nome         il nome della campagna.
     * @param maxGiocatori il numero massimo di giocatori ammessi.
     * @param master       il Master che gestisce la campagna.
     */

    public Campagna(int id, String nome, int maxGiocatori, boolean isIniziata, Master master) {
        this.id = id;
        this.nome = nome;
        this.maxGiocatori = maxGiocatori;
        this.isIniziata = isIniziata;
        this.master = master;
        this.partecipanti = new ArrayList<>();
        this.listaPG = new ArrayList<>();
        this.listaPnG = new ArrayList<>();
    }

    /** @return l'identificativo della campagna nel database. */
    public int getId() { return id; }

    /** @return il nome della campagna. */
    public String getNome() { return nome; }

    /** @return il numero massimo di giocatori consentiti. */
    public int getMaxGiocatori() { return maxGiocatori; }

    /** @return {@code true} se la campagna è avviata, {@code false} altrimenti. */
    public boolean isIniziata() { return isIniziata; }

    /** @return la lista dei giocatori partecipanti. */
    public ArrayList<Giocatore> getPartecipanti() { return partecipanti; }

    /** @return il Master della campagna. */
    public Master getMaster() { return master; }

    /** @return la lista dei personaggi giocanti (PG) attivi. */
    public ArrayList<Personaggio> getListaPG() { return listaPG; }

    /** @return la lista dei personaggi non giocanti (PnG) inseriti. */
    public ArrayList<Personaggio> getListaPnG() { return listaPnG; }

    /** @param nome il nuovo nome da assegnare alla campagna. */
    public void setNome(String nome) { this.nome = nome; }

    /** @param maxGiocatori il nuovo limite massimo di giocatori. */
    public void setMaxGiocatori(int maxGiocatori) { this.maxGiocatori = maxGiocatori; }

    /** @param iniziata lo stato di attivazione da impostare. */
    public void setIniziata(boolean iniziata) { this.isIniziata = iniziata; }

    /** @param partecipanti la nuova lista di giocatori partecipanti. */
    public void setPartecipanti(ArrayList<Giocatore> partecipanti) { this.partecipanti = partecipanti; }

    /** @param listaPG la nuova lista di personaggi giocanti. */
    public void setListaPG(ArrayList<Personaggio> listaPG) { this.listaPG = listaPG; }

    /** @param listaPnG la nuova lista di personaggi non giocanti. */
    public void setListaPnG(ArrayList<Personaggio> listaPnG) { this.listaPnG = listaPnG; }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Confronta questa campagna con un altro oggetto per stabilire se sono logicamente uguali.
     * <p>
     * Sovrascrivendo questo metodo, diciamo che due istanze diverse di
     * {@link Campagna} devono essere considerate "la stessa campagna" se hanno lo stesso {@code nome},
     * ignorando eventuali differenze tra lettere maiuscole e minuscole.
     * </p>
     *
     * @param o L'oggetto da confrontare con questa campagna.
     * @return {@code true} se l'oggetto passato è una Campagna con lo stesso nome, altrimenti {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        //Se è esattamente lo stesso oggetto in memoria, sono sicuramente uguali
        if (this == o) {
            return true;
        }

        //Se l'oggetto passato è nullo o appartiene a una classe diversa, non sono uguali
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        //Cast a Campagna
        Campagna campagna = (Campagna) o;

        //Confronto vero e proprio del nome
        if (this.nome != null) {
            // Se il nome non è nullo, confrontiamo ignorando il maiuscolo/minuscolo
            return this.nome.equalsIgnoreCase(campagna.nome);
        } else {
            // Se il nostro nome è nullo, sono uguali solo se anche l'altro nome è nullo
            return campagna.nome == null;
        }
    }

    /**
     * Genera un codice numerico (hash) che identifica in modo univoco questa campagna in base al suo nome.
     * <p>
     *  Strutture dati come le {@code HashMap} usate nel Controller per memorizzare la lista delle campagne
     *  usano questo numero per smistare e ritrovare velocemente gli oggetti. Se due campagne sono "uguali" secondo l'equals,
     * devono per forza restituire lo stesso numero qui.
     * </p>
     *
     * @return Il codice hash calcolato in base al nome (convertito in minuscolo per coerenza con l'equals).
     */
    @Override
    public int hashCode() {
        if (this.nome != null) {
            // Convertiamo in minuscolo prima di generare l'hash,
            // per garantire che "Yo" e "yo" finiscano nello stesso "cassetto" della HashMap
            return this.nome.toLowerCase().hashCode();
        } else {
            return 0;
        }
    }
}