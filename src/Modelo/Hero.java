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
    private boolean morto = false;
    private ImageIcon upImage, downImage, leftImage, rightImage;
    private PowerUp powerupAtivo;
    private boolean hasKey = false;

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

    public void getKey() {
        this.hasKey = true;
    }

    public boolean getKeyStatus() {
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

        if (tileAtual != null && "water.png".equals(tileAtual.getNomeImagem())) {
            if (this.powerupAtivo != null &&
                "gelo.png".equals(this.powerupAtivo.getNomeImagem()) &&
                this.powerupAtivo.estaAtivo()) {
                faseAtual.transformarAguaEmGelo(this.getPosicao().getLinha(), this.getPosicao().getColuna());
                this.powerupAtivo.usarChance();
                if (!this.powerupAtivo.estaAtivo()) {
                    this.powerupAtivo = null;
                }
                return true;
            } else {
                if (falhouSom != null) falhouSom.tocarUmaVez();
                faseAtual.carregarFase(faseAtual.getFase());
                return false;
            }
        }

        if (tileAtual != null && tileAtual.isFim()) {
            if (passouSom != null) passouSom.tocarUmaVez();
            faseAtual.proximaFase();
            return false;
        }

        if (tileAtual != null && !tileAtual.isTransponivel()) {
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
                } else if (p instanceof Inimigo) {
                    ((Inimigo) p).checarColisao();
                } else if (p instanceof Key) {
                    ((Key) p).checarColisao();
                } else if (p instanceof Tranca) {
                    ((Tranca) p).checarColisao();
                }
            }
        }
        return true;
    }

    public void precisaMorrer() {
        if (morto) return;

        int linha = this.getPosicao().getLinha();
        int coluna = this.getPosicao().getColuna();
        Tile tileAtual = faseAtual.getTile(linha, coluna);

        if (tileAtual != null && tileAtual.isMortal()) {
            if (falhouSom != null) falhouSom.tocarUmaVez();
            morto = true;
            faseAtual.reiniciarFase();
        }
    }

    private void preencherComAgua(int prevY, int prevX) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == prevY && p.getPosicao().getColuna() == prevX) {
                return;
            }
        }

        Tile tileAnterior = faseAtual.getTile(prevY, prevX);

        if (this.powerupAtivo != null &&
            "gelo.png".equals(this.powerupAtivo.getNomeImagem()) &&
            this.powerupAtivo.estaAtivo()) {

            if (tileAnterior != null && "ground.png".equals(tileAnterior.getNomeImagem())) {
                // No action needed, already ground.
            } else if (tileAnterior != null) {
                faseAtual.setTile(prevY, prevX, new Tile("ground.png", false, true, false));
            }

        } else {
            if (tileAnterior != null && "ground.png".equals(tileAnterior.getNomeImagem())) {
                faseAtual.setTile(prevY, prevX, new Tile("water.png", true, true, false));
            } else if (tileAnterior != null && "water.png".equals(tileAnterior.getNomeImagem())) {
                // No action needed, already water.
            } else if (tileAnterior != null) {
                faseAtual.setTile(prevY, prevX, new Tile("water.png", true, true, false));
            }
        }
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

    public void resetarMorte() {
        this.morto = false;
    }

    public void coletouPowerup(PowerUp powerupColetado) {
        this.powerupAtivo = powerupColetado;
        if (this.powerupAtivo != null) {
            this.powerupAtivo.ativar();
        }
    }
}