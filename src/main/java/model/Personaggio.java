package model;

import java.util.*;

/**
 * Rappresenta un personaggio all'interno del sistema di gioco.
 * Gestisce le statistiche (base e finali), lo stato delle risorse vitali (HP e Mana),
 * l'oro, le abilità apprese e l'inventario diviso tra oggetti consumabili ed equipaggiabili.
 * Può essere configurato come Personaggio Giocante (PG) o Non Giocante (PnG).
 */
public class Personaggio {
    private int id; // Identificativo univoco del database
    private String nome;
    private Statistica statisticaBase;
    private Statistica statisticaFinali;
    private int hpCorrenti;
    private int manaCorrente;
    private Classe classe;
    private Razza razza;
    private int puntiStatistica;
    private int oro;
    private boolean isPg;


    /** Mappa dei consumabili posseduti associati alla rispettiva quantità. */
    private HashMap<OggettoConsumabile, Integer> inventarioConsumabili;

    /** Mappa degli equipaggiabili posseduti associati allo stato di equipaggiamento (true se indossato). */
    private HashMap<OggettoEquipaggiabile, Boolean> inventarioEquipaggiabili;
    private ArrayList<Abilita> listaAbilita;

    /**
     * Costruttore per la creazione di un nuovo Personaggio Giocante (PG). Utilizzato anche per la creazione di PnG "standard".
     * Inizializza l'oro e i punti statistica a 0, assegna l'equipaggiamento iniziale della classe,
     * applica i modificatori di razza alle statistiche base e imposta HP e Mana al massimo.
     *
     * @param classe la classe iniziale del personaggio.
     * @param razza  la razza del personaggio con i relativi modificatori.
     * @param nome   il nome del personaggio.
     */
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

    /**
     * Costruttore per la creazione di un nuovo Personaggio Non Giocante (PnG) da parte del Master.
     * Permette di definire direttamente statistiche base, oro e punti statistica iniziali.
     *
     * @param classe          la classe del PnG.
     * @param razza           la razza del PnG.
     * @param statisticaBase  le statistiche base assegnate al PnG.
     * @param nome            il nome del PnG.
     * @param oro             l'oro iniziale in dotazione.
     * @param puntiStatistica i punti statistica spendibili iniziali.
     */
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


    /**
     * Costruttore per il Dao.
     * Ricostruisce lo stato esatto salvato senza applicare bonus iniziali o equipaggiamento di default.
     * Le collezioni (inventari, abilità) vengono inizializzate vuote e dovranno essere popolate
     * successivamente con query dedicate.
     *
     * @param id              l'identificativo univoco del personaggio nel database.
     * @param nome            il nome del personaggio.
     * @param classe          la classe del personaggio.
     * @param razza           la razza del personaggio.
     * @param statisticaBase  le statistiche base caricate dal database.
     * @param hpCorrenti      gli HP correnti salvati al momento dell'ultimo salvataggio.
     * @param manaCorrente    il mana corrente salvato al momento dell'ultimo salvataggio.
     * @param oro             l'oro posseduto nel database.
     * @param puntiStatistica i punti statistica non ancora spesi.
     * @param isPg            true se è un Personaggio Giocante, false se è un PnG.
     */
    public Personaggio(int id, String nome, Classe classe, Razza razza, Statistica statisticaBase,
                       int hpCorrenti, int manaCorrente, int oro, int puntiStatistica, boolean isPg) {
        this.id = id;
        this.nome = nome;
        this.classe = classe;
        this.razza = razza;
        this.statisticaBase = statisticaBase;
        this.hpCorrenti = hpCorrenti;
        this.manaCorrente = manaCorrente;
        this.oro = oro;
        this.puntiStatistica = puntiStatistica;
        this.isPg = isPg;

        this.inventarioConsumabili = new HashMap<>();
        this.inventarioEquipaggiabili = new HashMap<>();
        this.listaAbilita = new ArrayList<>();

        calcolaStatisticheFinali();
    }




