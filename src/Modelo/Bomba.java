
package Modelo;

import Auxiliar.Desenho;
import javax.swing.ImageIcon;

public class Bomba extends Personagem{
    private ImageIcon explosao;
    private int raioExplosao = 2;
    private transient volatile boolean pararExplosao = false;

    public Bomba(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        carregarSprites();
        this.transponivel =false;
    }

    @Override
    protected void carregarSprites(){
        explosao = carregarImagem("explosao.png");
    }
        
   public void explodir() {
        this.imagem = null;
        int linha = this.getPosicao().getLinha();
        int coluna = this.getPosicao().getColuna();
        final int faseIDAtual = faseAtual.getFaseID(); // snapshot da versão da fase

        if (destruirGelo(linha, coluna, faseIDAtual)) return;

        for (int i = 1; i < raioExplosao; i++) {
            if (destruirGelo(linha + i, coluna, faseIDAtual)) return;
            if (destruirGelo(linha - i, coluna, faseIDAtual)) return;
            if (destruirGelo(linha, coluna + i, faseIDAtual)) return;
            if (destruirGelo(linha, coluna - i, faseIDAtual)) return;
        }
    }

   
   private boolean destruirGelo(int linha, int coluna, int faseIDLocal) {
    if (faseAtual.getHero().comparaPosicao(linha, coluna)) {
        faseAtual.reiniciarFase();
        return true;
    }
    if(faseAtual.getPersonagem(linha, coluna) instanceof Botao || faseAtual.getPersonagem(linha, coluna) instanceof Bomba){}
    else{
        faseAtual.RemoveEntidade(faseAtual.getPersonagem(linha, coluna));
    }

    if (!faseAtual.getTile(linha, coluna).nomeImagem.equals("ground.png")) {
        return false;
    }

    new Thread(() -> {
        try {
            if (faseAtual.getFaseID() != faseIDLocal) return; // fase mudou
            synchronized (faseAtual) {
                faseAtual.setTile(linha, coluna, new Tile("explosao.png", true, true, false));
            }

            Thread.sleep(500);

            if (faseAtual.getFaseID() != faseIDLocal) return; // fase mudou

            synchronized (faseAtual) {
                faseAtual.setTile(linha, coluna, new Tile("water.png", true, true, false));
            }

            if (this.getPosicao().getLinha() == linha && this.getPosicao().getColuna() == coluna) {
                faseAtual.RemoveEntidade(this);
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }).start();

    return false;
}


}
