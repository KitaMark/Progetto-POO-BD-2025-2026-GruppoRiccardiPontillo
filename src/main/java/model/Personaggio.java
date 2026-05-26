package model;
import java.util.*;


public class Personaggio {
    private String nome;
    private Statistica statisticaBase;
    private Statistica statisticaFinali;
    private int hpCorrenti;
    private int manaCorrente;
    private  Classe classe;
    private  Razza razza;
    private int puntiStatistica;
    private int oro;
    private boolean isPg;
    private HashMap<OggettoConsumabile, Integer> inventarioConsumabili;
    private HashMap<OggettoEquipaggiabile, Boolean> inventarioEquipaggiabili;
    private ArrayList<Abilita> listaAbilita;


    //Per semplicità del sistema si preferisce implementare due HashMap distinte per i consumabili,
    // associati alla quantità posseduta; poiché per gli equipaggiabili il valore d'interesse del modello è
    // "isEquipaggiato", le chiavi sono associati a valori boolean che indicano se l'oggetto è equipaggiato.
    //Per gli equipaggiabili si ritiene superfluo indicarne la quantità.

    //Documentazione di riferimento HashMap: https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html.
    //Documentazione Map: https://docs.oracle.com/javase/8/docs/api/java/util/Map.html
    //Documentazione Collections: https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html
    //Documentazione Set: https://docs.oracle.com/javase/8/docs/api/java/util/Set.html
    //Documentazione Map.Entry: https://docs.oracle.com/javase/8/docs/api/java/util/Map.Entry.html



    // COSTRUTTORE PER LA CREAZIONE DI UN NUOVO PG (GUI GIOCATORE)
    public Personaggio(Classe classe, Razza razza, String nome) {
        this.classe = classe;
        this.razza = razza;
        this.statisticaBase = new Statistica();
        this.nome = nome;
        this.isPg = true;
        this.puntiStatistica = 0;
        this.oro = 0;
        this.inventarioConsumabili = new HashMap<>();
        this.inventarioEquipaggiabili = new HashMap<>();
        this.listaAbilita = new ArrayList<>();

        inizializzaEquipaggiamentoIniziale(classe);
        statisticaBase.aggiungiBonus(razza.getModificatori());

        this.hpCorrenti = statisticaBase.getHpMax();
        this.manaCorrente = statisticaBase.getManaMax();
        aggiornaStatoPG();
    }

    // COSTRUTTORE PER LA CREAZIONE DI UN NUOVO PNG (GUI MASTER)
    public Personaggio(Classe classe, Razza razza, Statistica statisticaBase, String nome,
                       int oro, int puntiStatistica) {
        this.classe = classe;
        this.razza = razza;
        this.statisticaBase = statisticaBase;
        this.nome = nome;
        this.isPg = false;
        this.puntiStatistica = puntiStatistica;
        this.oro = oro;
        this.inventarioEquipaggiabili = new HashMap<>();
        this.inventarioConsumabili = new HashMap<>();
        this.listaAbilita = new ArrayList<>();

        inizializzaEquipaggiamentoIniziale(classe);
        statisticaBase.aggiungiBonus(razza.getModificatori());

        this.hpCorrenti = statisticaBase.getHpMax();
        this.manaCorrente = statisticaBase.getManaMax();
        aggiornaStatoPG();
    }



    public void spendipuntiStatistica(int punti, String attributo) {
        if (punti > puntiStatistica || punti < 1) throw new IllegalArgumentException("Valore punti non valido.");
        statisticaBase.incrementa(attributo, punti);
        puntiStatistica -= punti;
        aggiornaStatoPG();
    }

