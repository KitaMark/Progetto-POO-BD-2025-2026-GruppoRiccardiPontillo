package model;

import java.util.ArrayList;

public class Campagna {
    private String nome;
    private int maxGiocatori;
    private boolean isIniziata;
    private ArrayList<Giocatore> partecipanti;
    private Master master;
    private ArrayList<Personaggio> listaPG;
    private ArrayList<Personaggio> listaPnG;


    public Campagna(String nome, int maxGiocatori, Master master){
        this.nome = nome;
        this.maxGiocatori = maxGiocatori;
        this.master = master;
        isIniziata = false;
        partecipanti = new ArrayList<>();
        listaPG = new ArrayList<>();
        listaPnG = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getMaxGiocatori() {
        return maxGiocatori;
    }

    public boolean isIniziata() {
        return isIniziata;
    }

    public ArrayList<Giocatore> getPartecipanti() {
        return partecipanti;
    }

    public String getMaster() {
        return master.toString(); //vengono ritornate le info in sola lettura per sicurezza.
    }

    public ArrayList<Personaggio> getListaPG() {
        return listaPG;
    }

    public ArrayList<Personaggio> getListaPnG() {
        return listaPnG;
    }

    //le operazioni sulle liste si definiscono dentro master

    //setter utilizzabili solo da master:

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMaxGiocatori(int maxGiocatori) {
        this.maxGiocatori = maxGiocatori;
    }

    public void setIniziata(boolean iniziata) {
        isIniziata = iniziata;
    }

    public void setPartecipanti(ArrayList<Giocatore> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public void setListaPG(ArrayList<Personaggio> listaPG) {
        this.listaPG = listaPG;
    }
}
