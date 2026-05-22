package model;

public class Master extends Utente{
    private Campagna campagna;

    public Master(String email, String username, String password){
        super(email, username, password);
        campagna = null;
    }

    //TODO: Valutare metodi che potrebbero essere responsabilità del controller.

   public void creaCampagna(String nome, int maxGiocatori){
        campagna = new Campagna(nome, maxGiocatori, this);
   }

   public void assegnaPuntiStatistica(Personaggio pg, int punti){
        if((campagna.getListaPG().contains(pg) || campagna.getListaPnG().contains(pg)) && pg != null){
            pg.setPuntiStatistica(punti);
        } else throw new IllegalArgumentException("Il personaggio inserito non appartiene alla campagna.");
   }

   public void modificaStatistichePersonaggio(Personaggio pg, int forza, int destrezza, int costituzione, int intelligenza,
   int fede, int carisma, int fortuna, int HpMax, int ManaMax){
        if((campagna.getListaPG().contains(pg) || campagna.getListaPnG().contains(pg)) && pg != null){
            Statistiche stat = new Statistiche(costituzione, forza, destrezza, intelligenza, fede,
                    carisma, fortuna, HpMax, ManaMax);
            pg.setStatisticheBase(stat);
        }
        else throw new IllegalArgumentException("il personaggio inserito non appartiene alla campagna.");
   }

   public void aggiungiOggetto(Personaggio pg, Oggetto oggetto){
        if(oggetto == null) throw new IllegalArgumentException("Oggetto non valido.");
        if((campagna.getListaPG().contains(pg) || campagna.getListaPnG().contains(pg)) && pg != null){
           if(oggetto instanceof OggettoConsumabile){
               pg.addConsumabile((OggettoConsumabile) oggetto , 1);
           } else{
               pg.addEquipaggiabile((OggettoEquipaggiabile) oggetto);
           }
       } else throw new IllegalArgumentException("Il personaggio inserito non appartiene alla campagna.");
   }

   public void rimuoviOggetto(Personaggio pg, Oggetto oggetto){
        if(oggetto == null) throw new IllegalArgumentException("Oggetto non valido.");
        if((campagna.getListaPG().contains(pg) || campagna.getListaPnG().contains(pg)) && pg != null){
           if(oggetto instanceof OggettoConsumabile){
               pg.rimuoviConsumabile((OggettoConsumabile) oggetto , pg.getInventarioConsumabili().get((OggettoConsumabile) oggetto));
           } else{
               pg.rimuoviEquipaggiabile((OggettoEquipaggiabile) oggetto);
           }
       } else throw new IllegalArgumentException("Il personaggio inserito non appartiene alla campagna.");
   }


   public void creaPersonaggio(Classe classe, Razza razza, String nome){
        Personaggio png = new Personaggio(classe, razza, nome, false);
        campagna.getListaPnG().add(png);
   }

    public void creaPersonaggio(Classe classe, Razza razza, Statistiche statisticheBase, String nome,
                                int oro, int puntiStatistica) {
        Personaggio png = new Personaggio( classe, razza, statisticheBase, nome,
         oro, puntiStatistica);
        campagna.getListaPnG().add(png);
    }

    public boolean aggiungiPersonaggio(Personaggio personaggio){
        if(personaggio.isPg() && !campagna.isIniziata()){
            campagna.getListaPG().add(personaggio);
            return true;
        } else if (!personaggio.isPg()){
            campagna.getListaPnG().add(personaggio);
            return true;
        }
        return false;
    }

    public void rimuoviPersonaggio(Personaggio personaggio){
        if(personaggio.isPg()){
            campagna.getListaPG().remove(personaggio);
        } else{
            campagna.getListaPnG().remove(personaggio);
        }
    }

    public void aggiungiAbilitaPersonaggio(Personaggio personaggio, Abilita abilita){
        if(abilita == null || abilita.getClasse() != personaggio.getClasse()){
            throw new IllegalArgumentException(("Abilità selezionata non valida."));
        }
        if(personaggio == null ||
                !(campagna.getListaPG().contains(personaggio) || campagna.getListaPnG().contains(personaggio))){
            throw new IllegalArgumentException("Il personaggio inserito non appartiene alla campagna.");
        }

        personaggio.addAbilita(abilita);
    }

    public void modificaOroPersonaggio(Personaggio pg, int oro){
        if((campagna.getListaPG().contains(pg) || campagna.getListaPnG().contains(pg)) && pg != null){
            pg.setOro(oro);
        } else throw new IllegalArgumentException("Il personaggio inserito non appartiene alla campagna.");
    }

    public void modificaStatoCampagna(boolean isIniziata){
        campagna.setIniziata(isIniziata);
    }

    public boolean modificaMaxGiocatori(int max){
        if(!campagna.isIniziata()){
            campagna.setMaxGiocatori(max);
            return true;
        }
        return false;
    }

    public boolean aggiungiGiocatore(Giocatore giocatore){
        if(giocatore == null) throw new IllegalArgumentException("Giocatore selezionato non valido.");
        if(campagna.getPartecipanti().size() < campagna.getMaxGiocatori() && !campagna.isIniziata()){
            campagna.getPartecipanti().add(giocatore);
            return true;
        }
        return false;
    }

    public void rimuoviGiocatore(Giocatore giocatore){
        if(giocatore == null) throw new IllegalArgumentException("Giocatore selezionato non valido.");
        if(campagna.getPartecipanti().contains(giocatore)){
            campagna.getPartecipanti().remove(giocatore);
        }
    }
}
