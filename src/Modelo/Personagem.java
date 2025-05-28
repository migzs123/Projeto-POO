package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import auxiliar.Posicao;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import javax.swing.ImageIcon;

public abstract class Personagem implements Serializable {

    protected ImageIcon iImage;
    protected String nomeImagem;
    protected Posicao pPosicao;
    protected boolean bTransponivel;
    protected boolean bMortal;

    public boolean isbMortal() {
        return bMortal;
    }

    protected Personagem(String sNomeImagePNG) {
        this.pPosicao = new Posicao(1, 1);
        this.bTransponivel = true;
        this.bMortal = false;
        this.nomeImagem = sNomeImagePNG;
        carregarImagem(); // carrega a imagem padrão
    }

    public void carregarImagem() {
        try {
            URL imgURL = getClass().getResource("/imgs/" + nomeImagem);
            if (imgURL == null) {
                throw new IOException("Imagem não encontrada: /imgs/" + nomeImagem);
            }
            Image img = new ImageIcon(imgURL).getImage();
            BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);
            g.dispose();
            iImage = new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
            BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.setColor(java.awt.Color.RED);
            g.fillRect(0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE);
            g.dispose();
            iImage = new ImageIcon(bi);
        }
    }

    protected abstract void carregarSprites();

    protected ImageIcon carregarImagem(String nomeImagem) {
        try {
            URL imgURL = getClass().getResource("/imgs/" + nomeImagem);
            if (imgURL == null) throw new IOException("Imagem não encontrada: " + nomeImagem);
            Image img = new ImageIcon(imgURL).getImage();
            BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);
            g.dispose();
            return new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println("Erro ao carregar sprite: " + nomeImagem);
            return this.iImage; // fallback
        }
    }

    public Posicao getPosicao() {
        return pPosicao;
    }

    public boolean isbTransponivel() {
        return bTransponivel;
    }

    public void setbTransponivel(boolean bTransponivel) {
        this.bTransponivel = bTransponivel;
    }

    public void autoDesenho(){
        Desenho.desenhar(this.iImage, this.pPosicao.getColuna(), this.pPosicao.getLinha());        
    }

    public boolean setPosicao(int linha, int coluna) {
        return pPosicao.setPosicao(linha, coluna);
    }

    public boolean moveUp() {
        return this.pPosicao.moveUp();
    }

    public boolean moveDown() {
        return this.pPosicao.moveDown();
    }

    public boolean moveRight() {
        return this.pPosicao.moveRight();
    }

    public boolean moveLeft() {
        return this.pPosicao.moveLeft();
    }
}
