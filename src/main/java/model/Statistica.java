package model;

public class Statistica {
    private int hpCorrenti;
    private int manaCorrenti;
    private int costituzione;
    private int forza;
    private int destrezza;
    private int intelligenza;
    private int fede;
    private int carisma;
    private int fortuna;
    private int maxHp;
    private int maxMana;

    public Statistica() {
        // Inizializzazione a un unico valore standard come richiesto dal documento
        this.forza = 10;
        this.destrezza = 10;
        this.costituzione = 10;
        this.intelligenza = 10;
        this.fede = 10;
        this.carisma = 10;
        this.fortuna = 10;

        // Valori standard iniziali per risorse vitali e magiche
        this.maxHp = 100;
        this.hpCorrenti = 100;
        this.maxMana = 50;
        this.manaCorrenti = 50;
    }

    //Costruttore specifico per l'inizializzazione dei requisiti degli equipaggiamenti. (metodo creaPersonaggio in utente)
    public Statistica(int forza, int destrezza, int costituzione, int intelligenza, int fede, int carisma, int fortuna) {
        this.forza = forza;
        this.destrezza = destrezza;
        this.costituzione = costituzione;
        this.intelligenza = intelligenza;
        this.fede = fede;
        this.carisma = carisma;
        this.fortuna = fortuna;

        // Azzeriamo i requisiti di HP e Mana direttamente qui
        this.maxHp = 0;
        this.hpCorrenti = 0;
        this.maxMana = 0;
        this.manaCorrenti = 0;
    }

    // Costruttore di copia (usato nel metodo  getStatisticheFinali in Personaggio)
    public Statistica(Statistica altra) {
        this.forza = altra.getForza();
        this.destrezza = altra.getDestrezza();
        this.costituzione = altra.getCostituzione();
        this.intelligenza = altra.getIntelligenza();
        this.fede = altra.getFede();
        this.carisma = altra.getCarisma();
        this.fortuna = altra.getFortuna();
        this.maxHp = altra.getMaxHp();
        this.maxMana = altra.getMaxMana();
        this.hpCorrenti = altra.getHpCorrenti();
        this.manaCorrenti = altra.getManaCorrenti();
    }



        public int getHpCorrenti(){
            return this.hpCorrenti;
         }

        public int getManaCorrenti(){
             return this.manaCorrenti;
        }

        public int getMaxHp(){
            return this.maxHp;
        }

        public int getMaxMana(){
           return this.maxMana;
        }

        public int getForza(){
           return this.forza;
        }

        public int getDestrezza(){
           return this.destrezza;
        }

        public int getCostituzione(){
            return this.costituzione;
        }

        public int getIntelligenza(){
            return this.intelligenza;
        }

        public int getFede(){
           return this.fede;
        }

        public int getCarisma(){
           return this.carisma;
        }

        public int getFortuna(){
           return this.fortuna;
        }


        public void setHpCorrenti( int nuovoHpCorrenti){
            this.hpCorrenti= nuovoHpCorrenti;
        }

        public void setManaCorrenti( int nuovoManaCorrenti){
            this.manaCorrenti= nuovoManaCorrenti;
        }

        public void setMaxHp( int nuovoMaxHp){
            this.maxHp= nuovoMaxHp;
        }

        public void setMaxMana( int nuovoMaxMana){
           this.maxMana= nuovoMaxMana;
        }

        public void setForza( int nuovoForza){
           this.forza= nuovoForza;
        }

        public void setDestrezza( int nuovoDestrezza){this.destrezza= nuovoDestrezza;}

        public void setCostituzione( int nuovoCostituzione){
          this.costituzione= nuovoCostituzione;
        }

        public void setIntelligenza( int nuovoIntelligenza){
           this.intelligenza= nuovoIntelligenza;
        }

        public void setCarisma( int nuovoCarisma){
           this.carisma= nuovoCarisma;
        }

        public void setFede( int nuovoFede){
           this.fede= nuovoFede;
        }

        public void setFortuna( int nuovoFortuna){
          this.fortuna= nuovoFortuna;
        }

           public void sommaStatistiche(Statistica puntiDaAggiungere) {
                   this.hpCorrenti+= puntiDaAggiungere.getHpCorrenti();
                   this.manaCorrenti+= puntiDaAggiungere.getManaCorrenti();
                   this.maxHp += puntiDaAggiungere.getMaxHp();
                   this.maxMana += puntiDaAggiungere.getMaxMana();
                   this.forza+= puntiDaAggiungere.getForza();
                   this.destrezza += puntiDaAggiungere.getDestrezza();
                   this.costituzione += puntiDaAggiungere.getCostituzione();
                   this.intelligenza+= puntiDaAggiungere.getIntelligenza();
                   this.fede+= puntiDaAggiungere.getFede();
                   this.carisma+= puntiDaAggiungere.getCarisma();
                   this.fortuna+= puntiDaAggiungere.getFortuna();
              }


           public int calcolaLivello() {
              int sommaCapacita = this.forza + this.destrezza + this.costituzione +
                     this.intelligenza + this.fede + this.carisma + this.fortuna;

                  int livelloPG= sommaCapacita / 10; //ogni 10 punti un livello
                  if (livelloPG < 1) return 1; //controllo per assicurarci che il livello minimo sia almeno 1

                          return livelloPG;
                          }

}

