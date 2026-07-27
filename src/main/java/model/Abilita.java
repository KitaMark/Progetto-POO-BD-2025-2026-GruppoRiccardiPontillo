package model;

/**
 * Rappresenta un'abilità, incantesimo o potere speciale sbloccabile nel sistema di gioco.
 * Ha una funzione puramente descrittiva e il suo esito narrativo è gestito dal Master[cite: 2].
 */
public class Abilita {
    private int id;
    private String nome;
    private String descrizione;
    private Classe classe;

    /**
     * Crea una nuova abilità associandola a una specifica classe.
     *
     * @param nome        il nome dell'abilità[cite: 2].
     * @param descrizione la descrizione dell'effetto dell'abilità[cite: 2].
     * @param classe      la classe a cui appartiene l'abilità[cite: 2].
     */
    public Abilita(String nome, String descrizione, Classe classe){
        this.nome = nome;
        this.descrizione = descrizione;
        this.classe = classe;
    }

    /**
     * Crea una nuova abilità senza assegnarla immediatamente a una classe.
     * Utile per la creazione transiente da parte del Controller prima dell'inserimento nel database.
     *
     * @param nome        il nome dell'abilità[cite: 2].
     * @param descrizione la descrizione dell'effetto dell'abilità[cite: 2].
     */
    public Abilita(String nome, String descrizione){
        this.nome = nome;
        this.descrizione = descrizione;
    }

    /**
     * Restituisce l'identificativo univoco dell'abilità nel database.
     *
     * @return l'identificativo numerico.
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta il nuovo identificativo generato dal database.
     *
     * @param id il nuovo identificativo da impostare.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce il nome dell'abilità.
     *
     * @return il nome dell'abilità[cite: 2].
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la descrizione dell'effetto dell'abilità.
     *
     * @return la descrizione testuale.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Restituisce la classe associata all'abilità.
     *
     * @return l'oggetto {@link Classe} di riferimento[cite: 2].
     */
    public Classe getClasse() {
        return classe;
    }

    /**
     * Imposta un nuovo nome per l'abilità.
     *
     * @param nome il nuovo nome da impostare[cite: 2].
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Imposta una nuova descrizione per l'abilità.
     *
     * @param descrizione la nuova descrizione dell'effetto da impostare[cite: 2].
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Associa l'abilità a una nuova classe.
     *
     * @param classe la nuova classe da associare[cite: 2].
     */
    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    /**
     * Confronta questa abilità con un altro oggetto per stabilirne l'uguaglianza.
     * <p>
     * Due abilità sono considerate identiche se possiedono lo stesso nome,
     * ignorando le differenze tra maiuscole/minuscole e gli spazi vuoti iniziali/finali[cite: 2].
     * Questo permette il corretto funzionamento dei metodi delle API Collection
     * (come {@code contains()} e {@code remove()})[cite: 2].
     * </p>
     *
     * @param o L'oggetto da confrontare[cite: 2].
     * @return {@code true} se gli oggetti rappresentano la stessa abilità logica, {@code false} altrimenti[cite: 2].
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Abilita abilita = (Abilita) o;

        if (this.nome == null || abilita.nome == null) return false;
        return this.nome.trim().equalsIgnoreCase(abilita.nome.trim());
    }

    /**
     * Genera un codice hash univoco per l'abilità basato sul suo nome formattato.
     * <p>
     * L'hash viene calcolato sulla stringa in minuscolo e senza spazi per mantenere
     * il contratto con il metodo {@code equals()}[cite: 2].
     * </p>
     *
     * @return il codice hash calcolato[cite: 2].
     */
    @Override
    public int hashCode() {
        return this.nome != null ? this.nome.trim().toLowerCase().hashCode() : 0;
    }

    /**
     * Restituisce la rappresentazione testuale dell'abilità.
     *
     * @return stringa formattata con i dettagli dell'oggetto[cite: 2].
     */
    @Override
    public String toString() {
        return String.format("Abilità [ID: %d]: %s%n Effetto: %s%n Classe: %s%n", id, nome, descrizione, classe);
    }
}