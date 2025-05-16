package Modelo;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.net.URL;

public class Tile {
    private ImageIcon imagem;
    private boolean transponivel;
    private boolean mortal;

    public Tile(String nomeImagem, boolean transponivel) throws IOException {
        // Carrega a imagem usando getResource (procura no classpath)
        URL imgURL = getClass().getResource(Auxiliar.Consts.PATH + nomeImagem);
        if (imgURL == null) {
            throw new IOException("Imagem não encontrada: " + nomeImagem);
        }
        this.imagem = new ImageIcon(imgURL);
        this.transponivel = transponivel;
    }
    
     public Tile(String nomeImagem, boolean transponivel, boolean mortal) throws IOException {
        // Carrega a imagem usando getResource (procura no classpath)
        URL imgURL = getClass().getResource(Auxiliar.Consts.PATH + nomeImagem);
        if (imgURL == null) {
            throw new IOException("Imagem não encontrada: " + nomeImagem);
        }
        this.imagem = new ImageIcon(imgURL);
        this.transponivel = transponivel;
        this.mortal = mortal;
    }

    public boolean isTransponivel() {
        return transponivel;
    }
    
     public boolean isMortal() {
        return mortal;
    }

    public void desenhar(int linha, int coluna) {
        Auxiliar.Desenho.desenhar(imagem, coluna, linha);
    }
}

