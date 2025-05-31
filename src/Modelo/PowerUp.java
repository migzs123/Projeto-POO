package Modelo;

import Auxiliar.Som;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class PowerUp extends Personagem implements Serializable {

    private boolean ativo;
    private int chancesRestantes;
    private static final int MAX_CHANCES = 4; 
    private transient Som coletaSom;

    public PowerUp(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);

        try {
            coletaSom = new Som(""); 
        } catch (Exception e) {
            System.err.println("Erro ao carregar som do power-up: " + e.getMessage());
            coletaSom = null;
        }

        this.ativo = false;
        this.chancesRestantes = 0;
    }

    @Override
    protected void carregarSprites() {
        
    }

    public void checarColisao() {
        if (this.faseAtual == null || this.faseAtual.getHero() == null) {
            return;
        }

        Hero hero = this.faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())) {
            System.out.println("PowerUp.checarColisao: Colisão detectada com Herói!");
            if (coletaSom != null) {
                coletaSom.tocarUmaVez();
            }
            hero.coletouPowerup(this);
            this.faseAtual.RemoveEntidade(this);
            System.out.println("PowerUp " + (this.getNomeImagem() != null ? this.getNomeImagem() : "desconhecido") + " coletado e removido da fase.");
        }
    }

    public void ativar() {
        this.ativo = true;
        this.chancesRestantes = MAX_CHANCES;
        String nomeDisplay = (this.getNomeImagem() != null && !this.getNomeImagem().isEmpty()) ? this.getNomeImagem() : "Power-up";
        System.out.println("PowerUp.ativar: Efeito do " + nomeDisplay + " ATIVADO! Estado: " + this.ativo + ", Chances: " + this.chancesRestantes);
    }

    public void desativar() {
        this.ativo = false;
        this.chancesRestantes = 0;
        String nomeDisplay = (this.getNomeImagem() != null && !this.getNomeImagem().isEmpty()) ? this.getNomeImagem() : "Power-up";
        System.out.println("PowerUp.desativar: Efeito do " + nomeDisplay + " DESATIVADO. Estado: " + this.ativo);
    }

    public boolean estaAtivo() {
        return this.ativo;
    }

    public int getChancesRestantes() {
        return this.chancesRestantes;
    }

    public void usarChance() {
        // CORRIGIDO: Usando getNomeImagem()
        String nomeDisplay = (this.getNomeImagem() != null && !this.getNomeImagem().isEmpty()) ? this.getNomeImagem() : "Power-up";

        if (this.ativo && this.chancesRestantes > 0) {
            this.chancesRestantes--;
            System.out.println("PowerUp.usarChance: Chance do " + nomeDisplay + " usada! Restam: " + this.chancesRestantes + ". Ativo: " + this.ativo);
            if (this.chancesRestantes == 0) {
                System.out.println("PowerUp.usarChance: Zerou as chances para " + nomeDisplay + ", desativando...");
                desativar();
            }
        } else if (this.ativo) { // Estava ativo, mas chancesRestantes já era 0
            System.out.println("PowerUp.usarChance: Tentativa de usar chance do " + nomeDisplay +
                               ", mas não há chances restantes (chances=" + this.chancesRestantes + ", ativo=" + this.ativo + "). Desativando.");
            desativar(); 
        } else { 
             System.out.println("PowerUp.usarChance: Tentativa de usar chance do " + nomeDisplay +
                               ", mas o power-up não está ativo (chances=" + this.chancesRestantes + ", ativo=" + this.ativo + ").");
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        try {
            coletaSom = new Som("");
        } catch (Exception e) {
            System.err.println("Erro ao recarregar som do power-up durante desserialização: " + e.getMessage());
            coletaSom = null;
        }
    }
}