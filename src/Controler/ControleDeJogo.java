package Controler;

import Modelo.Personagem;
import Modelo.Hero;
import Modelo.Inimigo;
import auxiliar.Posicao;
import java.util.ArrayList;
import java.util.Iterator;

public class ControleDeJogo {
    
    public void desenhaTudo(ArrayList<Personagem> entidades) {
        for (Personagem p : entidades) {
            p.autoDesenho();
        }
    }

    public void processaTudo(ArrayList<Personagem> entidades, ArrayList<Personagem> paraMorrer) {  

        for (Personagem p : entidades) {
            if (p instanceof Hero) {
                ((Hero) p).precisaMorrer();
            }
            if(p instanceof Inimigo){
                ((Inimigo) p).deteccao();
                ((Inimigo) p).precisaMorrer();
            }
        }
        
        for (Personagem p : paraMorrer) {
            entidades.remove(p);
        }
        paraMorrer.clear();

    }

    /*Retorna true se a posicao p é válida para o herói com relação a outros personagens*/
    public boolean ehPosicaoValida(ArrayList<Personagem> entidades, Posicao p) {
        for (Personagem personagem : entidades) {
            if (!personagem.isTransponivel()) {
                if (personagem.getPosicao().igual(p)) {
                    return false;
                }
            }
        }
        return true;
    }

}