    /**
     * Incrementa un attributo base spendendo i punti statistica disponibili.
     *
     * @param punti     il numero di punti da assegnare all'attributo.
     * @param attributo il nome dell'attributo da incrementare.
     * @throws IllegalArgumentException se i punti richiesti superano quelli disponibili o sono minori di 1.
     */
    public void spendipuntiStatistica(int punti, String attributo) {
        if (punti > puntiStatistica || punti < 1) throw new IllegalArgumentException("Valore punti non valido.");
        statisticaBase.incrementa(attributo, punti);
        puntiStatistica -= punti;
        aggiornaStatoPG();
    }

    /**
     * Gestisce l'acquisto di un oggetto verificando la disponibilità di oro.
     * Inserisce l'oggetto nella mappa corretta in base al tipo.
     *
     * @param oggetto l'oggetto da acquistare.
     * @throws IllegalArgumentException se l'oggetto è nullo, l'oro è insufficiente o l'equipaggiabile è già posseduto.
     */
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

    /**
     * Vende un oggetto dall'inventario, aggiungendo all'oro la metà del suo costo di listino.
     *
     * @param oggetto l'oggetto da vendere.
     * @throws IllegalArgumentException se l'oggetto è nullo o non è presente in inventario.
     */
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

    /**
     * Equipaggia un oggetto se presente in inventario e se sono soddisfatti i requisiti minimi basati sulle statistiche base.
     *
     * @param equipaggiabile l'oggetto da equipaggiare.
     * @return {@code true} se l'oggetto viene equipaggiato, {@code false} se i requisiti non sono soddisfatti.
     * @throws IllegalArgumentException se l'oggetto è nullo o non è posseduto.
     */
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

    /**
     * Rimuove l'equipaggiamento di un oggetto, riportando il suo stato a non equipaggiato.
     *
     * @param equipaggiabile l'oggetto da rimuovere.
     * @throws IllegalArgumentException se l'oggetto è nullo o non è posseduto.
     */
    public void rimuoviEquipaggiamento(OggettoEquipaggiabile equipaggiabile) {
        if (equipaggiabile == null) throw new IllegalArgumentException("Oggetto non valido.");
        if (inventarioEquipaggiabili.containsKey(equipaggiabile)) {
            inventarioEquipaggiabili.replace(equipaggiabile, false);
            aggiornaStatoPG();
        } else throw new IllegalArgumentException("Non possiedi quest'oggetto!");
    }

    /**
     * Utilizza un oggetto consumabile, applicando i suoi effetti di ripristino HP/Mana e riducendone la quantità.
     *
     * @param oggetto l'oggetto consumabile da usare.
     * @throws IllegalArgumentException se l'oggetto non è presente in inventario.
     */
    public void usaConsumabile(OggettoConsumabile oggetto) {
        if (inventarioConsumabili.containsKey(oggetto)) {
            ripristinaHP(oggetto.getRipristinoHP());
            ripristinaMana(oggetto.getRipristinoMana());
            rimuoviConsumabile(oggetto, 1);
        } else throw new IllegalArgumentException("Non possiedi quest'oggetto!");
    }


    /** @return l'identificativo univoco del personaggio. */
    public int getId() { return id; }

    /** @return il nome del personaggio. */
    public String getNome() { return nome; }

    /** @return le statistiche base (senza bonus da equipaggiamento). */
    public Statistica getStatisticheBase() { return statisticaBase; }

    /** @return le statistiche finali (comprensive di bonus da oggetti equipaggiati). */
    public Statistica getStatisticheFinali() { return statisticaFinali; }

    /** @return la classe del personaggio. */
    public Classe getClasse() { return classe; }

    /** @return la razza del personaggio. */
    public Razza getRazza() { return razza; }

    /** @return gli HP correnti. */
    public int getHpCorrenti() { return hpCorrenti; }

    /** @return il mana corrente. */
    public int getManaCorrente() { return manaCorrente; }

    /** @return i punti statistica ancora spendibili. */
    public int getPuntiStatistica() { return puntiStatistica; }

    /** @return l'oro posseduto. */
    public int getOro() { return oro; }

