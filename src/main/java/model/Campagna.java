package model;

public class Campagna {
       private String nomeCampagna;
       private int maxGiocatori;
       private boolean iniziata;

         public Campagna(String nomeCampagna, int maxGiocatori){
                this.nomeCampagna= nomeCampagna;
                this.maxGiocatori=maxGiocatori;
                this.iniziata= false;
         }
}
