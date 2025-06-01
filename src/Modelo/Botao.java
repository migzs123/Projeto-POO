package Modelo;

import Auxiliar.Som;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;

public class Botao extends Personagem {
    private ImageIcon botaoN, botaoP;
    private boolean apertado = false;
    private ArrayList<Bomba> bombas;
    private transient Som somExplosao;

    public Botao(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = apertado ? botaoP : botaoN;
        bombas = new ArrayList<>();
        somExplosao = new Som("/sounds/explosion.wav"); // caminho correto
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

    private void apertar() {
        apertado = true;
        this.imagem = botaoP;
         // Cria um timer para executar a explosão após 500ms
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                atualizaImagem();
                explodirBombas();
                timer.cancel(); // encerra o timer após a execução
            }
        }, 100); // 500 milissegundos de atraso
    }

    public void adicionarBomba(Bomba b) {
        bombas.add(b);
    }

    public void explodirBombas() {
        for (Bomba b : bombas) {
            b.explodir();
        }
        somExplosao.tocarUmaVez();
    }

    public void atualizaImagem() {
        this.imagem = apertado ? botaoP : botaoN;
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();  // desserializa campos normais
        // recria o som
        somExplosao = new Som("/sounds/explosion.wav");
    }
    
    public ArrayList<Bomba> getBombas(){
        return this.bombas;
    }
    
    public void setBombas(ArrayList<Bomba> bombas){
        this.bombas = bombas;
    }
}
