package Modelo;

import Auxiliar.Desenho;
import Auxiliar.Som;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Hero extends Personagem {

    private transient Som falhouSom;
    private transient Som passouSom;
    private boolean morto = false;
    private ImageIcon upImage, downImage, leftImage, rightImage;

    private boolean hasKey = false;

    public Hero(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = downImage;
        falhouSom = new Som("/sounds/fail.wav"); 
        passouSom = new Som("/sounds/win.wav"); 
    }

    @Override
    public void carregarSprites() {
        upImage = carregarImagem(nomeImagem.replace(".png", "") + "_up.png");
        downImage = carregarImagem(nomeImagem.replace(".png", "") + "_down.png");
        leftImage = carregarImagem(nomeImagem.replace(".png", "") + "_left.png");
        rightImage = carregarImagem(nomeImagem.replace(".png", "") + "_right.png");
    }
    
    public void getKey(){
        this.hasKey = true;
        System.out.println("hasKey status:" + this.hasKey);
    }
    
    public boolean getKeyStatus(){
        return this.hasKey;
    }

    public void voltaAUltimaPosicao() {
        this.pPosicao.volta();
    }

    public boolean setPosicao(int linha, int coluna) {
        return this.pPosicao.setPosicao(linha, coluna);
    }

    private boolean validaPosicao() {
        if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
            this.voltaAUltimaPosicao();
            return false;
        }

        Tile tileAtual = faseAtual.getTile(this.getPosicao().getLinha(), this.getPosicao().getColuna());

        if (tileAtual != null && tileAtual.isFim()) {
            passouSom.tocarUmaVez();
            faseAtual.proximaFase();
            return false;
        }

        for (Personagem p : new ArrayList<>(faseAtual.getPersonagens())) {
            if (p instanceof Food) {
                if (p.getPosicao().igual(this.getPosicao())) {
                    ((Food) p).checarColisao();
                }
            }
            if (p instanceof Botao) {
                if (p.getPosicao().igual(this.getPosicao())) {
                    ((Botao) p).checarColisao();
                }
            if (p instanceof Inimigo) {
                if (p.getPosicao().igual(this.getPosicao())) {
                    ((Inimigo) p).checarColisao();
                }
            }
            }

            if (p instanceof Key) {
                if (p.getPosicao().igual(this.getPosicao())) {
                    ((Key) p).checarColisao();
                }
            }
            if (p instanceof Tranca) {
                if (p.getPosicao().igual(this.getPosicao())) {
                    ((Tranca) p).checarColisao();
                }
            }
      }
        return true;
    }
    public void precisaMorrer() {
        if (morto) return; // Já morreu, não fazer nada

        int linha = this.getPosicao().getLinha();
        int coluna = this.getPosicao().getColuna();
        Tile tileAtual = faseAtual.getTile(linha, coluna);

        if (tileAtual != null && tileAtual.isMortal()) {
            System.out.println("O herói caiu na água gelada!");
            falhouSom.tocarUmaVez();
            morto = true;
            faseAtual.reiniciarFase();
        }
    }

    private void preencherComAgua(int y, int x) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == y && p.getPosicao().getColuna() == x) {
                return; // Não substituir chão do botão
            }
        }
        faseAtual.setTile(y, x, new Tile("water.png", true, true, false));
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();  // desserializa campos normais
        // recria o som
        falhouSom = new Som("/sounds/fail.wav");
         passouSom = new Som("/sounds/win.wav");
    }
    
    public boolean moveUp() {
        this.imagem = upImage;
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveUp() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior);
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        this.imagem = downImage;
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveDown() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior);
            return true;
        }
        return false;
    }

    public boolean moveLeft() {
        this.imagem = leftImage;
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveLeft() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior);
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        this.imagem = rightImage;
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveRight() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior);
            return true;
        }
        return false;
    }
    
   public void resetarMorte(){
      this.morto=false;
   }
}
