package Modelo;

import java.io.Serializable;

public class Food extends Personagem{

    public Food(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
    }
    
     @Override
    protected void carregarSprites() {
        // Pode deixar vazio, pois Food não precisa de sprites diferentes
    }
    
    public void checarColisao(){
        Hero hero = faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())){
            faseAtual.adicionarPontos(1);
            faseAtual.adicionarComida();
            faseAtual.RemoveEntidade(this);
            System.out.println("Comida coletada! Pontuação atual: " + faseAtual.getPontos());
        }
    }
}