    public void compraOggetto(Oggetto oggetto) {
        if (oggetto == null) throw new IllegalArgumentException("Seleziona un oggetto valido.");
        if (oro < oggetto.getCosto()) throw new IllegalArgumentException("Oro insufficiente.");
        if (oggetto instanceof OggettoEquipaggiabile) {
            if (!(inventarioEquipaggiabili.containsKey((OggettoEquipaggiabile) oggetto))) {
                inventarioEquipaggiabili.put((OggettoEquipaggiabile) oggetto, false);
                this.oro -= oggetto.getCosto();
            } else {
                throw new IllegalArgumentException("Non puoi acquistare un equipaggiabile che già possiedi!");
            }
        } else if (oggetto instanceof OggettoConsumabile) {
            addConsumabile((OggettoConsumabile) oggetto, 1);
            this.oro -= oggetto.getCosto();
        }
    }

    public void vendiOggetto(Oggetto oggetto) {
        if (oggetto == null) throw new IllegalArgumentException("Seleziona un oggetto valido.");
        if (oggetto instanceof OggettoEquipaggiabile) {
            if (!(inventarioEquipaggiabili.containsKey((OggettoEquipaggiabile) oggetto))) {
                throw new IllegalArgumentException("Non possiedi quest'oggetto!");
            } else {
                rimuoviEquipaggiabile((OggettoEquipaggiabile) oggetto);
                oro += oggetto.getCosto() / 2;
            }
        } else if (oggetto instanceof OggettoConsumabile) {
            if (inventarioConsumabili.containsKey((OggettoConsumabile) oggetto)) {
                rimuoviConsumabile((OggettoConsumabile) oggetto, 1);
                oro += oggetto.getCosto() / 2;
            } else {
                throw new IllegalArgumentException("Non possiedi quest'oggetto!");
            }
        }
    }

    public boolean equipaggia(OggettoEquipaggiabile equipaggiabile) {
        if (equipaggiabile == null) throw new IllegalArgumentException("Oggetto non valido.");
        if (inventarioEquipaggiabili.containsKey(equipaggiabile)) {
            if (statisticaBase.soddisfa(equipaggiabile.getRequisiti())) {
                inventarioEquipaggiabili.replace(equipaggiabile, true);
                aggiornaStatoPG();
                return true;
            } else return false;
        } else throw new IllegalArgumentException("Non possiedi quest'oggetto!");
    }

    public void rimuoviEquipaggiamento(OggettoEquipaggiabile equipaggiabile) {
        if (equipaggiabile == null) throw new IllegalArgumentException("Oggetto non valido.");
        if (inventarioEquipaggiabili.containsKey(equipaggiabile)) {
            inventarioEquipaggiabili.replace(equipaggiabile, false);
            aggiornaStatoPG();
        } else throw new IllegalArgumentException("Non possiedi quest'oggetto!");
    }

    public void usaConsumabile(OggettoConsumabile oggetto) {
        if (inventarioConsumabili.containsKey(oggetto)) {
            ripristinaHP(oggetto.getRipristinoHP());
            ripristinaMana(oggetto.getRipristinoMana());
            rimuoviConsumabile(oggetto, 1);
        } else throw new IllegalArgumentException("Non possiedi quest'oggetto!");
    }

    // GETTER
    public String getNome() { return nome; }
    public Statistica getStatisticheBase() { return statisticaBase; }
    public Statistica getStatisticheFinali() { return statisticaFinali; }
    public Classe getClasse() { return classe; }
    public Razza getRazza() { return razza; }
    public int getHpCorrenti() { return hpCorrenti; }
    public int getManaCorrente() { return manaCorrente; }
    public int getPuntiStatistica() { return puntiStatistica; }
    public int getOro() { return oro; }
    public boolean isPg() { return isPg; }
    public List<Abilita> getListaAbilita() { return Collections.unmodifiableList(listaAbilita); }
    public Map<OggettoConsumabile, Integer> getInventarioConsumabili() { return Collections.unmodifiableMap(inventarioConsumabili); }
    public Map<OggettoEquipaggiabile, Boolean> getInventarioEquipaggiabili() { return Collections.unmodifiableMap(inventarioEquipaggiabili); }


