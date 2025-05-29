package Modelo;

import Auxiliar.Som;
import java.io.IOException;
import java.io.Serializable;

public class Food extends Personagem{
    private transient Som pontoSom;
    
    public Food(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        pontoSom = new Auxiliar.Som("/sounds/coin.wav");
    }
    
     @Override
    protected void carregarSprites() {
        // Pode deixar vazio, pois Food não precisa de sprites diferentes
    }
    
    public void checarColisao(){
        Hero hero = faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())){
            pontoSom.tocarUmaVez();
            faseAtual.adicionarPontos(1);
            faseAtual.adicionarComida();
            faseAtual.RemoveEntidade(this);
            System.out.println("Comida coletada! Pontuação atual: " + faseAtual.getPontos());
        }
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();  // desserializa campos normais
        // recria o som
        pontoSom = new Som("/sounds/coin.wav");
    }
}
