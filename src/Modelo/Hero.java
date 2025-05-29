package Modelo;

import Auxiliar.Desenho;
import javax.swing.ImageIcon;

public class Hero extends Personagem {
    
    private ImageIcon upImage, downImage, leftImage, rightImage;
    private boolean hasKey = false;
    public Hero(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = downImage;
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
        if (tileAtual != null && tileAtual.isMortal()) {
            System.out.println("GAME OVER: O herói caiu na água gelada!");
            Desenho.acessoATelaDoJogo().faseAtual.carregarFase(faseAtual.getFase());
            return false;
        }

        if (tileAtual != null && tileAtual.isFim()) {
            faseAtual.proximaFase();
            return false;
        }
        
        for (Personagem p : faseAtual.getEntidades()) {
        if (p instanceof Food) {
            if (p.getPosicao().igual(this.getPosicao())) {
                ((Food) p).checarColisao();
            }
        }
        if (p instanceof Key) {
            if (p.getPosicao().igual(this.getPosicao())) {
                ((Key) p).checarColisao();
            }
            
        }
      }
        
        return true;
    }

    private void preencherComAgua(int y, int x){
        faseAtual.setTile(y, x, new Tile("water.png", true, true,false));
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
}