    //utilizzabile se permesso o da master.
    public void addAbilita(Abilita abilita) {
        if (abilita == null) return;
        if (!classe.getAbilitaSbloccabili().contains(abilita)) return;
        if (!listaAbilita.contains(abilita)) listaAbilita.add(abilita);
    }

    public void rimuoviEquipaggiabile(OggettoEquipaggiabile oggetto) {
        inventarioEquipaggiabili.remove(oggetto);
    }

    public void rimuoviConsumabile(OggettoConsumabile oggetto, int quantita) {
        if (inventarioConsumabili.get(oggetto) <= quantita) {
            inventarioConsumabili.remove(oggetto);
        } else {
            inventarioConsumabili.replace(oggetto, inventarioConsumabili.get(oggetto) - quantita);
        }
    }

    public void addEquipaggiabile(OggettoEquipaggiabile oggetto) {
        if (!(inventarioEquipaggiabili.containsKey(oggetto))) {
            inventarioEquipaggiabili.put(oggetto, false);
        }
    }

    public void addConsumabile(OggettoConsumabile oggetto, int quantita) {
        if (inventarioConsumabili.containsKey(oggetto)) {
            inventarioConsumabili.replace(oggetto, inventarioConsumabili.get(oggetto) + quantita);
        } else {
            inventarioConsumabili.put(oggetto, quantita);
        }
    }

    public void ripristinaHP(int valore){
        if(valore > 0){
            this.hpCorrenti += valore;
            if(hpCorrenti > this.getStatisticheFinali().getHpMax()){
                hpCorrenti = statisticaFinali.getHpMax();
            }
        }
    }

    public void ripristinaMana(int valore){
        if(valore > 0){
            this.manaCorrente += valore;
            if(manaCorrente > this.getStatisticheFinali().getManaMax()){
                manaCorrente = statisticaFinali.getManaMax();
            }
        }
    }

    private void calcolaStatisticheFinali() {
        statisticaFinali = new Statistica(statisticaBase);
        for(Map.Entry<OggettoEquipaggiabile, Boolean> entry : inventarioEquipaggiabili.entrySet()){
            if(entry.getValue()){
                statisticaFinali.aggiungiBonus(entry.getKey().getBonus());
            }
        }
    }

    private void inizializzaEquipaggiamentoIniziale(Classe classe) {
        for (Oggetto o : classe.getEquipaggiamentoIniziale()) {
            if (o instanceof OggettoConsumabile) {
                inventarioConsumabili.put((OggettoConsumabile) o, 1);
            } else if (o instanceof OggettoEquipaggiabile) {
                inventarioEquipaggiabili.put((OggettoEquipaggiabile) o, false);
            }
        }
    }

    public void aggiornaStatoPG() {
        for (Map.Entry<OggettoEquipaggiabile, Boolean> entry : inventarioEquipaggiabili.entrySet()) {
            if (entry.getValue() && !statisticaBase.soddisfa(entry.getKey().getRequisiti())) {
                inventarioEquipaggiabili.replace(entry.getKey(), false);
            }
        }
        calcolaStatisticheFinali();
    }



    @Override
    public String toString() {
        return String.format((isPg() ? "PG:%n{%n" : "PnG:%n{%n") + "Nome: %s%nRazza: %s%nClasse: %s%nHP: %d/%d%nMana: %d/%d%n" +
                        "Forza: %d%nDestrezza: %d%nCostituzione: %d%nIntelligenza: %d%nCarisma: %d%nFede: %d%nFortuna: %d%n" +
                        "Oro: %d%nPunti statistica: %d%n}", nome, razza, classe, hpCorrenti, statisticaFinali.getHpMax(),
                manaCorrente, statisticaFinali.getManaMax(), statisticaFinali.getForza(), statisticaFinali.getDestrezza(),
                statisticaFinali.getCostituzione(), statisticaFinali.getIntelligenza(), statisticaFinali.getCarisma(),
                statisticaFinali.getFede(), statisticaFinali.getFortuna(), oro, puntiStatistica);
    }
}