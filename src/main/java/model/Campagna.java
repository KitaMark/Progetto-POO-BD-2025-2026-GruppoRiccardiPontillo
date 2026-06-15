package model;

import java.util.ArrayList;

/**
 * Rappresenta una campagna di gioco.
 * Gestisce lo stato della sessione, il Master responsabile, i giocatori partecipanti
 * e le liste dei personaggi giocanti (PG) e non giocanti (PnG).
 */
public class Campagna {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campagna campagna = (Campagna) o;
        return nome != null ? nome.equalsIgnoreCase(campagna.nome) : campagna.nome == null;
    }

    @Override
    public int hashCode() {
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}