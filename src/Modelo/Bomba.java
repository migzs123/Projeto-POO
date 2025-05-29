
package Modelo;

import Auxiliar.Desenho;
import javax.swing.ImageIcon;

public class Bomba extends Personagem{
    private ImageIcon explosao;
    private int raioExplosao = 2;

    public Bomba(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        carregarSprites();
        this.transponivel =false;
    }

    @Override
    protected void carregarSprites(){
        explosao = carregarImagem("explosao.png");
    }
        
   public void explodir(){
       this.imagem = null;
       int linha = this.getPosicao().getLinha();
       int coluna = this.getPosicao().getColuna();
       
       destruirGelo(linha, coluna);
       for (int i = 0; i < raioExplosao; i++) {
            destruirGelo(linha + i, coluna);     // abaixo
            destruirGelo(linha - i, coluna);     // acima
            destruirGelo(linha, coluna + i);     // direita
            destruirGelo(linha, coluna - i);     // esquerda
        }
   }
   
   private void destruirGelo(int linha, int coluna){
       Personagem p = faseAtual.getPersonagem(linha, coluna);
       if(p != null && p instanceof Hero){
           faseAtual.carregarFase(faseAtual.getFase());
       }
       new Thread(()->{
           try {
               synchronized (faseAtual) {
                    faseAtual.setTile(linha, coluna, new Tile("explosao.png", true, true, false));
                }
               Thread.sleep(500);
               synchronized(faseAtual) {
                    faseAtual.setTile(linha, coluna, new Tile("water.png", true, true, false));
               }
               if (this.getPosicao().getLinha() == linha && this.getPosicao().getColuna() == coluna) {
                    faseAtual.RemoveEntidade(this);
                }
           } catch (InterruptedException ex) {
               System.getLogger(Bomba.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
           }
       }).start();
   }
}
