package model;
import java.util.ArrayList;
public class Personaggio {
       private String nome;
       int livello;
       private Statistica statisticheBase;
       private Classe classePersonaggio;
       private Razza razzaPersonaggio;
       private int puntiStatistica;
       private boolean isPg;
       private Statistica statisticheFinali;
       private int oroPosseduto;
       private Campagna campagnaPersonaggio;
       private ArrayList<Oggetto>  inventario;
       private ArrayList<OggettoEquipaggiabile> equipaggiamento;

           public Personaggio(String nome, Razza razzaPersonaggio, Classe classePersonaggio, Statistica statisticheBase,
                       boolean  isPg, Campagna campagnaPersonaggio){

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

                 //richiamo metodo calcolaLivello così in automatico c'è l'ho già

              //cosi di base abbiamo l'eqquipaggiamento legato alla classe
               this.inventario = new ArrayList<>();
               this.equipaggiamento = new ArrayList<>(classePersonaggio.getEquipaggiamentoIniziale());

                      }

                 public Statistica  getStatisticaBase(){
                     return this.statisticheBase;
                   }

                 public ArrayList<Oggetto> getInventario(){
                        return this.inventario;
                 }

                 public void setStatisticheBase(Statistica nuovaStatisticaBase){
                        this.statisticheBase= nuovaStatisticaBase;
                   }

                 public void setInventario(ArrayList<Oggetto> nuovoInventario){
                     this.inventario= nuovoInventario;
                 }


                 public void addPuntiStatistica(int puntiRicevuti) {

                          this.puntiStatistica += puntiRicevuti;
                           }



               public Statistica getStatisticheFinali() {
                  Statistica statisticheTotali = new Statistica(this.statisticheBase);

                    statisticheTotali.sommaStatistiche(this.razzaPersonaggio.getModificatoriRazza());

                      for (OggettoEquipaggiabile oggettoEquipaggiato : equipaggiamento) {
                               if (oggettoEquipaggiato.getIsEquipaggiato()) {
                      statisticheTotali.sommaStatistiche(oggettoEquipaggiato.getBonusStat());
                              }
                       }

                          return statisticheTotali;
                         }




              public void Equipaggia( OggettoEquipaggiabile oggettoDaEquipaggiare){
                  if(this.statisticheBase.soddisfaRequisito(oggettoDaEquipaggiare.getRequisiti()) == true){
                       oggettoDaEquipaggiare.setIsEquipaggiato(true);
                       aggiornaStatoPG();
                  }
              }


             public void disequipaggia(OggettoEquipaggiabile oggettoDaDisequipaggiare) {
                    if ((oggettoDaDisequipaggiare != null) && (oggettoDaDisequipaggiare.getIsEquipaggiato()==true)) {
                   oggettoDaDisequipaggiare.setIsEquipaggiato(false); // Cambia lo stato interno dell'oggetto
                       aggiornaStatoPG();
                            }
                          }

                  public void sommaOro(int quantita) {
                         if (quantita > 0) {
                        this.oroPosseduto += quantita;
                            }
                            }

                  public boolean sottraiOro(int costo) {
                       if (costo <= this.oroPosseduto) {
                           this.oroPosseduto -= costo;
                              return true;
                             }
                            return false;
                             }

                  public void acquistaOggetto(Oggetto oggettoDaAcquistare) {
                     if (sottraiOro(oggettoDaAcquistare.getCosto()) ==true) {
                          this.inventario.add(oggettoDaAcquistare);
                           }
                       }



              public void spendiPuntiStatistica(Statistica statDaIncrementare) {
                  if (this.puntiStatistica > 0) {
                      this.statisticheBase.sommaStatistiche(statDaIncrementare);
                      this.puntiStatistica--;
                      aggiornaStatoPG();
                  }
              }


              public void aggiornaStatoPG() {
                this.statisticheFinali = this.getStatisticheFinali();// ricalcolo le stat finali PG
                this.livello = this.statisticheFinali.calcolaLivello(); //calcolo il livello dopo le modifiche

                if (this.statisticheFinali.getHpCorrenti() > this.statisticheFinali.getMaxHp()) {
                this.statisticheFinali.setHpCorrenti(this.statisticheFinali.getMaxHp());

                 if (this.statisticheFinali.getManaCorrenti() > this.statisticheFinali.getMaxMana()) {
                  this.statisticheFinali.setManaCorrenti(this.statisticheFinali.getMaxMana());
                    }

                  for (OggettoEquipaggiabile oggettoDaControllare : equipaggiamento) {
                       if ((oggettoDaControllare.getIsEquipaggiato()==true) && (this.statisticheBase.soddisfaRequisito(oggettoDaControllare.getRequisiti())==false)) {
                            oggettoDaControllare.setIsEquipaggiato(false);
                        }
                    }

                   }
                           }






}
