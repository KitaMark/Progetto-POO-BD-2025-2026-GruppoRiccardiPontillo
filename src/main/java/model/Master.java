package model;

public class Master extends Utente {
    private Campagna campagna;


    // Costruttore completo
    public Master(String email, String username, String password) {
        super(email, username, password);
        this.campagna = null;
    }

    //GETTER

    public Campagna getCampagna() {
        return campagna;
    }

    //SETTER
    public void setCampagna(Campagna campagna) {
        this.campagna = campagna;
    }

}