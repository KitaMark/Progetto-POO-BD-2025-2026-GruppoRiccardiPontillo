package model;
import java.util.ArrayList;
public class Personaggio {
       private String nome;
       private Statistica statisticheBase;
       private Classe classePersonaggio;
       private Razza razzaPersonaggio;
       private int puntiStatistica;
       private boolean isPg;
       private Statistica statisticheFinali;
       private int oroPosseduto;
       private Campagna campagnaPersonaggio;
       private ArrayList<Oggetto>  inventario;

           public Personaggio(String nome, Statistica statisticheBase, Classe classePersonaggio, Razza razzaPersonaggio,
                       boolean  isPg,Statistica statisticheFinali, Campagna campagnaPersonaggio){

                            this.nome=nome;
                            this.razzaPersonaggio= razzaPersonaggio;
                            this.classePersonaggio= classePersonaggio;
                            this.campagnaPersonaggio= campagnaPersonaggio;
                            this.isPg= isPg;
                            this.oroPosseduto= 100;
                            this.puntiStatistica= 0;

                            this.statisticheBase= new Statistica();

                            //metodo per applicare i bonus stat dovuti dalla razza (meglio qua)


                           this.statisticheFinali= new Statistica();

                           //metodo per aggiornare stat finali (meglio qua)

              //cosi di base abbiamo l'eqquipaggiamento legato alla classe
               this.inventario = new ArrayList<>(classePersonaggio.getEquipaggiamentoIniziale());


                      }
}
