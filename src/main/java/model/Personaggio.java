package model;
import java.util.ArrayList;
public class Personaggio {
       private String nome;
       private  int livello;
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

                            this.statisticheBase= new Statistica(statisticheBase);
                            this.statisticheBase.sommaStatistiche(this.razzaPersonaggio.getModificatoriRazza());

                            this.inventario = new ArrayList<>();
                            this.equipaggiamento = new ArrayList<>(classePersonaggio.getEquipaggiamentoIniziale());

                              this.aggiornaStatoPG();

                      }

                 public Statistica  getStatisticaBase(){
                     return this.statisticheBase;
                   }

                 public ArrayList<Oggetto> getInventario(){
                        return this.inventario;
                 }

                 public Campagna getCampagnaPersonaggio() {
                        return campagnaPersonaggio;
                          }

              public void setStatisticheBase(Statistica nuovaStatisticaBase){
                        this.statisticheBase= nuovaStatisticaBase;
                   }

                 public void setInventario(ArrayList<Oggetto> nuovoInventario){
                     this.inventario= nuovoInventario;
                 }

                   public void setCampagnaPersonaggio(Campagna nuovoCampagnaPersonaggio) {
                            this.campagnaPersonaggio = nuovoCampagnaPersonaggio;
                        }

                      public void addPuntiStatistica(int puntiRicevuti) {

                          this.puntiStatistica += puntiRicevuti;
                           }



               public Statistica getStatisticheFinali() {
                   Statistica statisticheTotali = new Statistica(this.statisticheBase);

                      for (OggettoEquipaggiabile oggettoEquipaggiato : equipaggiamento) {
                               if (oggettoEquipaggiato.getIsEquipaggiato()==true) {
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
                     int puntiRichiesti = statDaIncrementare.getForza() + statDaIncrementare.getDestrezza() +
                        statDaIncrementare.getCostituzione() + statDaIncrementare.getIntelligenza() +
                         statDaIncrementare.getFede() + statDaIncrementare.getCarisma() +
                        statDaIncrementare.getFortuna();

                      if (puntiRichiesti > 0 && this.puntiStatistica >= puntiRichiesti) {
                       this.statisticheBase.sommaStatistiche(statDaIncrementare);
                      this.puntiStatistica -= puntiRichiesti; // Sottraiamo il numero esatto di punti
                     aggiornaStatoPG(); // Ricalcola tutto (compresi eventuali nuovi requisiti soddisfatti)
                      }
                    }


              public void aggiornaStatoPG() {

                  for (OggettoEquipaggiabile oggettoDaControllare : equipaggiamento) {
                      if ((oggettoDaControllare.getIsEquipaggiato()==true) && (this.statisticheBase.soddisfaRequisito(oggettoDaControllare.getRequisiti())==false)) {
                          oggettoDaControllare.setIsEquipaggiato(false);
                      }
                  }


               this.statisticheFinali = this.getStatisticheFinali();// ricalcolo le stat finali PG
                this.livello = this.statisticheFinali.calcolaLivello(); //calcolo il livello dopo le modifiche

                if (this.statisticheFinali.getHpCorrenti() > this.statisticheFinali.getMaxHp()) {
                    this.statisticheFinali.setHpCorrenti(this.statisticheFinali.getMaxHp());
                       }

                 if (this.statisticheFinali.getManaCorrenti() > this.statisticheFinali.getMaxMana()) {
                  this.statisticheFinali.setManaCorrenti(this.statisticheFinali.getMaxMana());
                    }



                   }
                           }







