package Modelo;

import Auxiliar.Consts;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import javax.swing.ImageIcon;

public abstract class Entidade  implements Serializable{
    private static final long serialVersionUID = 1L; 
    
    protected transient ImageIcon imagem;
    protected String nomeImagem;
    protected boolean transponivel;
    protected boolean mortal;
    
     public Entidade(String nomeImagem, boolean transponivel, boolean mortal) {
        this.nomeImagem = nomeImagem;
        this.transponivel = transponivel;
        this.mortal = mortal;
    }
     
    public void carregarImagem() {
        this.imagem = carregarImagem(this.nomeImagem);
    }

    protected ImageIcon carregarImagem(String nomeImagem) {
        try {
            
            if (!nomeImagem.toLowerCase().endsWith(".png")) {
                System.out.println(nomeImagem);
                nomeImagem += ".png";  // garante extensão
                System.out.println(nomeImagem);
            }
            URL imgURL = getClass().getResource("/imgs/" + nomeImagem);
            if (imgURL == null) throw new IOException("Imagem não encontrada: " + nomeImagem);
            Image img = new ImageIcon(imgURL).getImage();
            BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);
            g.dispose();
            return new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println("Erro ao carregar sprite: " + nomeImagem + ex.getMessage());
            return this.imagem; 
        }
    }

    public void desenhar(int linha, int coluna) {
        if (imagem != null) {
            Auxiliar.Desenho.desenhar(imagem, coluna, linha);
        }
    }

    public boolean isTransponivel() {
        return transponivel;
    }

    public boolean isMortal() {
        return mortal;
    }

    public String getNomeImagem() {
        return nomeImagem;
    }
    private void readObject(java.io.ObjectInputStream in) 
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.carregarImagem(); // Recarrega a imagem após desserialização
    }
}

