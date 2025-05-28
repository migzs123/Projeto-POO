package Modelo;

import auxiliar.Posicao;

public abstract class Personagem extends Entidade {
    protected Posicao pPosicao;
    protected Fase faseAtual;

    public Personagem(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, true, false); 
        this.faseAtual = faseAtual;
        this.pPosicao = new Posicao(1, 1);
        carregarImagem(); 
    }

    // Método que as subclasses devem implementar
    protected abstract void carregarSprites();

    public Posicao getPosicao() {
        return pPosicao;
    }

    public void autoDesenho() {
        desenhar(pPosicao.getLinha(), pPosicao.getColuna()); // usa o desenhar() da superclasse
    }

    public boolean setPosicao(int linha, int coluna) {
        return pPosicao.setPosicao(linha, coluna);
    }

    public boolean moveUp() {
        return pPosicao.moveUp();
    }

    public boolean moveDown() {
        return pPosicao.moveDown();
    }

    public boolean moveRight() {
        return pPosicao.moveRight();
    }

    public boolean moveLeft() {
        return pPosicao.moveLeft();
    }
}