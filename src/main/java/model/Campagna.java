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
        this.isIniziata = false;
        this.partecipanti = new ArrayList<>();
        this.listaPG = new ArrayList<>();
        this.listaPnG = new ArrayList<>();
    }

    //GETTER
    public String getNome() { return nome; }
    public int getMaxGiocatori() { return maxGiocatori; }
    public boolean isIniziata() { return isIniziata; }
    public ArrayList<Giocatore> getPartecipanti() { return partecipanti; }
    public Master getMaster() { return master; }
    public ArrayList<Personaggio> getListaPG() { return listaPG; }
    public ArrayList<Personaggio> getListaPnG() { return listaPnG; }

    //SETTER
    public void setNome(String nome) { this.nome = nome; }
    public void setMaxGiocatori(int maxGiocatori) { this.maxGiocatori = maxGiocatori; }
    public void setIniziata(boolean iniziata) { this.isIniziata = iniziata; }
    public void setPartecipanti(ArrayList<Giocatore> partecipanti) { this.partecipanti = partecipanti; }
    public void setListaPG(ArrayList<Personaggio> listaPG) { this.listaPG = listaPG; }
    public void setMaster(Master master) { this.master = master; }
    public void setListaPnG(ArrayList<Personaggio> listaPnG) { this.listaPnG = listaPnG; }
}