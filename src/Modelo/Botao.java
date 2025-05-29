
package Modelo;

import javax.swing.ImageIcon;

public class Botao extends Personagem {
    private ImageIcon botaoN, botaoP;
    private boolean apertado = false;

    public Botao(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = botaoN;
    }
    
    @Override
    public void carregarSprites() {
        botaoN = carregarImagem(nomeImagem);
        botaoP = carregarImagem(nomeImagem.replace(".png", "") + "Apertado.png");
    }
    
    public void checarColisao() {
        Hero heroi = faseAtual.getHero();

        if (this.getPosicao().igual(heroi.getPosicao()) && !apertado) {
            apertar();
        }
    }

    private void apertar(){
        apertado = true;
        this.imagem = botaoP;
    }
    
    
}
