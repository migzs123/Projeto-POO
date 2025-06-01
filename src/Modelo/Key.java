/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author prado
 */
public class Key extends Personagem {
    public Key(String nomeImagem, Fase faseAtual){
        super(nomeImagem, faseAtual);
    }
    
    @Override
    protected void carregarSprites() {
        // Pode deixar vazio, pois Food não precisa de sprites diferentes
    }
    
    public void checarColisao(){
        Hero hero = faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())){
            hero.getKey();
            faseAtual.RemoveEntidade(this);
            System.out.println("Key has been coleted!");
        }
    }
}