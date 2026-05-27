package model;

/**
 * Gestisce i parametri numerici base e i massimali (HP e Mana) di un personaggio.
 * Non include lo stato corrente delle risorse dinamiche, gestite direttamente in Personaggio.
 */
public class Statistica {

    private int forza;
    private int destrezza;
    private int costituzione;
    private int intelligenza;
    private int fede;
    private int carisma;
    private int fortuna;
    private int hpMax;
    private int manaMax;

    /**
     * Crea un'istanza di Statistica con i valori iniziali predefiniti dal sistema.
     */
    public Statistica(){
        this.forza = 10;
        this.destrezza = 10;
        this.costituzione = 10;
        this.intelligenza = 10;
        this.fede = 10;
        this.carisma = 10;
        this.fortuna = 10;
        this.hpMax = 100;
        this.manaMax = 50;
    }

    /**
     * Crea un'istanza di Statistica con valori personalizzati per ciascun parametro.
     *
     * @param costituzione il valore di costituzione.
     * @param forza        il valore di forza.
     * @param destrezza    il valore di destrezza.
     * @param intelligenza il valore di intelligenza.
     * @param fede         il valore di fede.
     * @param carisma      il valore di carisma.
     * @param fortuna      il valore di fortuna.
     * @param hpMax        la soglia massima di punti ferita.
     * @param manaMax      la riserva massima di punti mana.
     */
    public Statistica(int costituzione, int forza, int destrezza, int intelligenza,
                      int fede, int carisma, int fortuna, int hpMax, int manaMax) {
        this.costituzione = costituzione;
        this.forza = forza;
        this.destrezza = destrezza;
        this.intelligenza = intelligenza;
        this.fede = fede;
        this.carisma = carisma;
        this.fortuna = fortuna;
        this.hpMax = hpMax;
        this.manaMax = manaMax;
    }

    /**
     * Costruttore di copia. Crea una nuova istanza duplicando i valori di un'altra Statistica.
     *
     * @param daCopiare l'oggetto Statistica da cui copiare i valori.
     */
    public Statistica(Statistica daCopiare) {
        this.costituzione = daCopiare.getCostituzione();
        this.forza = daCopiare.getForza();
        this.destrezza = daCopiare.getDestrezza();
        this.intelligenza = daCopiare.getIntelligenza();
        this.fede = daCopiare.getFede();
        this.carisma = daCopiare.getCarisma();
        this.fortuna = daCopiare.getFortuna();
        this.hpMax = daCopiare.getHpMax();
        this.manaMax = daCopiare.getManaMax();
    }

    /** @return il punteggio di forza. */
    public int getForza() {
        return forza;
    }

    /** @return il punteggio di destrezza. */
    public int getDestrezza() {
        return destrezza;
    }

    /** @return il punteggio di costituzione. */
    public int getCostituzione() {
        return costituzione;
    }

    /** @return il punteggio di intelligenza. */
    public int getIntelligenza() {
        return intelligenza;
    }

    /** @return il punteggio di fede. */
    public int getFede() {
        return fede;
    }

    /** @return il punteggio di carisma. */
    public int getCarisma() {
        return carisma;
    }

    /** @return il punteggio di fortuna. */
    public int getFortuna() {
        return fortuna;
    }

    /** @return il valore massimo di HP. */
    public int getHpMax() {
        return hpMax;
    }

    /** @return il valore massimo di mana. */
    public int getManaMax() {
        return manaMax;
    }

    /** @param forza il nuovo valore di forza da impostare. */
    public void setForza(int forza) {
        this.forza = forza;
    }

    /** @param destrezza il nuovo valore di destrezza da impostare. */
    public void setDestrezza(int destrezza) {
        this.destrezza = destrezza;
    }

    /** @param costituzione il nuovo valore di costituzione da impostare. */
    public void setCostituzione(int costituzione) {
        this.costituzione = costituzione;
    }

    /** @param intelligenza il nuovo valore di intelligenza da impostare. */
    public void setIntelligenza(int intelligenza) {
        this.intelligenza = intelligenza;
    }

    /** @param fede il nuovo valore di fede da impostare. */
    public void setFede(int fede) {
        this.fede = fede;
    }

    /** @param carisma il nuovo valore di carisma da impostare. */
    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    /** @param fortuna il nuovo valore di fortuna da impostare. */
    public void setFortuna(int fortuna) {
        this.fortuna = fortuna;
    }

    /** @param hpMax la nuova soglia massima di HP da impostare. */
    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
    }

    /** @param manaMax la nuova soglia massima di mana da impostare. */
    public void setManaMax(int manaMax) {
        this.manaMax = manaMax;
    }

    /**
     * Somma i valori di un secondo oggetto Statistica a quella corrente.
     *
     * @param bonus l'oggetto Statistica contenente i modificatori da sommare.
     */
    public void aggiungiBonus(Statistica bonus){
        this.forza += bonus.getForza();
        this.destrezza += bonus.getDestrezza();
        this.costituzione += bonus.getCostituzione();
        this.intelligenza += bonus.getIntelligenza();
        this.fede += bonus.getFede();
        this.carisma += bonus.getCarisma();
        this.fortuna += bonus.getFortuna();
        this.hpMax += bonus.getHpMax();
        this.manaMax += bonus.getManaMax();
    }

    /**
     * Verifica se gli attributi base soddisfano o superano i requisiti specificati.
     * Il controllo esclude i massimali di HP e Mana.
     *
     * @param requisiti i valori minimi richiesti.
     * @return {@code true} se tutti i requisiti sono soddisfatti, {@code false} altrimenti.
     */
    public boolean soddisfa(Statistica requisiti) {
        return forza >= requisiti.getForza() &&
                destrezza >= requisiti.getDestrezza() &&
                costituzione >= requisiti.getCostituzione() &&
                intelligenza >= requisiti.getIntelligenza() &&
                fede >= requisiti.getFede() &&
                carisma >= requisiti.getCarisma() &&
                fortuna >= requisiti.getFortuna();
    }

    /**
     * Incrementa il valore di uno specifico attributo identificato tramite stringa.
     *
     * @param attributo il nome della statistica da potenziare.
     * @param punti     la quantità di punti da aggiungere.
     * @throws IllegalArgumentException se la stringa non corrisponde a nessun attributo valido.
     */
    public void incrementa(String attributo, int punti) {
        switch (attributo.toLowerCase()) {
            case "forza" -> forza += punti;
            case "destrezza" -> destrezza += punti;
            case "costituzione" -> costituzione += punti;
            case "intelligenza" -> intelligenza += punti;
            case "fede" -> fede += punti;
            case "carisma" -> carisma += punti;
            case "fortuna" -> fortuna += punti;
            case "hpmax" -> hpMax += punti;
            case "manamax" -> manaMax += punti;
            default -> throw new IllegalArgumentException("Attributo non riconosciuto: " + attributo);
        }
    }

    /**
     * Restituisce la rappresentazione testuale dei valori delle statistiche.
     *
     * @return stringa formattata con l'elenco dei parametri correnti.
     */
    @Override
    public String toString() {
        return String.format("""
                Statistiche{%n
                    forza = %d%n 
                    destrezza = %d%n
                    costituzione = %d%n 
                    intelligenza = %d%n
                    fede = %d%n
                    carisma = %d%n 
                    fortuna = %d%n 
                    hpMax= %d%n 
                    manaMax= %d%n
                }%n""", forza, destrezza, costituzione, intelligenza, fede,
                carisma, fortuna, hpMax, manaMax);
    }
}