package Modelo;

public class PowerUp extends Personagem {

    private boolean ativo;
    private int chancesRestantes;
    private static final int MAX_CHANCES = 4; 

    public PowerUp(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.ativo = false;
    }

    @Override
    protected void carregarSprites() {}
    
    public void checarColisao() {
        if(ativo) return;
        Hero hero = this.faseAtual.getHero();
        if (this.getPosicao().igual(hero.getPosicao())) {
            this.ativar();
            this.faseAtual.RemoveEntidade(this);
        }
    }
    
    public void usarChance() { 
        chancesRestantes--;
        if (chancesRestantes == 0) {
            this.desativar();
            faseAtual.getHero().removerPowerUp();
            return;
        }
    }

    private void ativar() {
        this.ativo = true;
        this.chancesRestantes = MAX_CHANCES;
    }

    private void desativar() {
        this.ativo = false;
    }

    public boolean estaAtivo() {
        return this.ativo;
    }

    public int getChancesRestantes() {
        return this.chancesRestantes;
    }
    
    public int getChances(){
        return chancesRestantes;
    }
}