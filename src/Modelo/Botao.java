
package Modelo;

import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Botao extends Personagem {
    private ImageIcon botaoN, botaoP;
    private boolean apertado = false;
    private ArrayList<Bomba> bombas;

    public Botao(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        if(apertado){
            this.imagem = botaoP;
        }else{
            this.imagem = botaoN;
        } 
        bombas = new ArrayList<>();
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
        atualizaImagem();
        explodirBombas();
    }
    
    public void adicionarBomba(Bomba b){
        bombas.add(b);
    }
    
    public void explodirBombas(){
        for(Bomba b : bombas){
            b.explodir();
        }
    }
    
    public void atualizaImagem() {
    if (apertado) {
        this.imagem = botaoP;
    } else {
        this.imagem = botaoN;
    }
}
}
