package Modelo;

public class Tile extends Entidade {
    private final boolean fim;

/*------------CONSTRUTORES------------*/
    Tile(String nomeImagem, boolean transponivel, boolean mortal, boolean fim) {
        super(nomeImagem, transponivel, mortal);
        this.fim = fim;
        carregarImagem(); // da classe Entidade
    }

/*------------DESENHO------------*/
    @Override
    public void desenhar(int linha, int coluna) {
        super.desenhar(linha, coluna);
    }

    public void recarregarImagem() {
        carregarImagem(); // da superclasse
    }

/*------------VERIFICAÇÕES------------*/
    public boolean isFim() {
        return fim;
    }
}