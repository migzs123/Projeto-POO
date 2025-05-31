package Modelo;

import Auxiliar.Desenho;
import Auxiliar.Som;
import java.io.IOException;
import java.io.ObjectInputStream; 
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Hero extends Personagem {

    private transient Som falhouSom;
    private transient Som passouSom;
    private ImageIcon upImage, downImage, leftImage, rightImage;
    private PowerUp powerupAtivo;

    public Hero(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = downImage;
        falhouSom = new Som("/sounds/fail.wav"); 
        passouSom = new Som("/sounds/win.wav"); 
        this.powerupAtivo = null; 
    }

    @Override
    public void carregarSprites() {
        upImage = carregarImagem(nomeImagem.replace(".png", "") + "_up.png");
        downImage = carregarImagem(nomeImagem.replace(".png", "") + "_down.png");
        leftImage = carregarImagem(nomeImagem.replace(".png", "") + "_left.png");
        rightImage = carregarImagem(nomeImagem.replace(".png", "") + "_right.png");
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

        // Debug: mostra qual tile o herói pisou
        System.out.println("Hero pisou em tile: " + (tileAtual != null ? tileAtual.getNomeImagem() : "null"));

        // Verifica se pisou em água (water.png)
        if (tileAtual != null && "water.png".equals(tileAtual.getNomeImagem())) {
            System.out.println("Detectou água! Verificando poder de gelo...");
            
            // Verifica se tem poder de gelo ativo
            if (this.powerupAtivo != null && 
                "gelo.png".equals(this.powerupAtivo.getNomeImagem()) && 
                this.powerupAtivo.estaAtivo()) {
                
                System.out.println("Herói tem poder de gelo ativo! Transformando água em gelo...");
                faseAtual.transformarAguaEmGelo(this.getPosicao().getLinha(), this.getPosicao().getColuna());

                this.powerupAtivo.usarChance(); 
                if (!this.powerupAtivo.estaAtivo()) {
                    System.out.println("Poder de Gelo esgotado.");
                    this.powerupAtivo = null; 
                }
                return true; 
            } else {
                System.out.println("Herói não tem poder de gelo! Morrendo...");
                if(falhouSom != null) falhouSom.tocarUmaVez();
                faseAtual.carregarFase(faseAtual.getFase()); 
                return false;
            }
        }

        if (tileAtual != null && tileAtual.isFim()) {
            if(passouSom != null) passouSom.tocarUmaVez();
            faseAtual.proximaFase();
            return false;
        }

        if (tileAtual != null && !tileAtual.isTransponivel()) {
            System.out.println("Hero.validaPosicao: Tile é uma barreira não transponível: " + tileAtual.getNomeImagem() + ". Revertendo movimento.");
            this.voltaAUltimaPosicao(); 
            return false;
        }

        for (Personagem p : new ArrayList<>(faseAtual.getPersonagens())) {
            if (p != null && p.getPosicao() != null && p.getPosicao().igual(this.getPosicao())) {
                if (p instanceof Food) {
                    ((Food) p).checarColisao();
                } else if (p instanceof Botao) {
                    ((Botao) p).checarColisao();
                } else if (p instanceof PowerUp) {
                    ((PowerUp) p).checarColisao(); 
                }
            }
        }
        return true;
    }

    private void preencherComAgua(int prevY, int prevX) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == prevY && p.getPosicao().getColuna() == prevX) {
                System.out.println("Hero.preencherComAgua: Tile anterior (" + prevY + "," + prevX + ") é chão de botão, mantendo como está.");
                return; 
            }
        }
        
        Tile tileAnterior = faseAtual.getTile(prevY, prevX);
        if (tileAnterior != null && "ground.png".equals(tileAnterior.getNomeImagem())) {
            System.out.println("Hero.preencherComAgua: Tile anterior (" + prevY + "," + prevX + ") é gelo, mantendo como gelo.");
            return; // Não transforma gelo em água
        }
        
        System.out.println("Hero.preencherComAgua: Transformando tile anterior (" + prevY + "," + prevX + ") em água.");
        faseAtual.setTile(prevY, prevX, new Tile("water.png", true, true, false));
 
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
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

    public void coletouPowerup(PowerUp powerupColetado) {
    this.powerupAtivo = powerupColetado;
    System.out.println("Hero.coletouPowerup: PowerUp recebido: " + (powerupColetado != null ? powerupColetado.getNomeImagem() : "null"));
    if (this.powerupAtivo != null) {
        this.powerupAtivo.ativar(); 
        System.out.println("Hero.coletouPowerup: PowerUp ativado via hero.ativar(). Estado: " + this.powerupAtivo.estaAtivo() + ", Chances: " + this.powerupAtivo.getChancesRestantes());
        if ("gelo.png".equals(this.powerupAtivo.getNomeImagem())) {
            System.out.println("Hero.coletouPowerup: É o PowerUp de GELO!");
        }
    } else {
        System.err.println("Hero.coletouPowerup: Tentativa de coletar um power-up nulo.");
    }
}
}