    /** @return {@code true} se è un PG, {@code false} se è un PnG. */
    public boolean isPg() { return isPg; }

    /** @return una vista non modificabile della lista delle abilità apprese. */
    public List<Abilita> getListaAbilita() { return Collections.unmodifiableList(listaAbilita); }

    /** @return una vista non modificabile dell'inventario dei consumabili. */
    public Map<OggettoConsumabile, Integer> getInventarioConsumabili() { return Collections.unmodifiableMap(inventarioConsumabili); }

    /** @return una vista non modificabile dell'inventario degli oggetti equipaggiabili. */
    public Map<OggettoEquipaggiabile, Boolean> getInventarioEquipaggiabili() { return Collections.unmodifiableMap(inventarioEquipaggiabili); }

    public void setOro(int oro) {
        this.oro = oro;
    }

    public void setStatisticaBase(Statistica statistica){statisticaBase = statistica;}

    public void setHpCorrenti(int hpCorrenti) {
        this.hpCorrenti = hpCorrenti;
    }

    public void setManaCorrente(int manaCorrente) {
        this.manaCorrente = manaCorrente;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Aggiunge un'abilità alla lista del personaggio se prevista tra quelle sbloccabili dalla classe.
     *
     * @param abilita l'abilità da aggiungere.
     */
    public void addAbilita(Abilita abilita) {
        if (abilita == null) return;
        if (!classe.getAbilitaSbloccabili().contains(abilita)) return;
        if (!listaAbilita.contains(abilita)) listaAbilita.add(abilita);
    }

    /**
     * Rimuove un oggetto equipaggiabile dall'inventario.
     *
     * @param oggetto l'oggetto da rimuovere.
     */
    public void rimuoviEquipaggiabile(OggettoEquipaggiabile oggetto) {
        inventarioEquipaggiabili.remove(oggetto);
    }

    /**
     * Riduce la quantità di un oggetto consumabile o lo rimuove se la quantità scende a zero.
     *
     * @param oggetto  l'oggetto consumabile da ridurre.
     * @param quantita la quantità da sottrarre.
     */
    public void rimuoviConsumabile(OggettoConsumabile oggetto, int quantita) {
        if (inventarioConsumabili.get(oggetto) <= quantita) {
            inventarioConsumabili.remove(oggetto);
        } else {
            inventarioConsumabili.replace(oggetto, inventarioConsumabili.get(oggetto) - quantita);
        }
    }

    /**
     * Aggiunge un oggetto equipaggiabile all'inventario impostandolo come non equipaggiato.
     *
     * @param oggetto l'oggetto da inserire.
     */
    public void addEquipaggiabile(OggettoEquipaggiabile oggetto) {
        if (!(inventarioEquipaggiabili.containsKey(oggetto))) {
            inventarioEquipaggiabili.put(oggetto, false);
        }
    }

    /**
     * Incrementa la quantità di un oggetto consumabile o lo inserisce se non presente.
     *
     * @param oggetto  l'oggetto consumabile da aggiungere o incrementare.
     * @param quantita il numero di unità da inserire.
     */
    public void addConsumabile(OggettoConsumabile oggetto, int quantita) {
        if (inventarioConsumabili.containsKey(oggetto)) {
            inventarioConsumabili.replace(oggetto, inventarioConsumabili.get(oggetto) + quantita);
        } else {
            inventarioConsumabili.put(oggetto, quantita);
        }
    }

    /**
     * Svuota completamente l'inventario.
     * Usato dal Controller esclusivamente per sincronizzare i dati dal database.
     */
    public void svuotaInventari() {
        this.inventarioConsumabili.clear();
        this.inventarioEquipaggiabili.clear();
    }

    /**
     * Inserisce o aggiorna un equipaggiabile forzando lo stato letto dal DB,
     * aggirando i normali controlli di equipaggiamento in-game.
     */
    public void impostaStatoEquipaggiabile(OggettoEquipaggiabile oggetto, boolean equipaggiato) {
        this.inventarioEquipaggiabili.put(oggetto, equipaggiato);
    }

    /**Incrementa i punti statistica del personaggio
     *
     * @param quantita i punti da assegnare
     */
    public void addPuntiStatistica(int quantita){
        puntiStatistica += quantita;
    }

    /**
     * Ripristina gli HP del personaggio senza superare il valore massimo delle statistiche finali.
     *
     * @param valore la quantità di HP da ripristinare.
     */
    public void ripristinaHP(int valore){
        if(valore > 0){
            this.hpCorrenti += valore;
            if(hpCorrenti > this.getStatisticheFinali().getHpMax()){
                hpCorrenti = statisticaFinali.getHpMax();
            }
        }
    }

    /**
     * Ripristina il mana del personaggio senza superare il valore massimo delle statistiche finali.
     *
     * @param valore la quantità di mana da ripristinare.
     */
    public void ripristinaMana(int valore){
        if(valore > 0){
            this.manaCorrente += valore;
            if(manaCorrente > this.getStatisticheFinali().getManaMax()){
                manaCorrente = statisticaFinali.getManaMax();
            }
        }
    }

    /**
     * Ricalcola le statistiche finali sommando alle statistiche base i bonus degli oggetti attualmente equipaggiati.
     */
    private void calcolaStatisticheFinali() {
        if (statisticaBase == null) {
            this.statisticaFinali = null;
            return;
        }
        statisticaFinali = new Statistica(statisticaBase);
        for(Map.Entry<OggettoEquipaggiabile, Boolean> entry : inventarioEquipaggiabili.entrySet()){
            if(entry.getValue()){
                statisticaFinali.aggiungiBonus(entry.getKey().getBonus());
            }
        }
    }

    /**
     * Inserisce in inventario gli oggetti previsti come equipaggiamento iniziale dalla classe.
     */
    private void inizializzaEquipaggiamentoIniziale(Classe classe) {
        for (Oggetto o : classe.getEquipaggiamentoIniziale()) {
            if (o instanceof OggettoConsumabile) {
                inventarioConsumabili.put((OggettoConsumabile) o, 1);
            } else if (o instanceof OggettoEquipaggiabile) {
                inventarioEquipaggiabili.put((OggettoEquipaggiabile) o, false);
            }
        }
    }

    /**
     * Aggiorna lo stato del personaggio: rimuove l'equipaggiamento degli oggetti i cui requisiti
     * non sono più soddisfatti dalle statistiche base e ricalcola le statistiche finali.
     */
    public void aggiornaStatoPG() {
        for (Map.Entry<OggettoEquipaggiabile, Boolean> entry : inventarioEquipaggiabili.entrySet()) {
            if (entry.getValue() && !statisticaBase.soddisfa(entry.getKey().getRequisiti())) {
                inventarioEquipaggiabili.replace(entry.getKey(), false);
            }
        }
        calcolaStatisticheFinali();
    }

    /**
     * Restituisce una stringa formattata contenente i dati e le statistiche del personaggio.
     *
     * @return la rappresentazione testuale del personaggio.
     */
    @Override
    public String toString() {
        return String.format((isPg() ? "PG:%n{%n" : "PnG:%n{%n") + "Nome: %s%nRazza: %s%nClasse: %s%nHP: %d/%d%nMana: %d/%d%n" +
                        "Forza: %d%nDestrezza: %d%nCostituzione: %d%nIntelligenza: %d%nCarisma: %d%nFede: %d%nFortuna: %d%n" +
                        "Oro: %d%nPunti statistica: %d%n}", nome, razza, classe, hpCorrenti, statisticaFinali.getHpMax(),
                manaCorrente, statisticaFinali.getManaMax(), statisticaFinali.getForza(), statisticaFinali.getDestrezza(),
                statisticaFinali.getCostituzione(), statisticaFinali.getIntelligenza(), statisticaFinali.getCarisma(),
                statisticaFinali.getFede(), statisticaFinali.getFortuna(), oro, puntiStatistica);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personaggio that = (Personaggio) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}