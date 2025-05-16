
package Modelo;

import Auxiliar.Consts;
import Controler.Tela;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Fase {
    
       private Tela tela;
       private int levelAtual;
       private ArrayList<Personagem> entidades;
       private Tile[][] mapaBase = new Tile[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
       
       public Fase(Tela tela, int levelAtual){
           this.tela = tela;
           this.levelAtual = levelAtual;
           this.carregarFase(levelAtual);
       }
       
       
       public void carregarFase(int n) {
        entidades = new ArrayList<>();
        mapaBase = new Tile[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
        
        try {
             this.ConstroiMundo(Consts.PATH + n +  "mapa.png", tela); // Padrão: 1mapa.png, 2mapa.png, etc.
        } catch (IOException ex) {
            Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
       public void proximaFase() {
        if (levelAtual < Consts.TOTAL_LEVEIS) {
            levelAtual++;
            this.carregarFase(levelAtual);
        } else {
            // Jogo completado
            System.out.println("Parabéns! Você completou todos os níveis!");
        }
    }
       
       public void ConstroiMundo(String path, Tela tela) throws IOException {
        this.tela = tela;
        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            int[] pixels = new int[map.getWidth() * map.getHeight()];
            map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    int pixelAtual = pixels[x + (y * map.getWidth())];
                    processaPixel(pixelAtual, y, x);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o mapa: " + path);
            throw e;
        }
    }
       
       private void processaPixel(int pixel, int y, int x) throws IOException {
        if (pixel == 0xFFFFFFFF) { // branco - chão
            this.setTile(y, x, new Tile("ground.png", true));
        } else if (pixel == 0xFF000000) { // preto - parede
            this.setTile(y, x, new Tile("wall.png", false));
        }else if (pixel == 0xFF404040) { // cinza - backgorund
            this.setTile(y, x, new Tile("background.png", false)); 
        } 
        else if (pixel == 0xFFFF0000) { // vermelho - herói
            Hero h = new Hero("hero.png");
            h.setPosicao(y, x);
            tela.setHero(h);
            this.AdicionaEntidade(h);
            this.setTile(y, x, new Tile("ground.png", true));
            tela.atualizaCamera();
        }
        // Adicione mais condições para outros elementos do jogo
        
        
    }
       
    public void setTile(int linha, int coluna, Tile tile) {
        if (linha >= 0 && linha < mapaBase.length && 
            coluna >= 0 && coluna < mapaBase[0].length) {
            mapaBase[linha][coluna] = tile;
        }
    }
    
    public Tile getTile(int linha, int coluna) {
        return mapaBase[linha][coluna];
    }
       
       
    public ArrayList<Personagem> getEntidades(){
         return entidades;
    }
       
       
        public void AdicionaEntidade(Personagem p){
           entidades.add(p);
       }
       
       public void RemoveEntidade(Personagem p){
           entidades.remove(p);
       }
       
       public boolean estaVazia(){
           return entidades.isEmpty();
       }
       
       public int getFase(){
           return levelAtual;
       }
       
       public Tile[][] getMapaBase(){
           return mapaBase;
       }
}
