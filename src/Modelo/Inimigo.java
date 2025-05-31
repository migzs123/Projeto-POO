package Modelo;

import Auxiliar.Desenho;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Inimigo extends Personagem {
    private int distanciaDetecao = 4;
    private boolean detectou = false;
    private transient Timer timer;
    private boolean morto = false;

    public Inimigo(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        iniciarMovimentacao(); // Inicia o Timer no construtor
    }

    @Override
    protected void carregarSprites() {}

    public void checarColisao() {
       Hero hero = faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())){
           faseAtual.setTile(this.getPosicao().getLinha(), this.getPosicao().getColuna(), new Tile("water.png", true, true,false));
           
        } 
    }
    
    public void deteccao() {
        int linhaHeroi = faseAtual.getHero().getPosicao().getLinha();
        int colunaHeroi = faseAtual.getHero().getPosicao().getColuna();
        int linhaInimigo = this.getPosicao().getLinha();
        int colunaInimigo = this.getPosicao().getColuna();

        detectou = Math.abs(linhaHeroi - linhaInimigo) <= distanciaDetecao &&
                   Math.abs(colunaHeroi - colunaInimigo) <= distanciaDetecao;
    }

    public void iniciarMovimentacao() {
        timer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deteccao(); // atualiza detecção
                if (detectou) {
                    seMovimenta();
                }
            }
        });
        timer.start();
    }

    public void seMovimenta() {
        if(morto) {return;}
        int linhaHeroi = faseAtual.getHero().getPosicao().getLinha();
        int colunaHeroi = faseAtual.getHero().getPosicao().getColuna();
        int linhaInimigo = this.getPosicao().getLinha();
        int colunaInimigo = this.getPosicao().getColuna();

        if (linhaHeroi < linhaInimigo) {
            moveUp();
        } else if (linhaHeroi > linhaInimigo) {
            moveDown();
        } else if (colunaHeroi < colunaInimigo) {
            moveLeft();
        } else if (colunaHeroi > colunaInimigo) {
            moveRight();
        }
    }

    public void voltaAUltimaPosicao() {
        this.pPosicao.volta();
    }

    private boolean validaPosicao() {
        if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
            this.voltaAUltimaPosicao();
            return false;
        }

        Tile tileAtual = faseAtual.getTile(this.getPosicao().getLinha(), this.getPosicao().getColuna());

        for (Personagem p : new ArrayList<>(faseAtual.getPersonagens())) {
            if (p instanceof Botao) {
                if (p.getPosicao().igual(this.getPosicao())) {
                    ((Botao) p).checarColisao();
                }
            }
            if(p instanceof Hero){
                if (p.getPosicao().igual(this.getPosicao())) {
                    this.checarColisao();
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
            System.out.println("O inimigo caiu na água gelada!");
            morto = true;
            faseAtual.marcarParaRemocao(this);
        }
    }
    
    private void preencherComAgua(int y, int x) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == y && p.getPosicao().getColuna() == x) {
                return; // Não sobrepor botão
            }
        }
        faseAtual.setTile(y, x, new Tile("water.png", true, true, false));
    }

    public boolean moveUp() {
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();
        if (super.moveUp()) {
            if (validaPosicao()) {
                preencherComAgua(linhaAnterior, colunaAnterior);
                return true;
            } else {
                this.pPosicao.setPosicao(linhaAnterior, colunaAnterior);
            }
        }
        return false;
    }

    public boolean moveDown() {
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();
        if (super.moveDown()) {
            if (validaPosicao()) {
                preencherComAgua(linhaAnterior, colunaAnterior);
                return true;
            } else {
                this.pPosicao.setPosicao(linhaAnterior, colunaAnterior);
            }
        }
        return false;
    }

    public boolean moveLeft() {
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();
        if (super.moveLeft()) {
            if (validaPosicao()) {
                preencherComAgua(linhaAnterior, colunaAnterior);
                return true;
            } else {
                this.pPosicao.setPosicao(linhaAnterior, colunaAnterior);
            }
        }
        return false;
    }

    public boolean moveRight() {
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();
        if (super.moveRight()) {
            if (validaPosicao()) {
                preencherComAgua(linhaAnterior, colunaAnterior);
                return true;
            } else {
                this.pPosicao.setPosicao(linhaAnterior, colunaAnterior);
            }
        }
        return false;
    }

    public boolean getDetectou() {
        return this.detectou;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    iniciarMovimentacao(); // reinicia o timer após desserializar
}
}
