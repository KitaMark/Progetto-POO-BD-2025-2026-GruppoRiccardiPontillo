package model;

public class Statistica {
    //si è scelto di modellare hp correnti e mana correnti in Personaggio, poiché descrittivi del suo stato
    //in relazione alle statistiche.

    //il nome della classe va al plurale perchè descrittivo di un'insieme di attributi
    //che formano lo stato interno di un personaggio.
    private int forza;
    private int destrezza;
    private int costituzione;
    private int intelligenza;
    private int fede;
    private int carisma;
    private int fortuna;
    private int hpMax;
    private int manaMax;

    //costruttore statistiche standard per personaggi
    public Statistica(){
        // Inizializzazione a un unico valore standard come richiesto dal documento
        this.forza = 10;
        this.destrezza = 10;
        this.costituzione = 10;
        this.intelligenza = 10;
        this.fede = 10;
        this.carisma = 10;
        this.fortuna = 10;

        // Valori standard iniziali per risorse vitali e magiche
        this.hpMax = 100;
        this.manaMax = 50;
    }

    //costruttore con valori inizializzati, utile per bonus e per png.


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

    //crea copia di altre statistica per manipolazioni sicure
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


    //metodi getter
    public int getForza() {
        return forza;
    }

    public int getDestrezza() {
        return destrezza;
    }

    public int getCostituzione() {
        return costituzione;
    }

    public int getIntelligenza() {
        return intelligenza;
    }

    public int getFede() {
        return fede;
    }

    public int getCarisma() {
        return carisma;
    }

    public int getFortuna() {
        return fortuna;
    }

    public int getHpMax() {
        return hpMax;
    }

    public int getManaMax() {return manaMax;}

    //metodi setter

    public void setForza(int forza) {
        this.forza = forza;
    }

    public void setDestrezza(int destrezza) {
        this.destrezza = destrezza;
    }

    public void setCostituzione(int costituzione) {
        this.costituzione = costituzione;
    }

    public void setIntelligenza(int intelligenza) {
        this.intelligenza = intelligenza;
    }

    public void setFede(int fede) {
        this.fede = fede;
    }

    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    public void setFortuna(int fortuna) {
        this.fortuna = fortuna;
    }

    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
    }

    public void setManaMax(int manaMax) {
        this.manaMax = manaMax;
    }

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

    public boolean soddisfa(Statistica requisiti) {
        return forza >= requisiti.getForza() &&
                destrezza >= requisiti.getDestrezza() &&
                costituzione >= requisiti.getCostituzione() &&
                intelligenza >= requisiti.getIntelligenza() &&
                fede >= requisiti.getFede() &&
                carisma >= requisiti.getCarisma() &&
                fortuna >= requisiti.getFortuna();
    }

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

