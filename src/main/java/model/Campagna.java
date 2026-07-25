package model;

import java.util.ArrayList;
import java.util.Objects;

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
    private ArrayList<Oggetto> catalogoOggetti;
    private ArrayList<Razza> listaRazze;
    private ArrayList<Classe> listaClassi;

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
        this.catalogoOggetti = new ArrayList<>();
        this.listaRazze = new ArrayList<>();
        this.listaClassi = new ArrayList<>();
    }

    /**
     * Crea una nuova campagna di gioco in stato non iniziata (costruttore per il DAO).
     *
     * @param id           l'identificativo univoco della campagna nel db.
     * @param nome         il nome della campagna.
     * @param maxGiocatori il numero massimo di giocatori ammessi.
     * @param isIniziata   lo stato di avanzamento della campagna.
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
        this.catalogoOggetti = new ArrayList<>();
        this.listaRazze = new ArrayList<>();
        this.listaClassi = new ArrayList<>();
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

    /** @return la lista degli oggetti acquistabili nel negozio della campagna. */
    public ArrayList<Oggetto> getCatalogoOggetti() { return catalogoOggetti; }

    /** @return la lista delle razze disponibili per la creazione dei personaggi in questa campagna. */
    public ArrayList<Razza> getListaRazze() { return listaRazze; }

    /** @return la lista delle classi giocabili disponibili in questa campagna. */
    public ArrayList<Classe> getListaClassi() { return listaClassi; }

    /** @param id il nuovo identificativo univoco da assegnare alla campagna nel database. */
    public void setId(int id) { this.id = id; }

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

    /**
     * Confronta questa campagna con un altro oggetto per stabilirne l'uguaglianza.
     * L'uguaglianza è basata sull'ID (se presente) o sul nome case-insensitive.
     *
     * @param o L'oggetto da confrontare.
     * @return {@code true} se le campagne sono considerate identiche, {@code false} altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Campagna campagna = (Campagna) o;

        if (this.id > 0 && campagna.id > 0) {
            return this.id == campagna.id;
        }
        if (this.nome != null && campagna.nome != null) {
            return this.nome.trim().equalsIgnoreCase(campagna.nome.trim());
        }
        return false;
    }

    /**
     * Genera un codice hash per la campagna basato sul nome o sull'ID.
     *
     * @return il codice hash generato.
     */
    @Override
    public int hashCode() {
        return this.nome != null ? this.nome.trim().toLowerCase().hashCode() : Objects.hash(id);
    }
}