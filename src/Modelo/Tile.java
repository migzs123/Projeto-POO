package Modelo;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class Tile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private transient ImageIcon imagem;
    private String nomeImagem;
    private boolean transponivel;
    private boolean mortal;
    private boolean fim;

    public Tile(String nomeImagem, boolean transponivel) throws IOException {
        // Carrega a imagem usando getResource (procura no classpath)
        this.nomeImagem= nomeImagem;
        URL imgURL = getClass().getResource(Auxiliar.Consts.PATH + nomeImagem);
        if (imgURL == null) {
            throw new IOException("Imagem não encontrada: " + nomeImagem);
        }
        this.imagem = new ImageIcon(imgURL);
        this.transponivel = transponivel;
    }
    
     public Tile(String nomeImagem, boolean transponivel, boolean mortal) throws IOException {
        // Carrega a imagem usando getResource (procura no classpath)
        this.nomeImagem= nomeImagem;
        URL imgURL = getClass().getResource(Auxiliar.Consts.PATH + nomeImagem);
        if (imgURL == null) {
            throw new IOException("Imagem não encontrada: " + nomeImagem);
        }
        this.imagem = new ImageIcon(imgURL);
        this.transponivel = transponivel;
        this.mortal = mortal;
    }
     
    public Tile(String nomeImagem, boolean transponivel, int fim) throws IOException {
        // Carrega a imagem usando getResource (procura no classpath)
        this.nomeImagem= nomeImagem;
        URL imgURL = getClass().getResource(Auxiliar.Consts.PATH + nomeImagem);
        if (imgURL == null) {
            throw new IOException("Imagem não encontrada: " + nomeImagem);
        }
        this.imagem = new ImageIcon(imgURL);
        this.transponivel = transponivel;
        if (fim == 0) this.fim = false;
        if (fim == 1) this.fim = true;
    }
    
    public void carregarImagem() {
        this.imagem = new ImageIcon(getClass().getResource("/imgs/" + this.nomeImagem));
    }

    public boolean isTransponivel() {
        return transponivel;
    }
    
    public boolean isFim() {
        return fim;
    }
    
     public boolean isMortal() {
        return mortal;
    }

    public void desenhar(int linha, int coluna) {
        Auxiliar.Desenho.desenhar(imagem, coluna, linha);
    }
}

