/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author prado
 */
public class Tranca extends Personagem {
    
     public Tranca(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.transponivel = false;
    }
     
      @Override
    protected void carregarSprites() {
        // Pode deixar vazio, pois Food não precisa de sprites diferentes
    }
    
    public void checarColisao(){
        Hero hero = faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())){
            if (hero.getKeyStatus()){
                   this.transponivel = true;
            } else {
                //IMPLEMENTAR AVISO NA TELA
                 this.transponivel = false;
                 System.out.println("You do not have a key to pass!");
                
            }
        }
    }
}

