package Modelo;

import Auxiliar.Desenho;
import Auxiliar.Som;
import auxiliar.Posicao;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Hero extends Personagem {

    private transient Som falhouSom;

    private boolean morto = false;
    private ImageIcon upImage, downImage, leftImage, rightImage;
    private boolean hasKey = false;
    private PowerUp powerUp;

    public Hero(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = downImage;
        falhouSom = new Som("/sounds/fail.wav");  
        powerUp = null;
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
        System.out.println("hasKey status:" + this.hasKey);
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
        
         if (tileAtual != null && tileAtual.isMortal()) {
            if (powerUp != null && powerUp.estaAtivo()) {
                faseAtual.setTile(this.getPosicao().getLinha(), this.getPosicao().getColuna(), new Tile("ground.png", true, false, false));
                powerUp.usarChance();
                return true;
            } else {
                this.precisaMorrer();  
                return false;
            }
        }
         
        if (tileAtual != null && tileAtual.isFim()) {
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
                    if (powerUp == null || !powerUp.estaAtivo()) {
                        powerUp = ((PowerUp) p);
                        powerUp.checarColisao();
                    }
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


    public boolean comparaPosicao(int linha, int coluna){
        if(this.getPosicao().getColuna() == coluna && this.getPosicao().getLinha() == linha){
            return true;
        }
        return false;
    }
    

    public void precisaMorrer() {
        if (morto) {
            return;
        }
        int linha = this.getPosicao().getLinha();
        int coluna = this.getPosicao().getColuna();
        Tile tileAtual = faseAtual.getTile(linha, coluna);
        if (tileAtual != null && tileAtual.isMortal()) {
            System.out.println("O herói caiu na água gelada!");
            if (falhouSom != null) {
                falhouSom.tocarUmaVez();
            }
            morto = true;
            faseAtual.reiniciarFase();
        }
    }

    private void preencherComAgua(int y, int x) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == y && p.getPosicao().getColuna() == x) {
                return;
            }
        }
        if(faseAtual.isReiniciando()){
            return;
        }
        faseAtual.setTile(y, x, new Tile("water.png", true, true, false));
    }
    
     private void preencherComGelo(int y, int x) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == y && p.getPosicao().getColuna() == x) {
                return;
            }
        }
        faseAtual.setTile(y, x, new Tile("ground.png", true, false, false));
    }
    
   public PowerUp getPowerUp(){
       return powerUp;
   }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        falhouSom = new Som("/sounds/fail.wav");
    }

    public boolean moveUp() {
        this.imagem = upImage;
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveUp() && validaPosicao()) { // validaPosicao pode mudar o estado de powerupAtivo
            
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
    
    public void removerPowerUp() {
        this.powerUp = null;
    }
}