package model;

import java.util.ArrayList;

/**
 * Rappresenta la classe archetipica di un personaggio (es. Guerriero, Mago).
 * Definisce l'equipaggiamento iniziale e l'insieme di abilità sbloccabili.
 */
public class Classe {
    private int id; // Identificativo nel DB
    private String nome;
    private String descrizione;
    private ArrayList<Abilita> abilitaSbloccabili;
    private ArrayList<Oggetto> equipaggiamentoIniziale;

    /**
     * Crea una nuova classe di gioco.
     *
     * @param nome il nome identificativo della classe.
     */
    public Classe(String nome) {
        this.nome = nome;
        this.abilitaSbloccabili = new ArrayList<>();
        this.equipaggiamentoIniziale = new ArrayList<>();
    }

    /**
     * Costruttore d'appoggio creato appositamente per il DAO.
     *
     * @param id   identificativo della classe nel database.
     * @param nome nome identificativo della classe.
     */
    public Classe(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.abilitaSbloccabili = new ArrayList<>();
        this.equipaggiamentoIniziale = new ArrayList<>();
    }

    /** @return l'identificativo univoco della classe. */
    public int getId() { return id; }

    /** @return il nome della classe. */
    public String getNome() { return nome; }

    /** @return la descrizione testuale e la lore della classe. */
    public String getDescrizione() { return descrizione; }

    /** @return la lista delle abilità sbloccabili da questa classe. */
    public ArrayList<Abilita> getAbilitaSbloccabili() { return abilitaSbloccabili; }

    /** @return la lista dell'equipaggiamento iniziale previsto. */
    public ArrayList<Oggetto> getEquipaggiamentoIniziale() { return equipaggiamentoIniziale; }

    /** @param id il nuovo identificativo univoco da assegnare alla classe nel database. */
    public void setId(int id) { this.id = id; }

    /** @param nome il nuovo nome da impostare per la classe. */
    public void setNome(String nome) { this.nome = nome; }

    /** @param descrizione la nuova descrizione o lore da associare alla classe. */
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    /** @param abilitaSbloccabili la nuova lista di abilità sbloccabili. */
    public void setAbilitaSbloccabili(ArrayList<Abilita> abilitaSbloccabili) { this.abilitaSbloccabili = abilitaSbloccabili; }

    /** @param equipaggiamentoIniziale il nuovo equipaggiamento iniziale da associare. */
    public void setEquipaggiamentoIniziale(ArrayList<Oggetto> equipaggiamentoIniziale) { this.equipaggiamentoIniziale = equipaggiamentoIniziale; }

    /** @param abilita l'abilità da aggiungere a quelle sbloccabili. */
    public void addAbilita(Abilita abilita) { abilitaSbloccabili.add(abilita); }

    /** @param oggetto l'oggetto da inserire nell'equipaggiamento iniziale. */
    public void addOggetto(Oggetto oggetto) { equipaggiamentoIniziale.add(oggetto); }

    /**
     * Rimuove un'abilità dall'elenco di quelle sbloccabili.
     *
     * @param abilita l'abilità da rimuovere.
     * @return {@code true} se rimossa con successo, {@code false} se non presente.
     */
    public boolean removeAbilita(Abilita abilita){
        if(abilitaSbloccabili.contains(abilita)){
            abilitaSbloccabili.remove(abilita);
            return true;
        }
        return false;
    }

    /**
     * Rimuove un oggetto dall'equipaggiamento iniziale.
     *
     * @param oggetto l'oggetto da rimuovere.
     * @return {@code true} se rimosso con successo, {@code false} se non presente.
     */
    public boolean removeOggetto(Oggetto oggetto){
        if(equipaggiamentoIniziale.contains(oggetto)){
            equipaggiamentoIniziale.remove(oggetto);
            return true;
        }
        return false;
    }

    /**
     * Restituisce la rappresentazione testuale della classe.
     *
     * @return il nome della classe.
     */
    @Override
    public String toString(){
        return nome;
    }
}