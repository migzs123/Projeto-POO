package Modelo;

import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class Tile implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient ImageIcon imagem;
    private final String nomeImagem;
    private final boolean transponivel;
    private final boolean mortal;
    private final boolean fim;

/*------------CONSTRUTORES------------*/
    public Tile(String nomeImagem, boolean transponivel) throws IOException {
        this(nomeImagem, transponivel, false, false);
    }

    public Tile(String nomeImagem, boolean transponivel, boolean mortal) throws IOException {
        this(nomeImagem, transponivel, mortal, false);
    }

    public Tile(String nomeImagem, boolean transponivel, int fim) throws IOException {
        this(nomeImagem, transponivel, false, fim == 1);
    }

    private Tile(String nomeImagem, boolean transponivel, boolean mortal, boolean fim) throws IOException {
        this.nomeImagem = nomeImagem;
        this.transponivel = transponivel;
        this.mortal = mortal;
        this.fim = fim;
        carregarImagem();
    }

/*------------CARREGAMENTO DE IMAGEM------------*/
    private void carregarImagem() throws IOException {
        URL imgURL = getClass().getResource(Auxiliar.Consts.PATH + nomeImagem);
        if (imgURL == null) {
            throw new IOException("Imagem não encontrada: " + nomeImagem);
        }
        this.imagem = new ImageIcon(imgURL);
    }

    public void recarregarImagem() {
        try {
            carregarImagem();
        } catch (IOException e) {
            System.err.println("Erro ao recarregar imagem do tile: " + nomeImagem);
            e.printStackTrace();
        }
    }
    
     public void desenhar(int linha, int coluna) {
        if (imagem != null) {
            Auxiliar.Desenho.desenhar(imagem, coluna, linha);
        } else {
            System.err.println("Imagem nula para tile: " + nomeImagem);
        }
    }
     
     
/*------------VERIFICAÇÕES------------*/
    public boolean isTransponivel() {
        return transponivel;
    }

    public boolean isMortal() {
        return mortal;
    }

    public boolean isFim() {
        return fim;
    }

   
/*------------GETTERS/SETTERS------------*/
    public String getNomeImagem() {
        return nomeImagem;
    }
}
