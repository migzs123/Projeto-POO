package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import javax.swing.ImageIcon;

public class Hero extends Personagem implements Serializable{
    private ImageIcon upImage, downImage, leftImage, rightImage;
    private String baseName;
    
    public Hero(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.baseName = sNomeImagePNG.replace(".png", ""); // "Robbo"
        this.loadDirectionalSprites();
        this.iImage = downImage; // começa olhando para baixo
    }

    private void loadDirectionalSprites() {
        upImage = carregarImagem(baseName + "_up.png");
        downImage = carregarImagem(baseName + "_down.png");
        leftImage = carregarImagem(baseName + "_left.png");
        rightImage = carregarImagem(baseName + "_right.png");
    }
    
      private ImageIcon carregarImagem(String nomeImagem) {
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
            return this.iImage; // fallback: imagem padrão carregada pelo Personagem
        }
    }
    public void voltaAUltimaPosicao() {
        this.pPosicao.volta();
    }
      
    public boolean setPosicao(int linha, int coluna) {
        if (this.pPosicao.setPosicao(linha, coluna)) {
            if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
                this.voltaAUltimaPosicao();
            }
            return true;
        }
        return false;
    }

    private boolean validaPosicao() {
        if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
            this.voltaAUltimaPosicao();
            return false;
        }
        return true;
    }

    public boolean moveUp() {
        this.iImage = upImage;
        if (super.moveUp()) return validaPosicao();
        return false;
    }

    public boolean moveDown() {
        this.iImage = downImage;
        if (super.moveDown()) return validaPosicao();
        return false;
    }

    public boolean moveRight() {
        this.iImage = rightImage;
        if (super.moveRight()) return validaPosicao();
        return false;
    }

    public boolean moveLeft() {
        this.iImage = leftImage;
        if (super.moveLeft()) return validaPosicao();
        return false;
    }
}
