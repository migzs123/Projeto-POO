package Controler;

import Modelo.Personagem;
import Modelo.Hero;
import auxiliar.Posicao;
import java.util.ArrayList;
import java.util.Iterator;

public class ControleDeJogo {
    
    public void desenhaTudo(ArrayList<Personagem> entidades) {
        for (Personagem p : entidades) {
            p.autoDesenho();
        }
    }

    public void processaTudo(ArrayList<Personagem> entidades) {
        Hero hero = null;

        // Encontra o herói
        for (Personagem p : entidades) {
            if (p instanceof Hero) {
                hero = (Hero) p;
                break;
            }
        }

        if (hero == null) return; // Nenhum herói presente

        Posicao posHero = hero.getPosicao();

        Iterator<Personagem> it = entidades.iterator();
        while (it.hasNext()) {
            Personagem p = it.next();

            // Ignora o próprio herói
            if (p == hero) continue;

            // Verifica colisão
            if (posHero.igual(p.getPosicao())) {
                if (p.isbTransponivel()) {
                    if (p.isbMortal()) {
                        it.remove(); // Remove a entidade mortal
                    }
                }
            }
        }
    }

    /*Retorna true se a posicao p é válida para o herói com relação a outros personagens*/
    public boolean ehPosicaoValida(ArrayList<Personagem> entidades, Posicao p) {
        for (Personagem personagem : entidades) {
            if (!personagem.isbTransponivel()) {
                if (personagem.getPosicao().igual(p)) {
                    return false;
                }
            }
        }
        return true;
    }

}
