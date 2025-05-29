
package Modelo;

import Auxiliar.Consts;
import Controler.Tela;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Fase implements Serializable {
     private static final long serialVersionUID = 1L;
     
       private transient Tela tela;
       private int levelAtual;
       private Hero hero;
       private ArrayList<Personagem> entidades;
       private Tile[][] mapaBase = new Tile[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
       private int tentativas;
       private int pontos;
       private int comidas;
       private int maxComidas;
       
       public Fase(Tela tela, int levelAtual){
           this.tela = tela;
           this.levelAtual = levelAtual;
           this.carregarFase(levelAtual);
           pontos = 0;
       }
       
       
       public void carregarFase(int n) {
        if(n == levelAtual){
            maxComidas = 0;
            tentativas++;
        }
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
            maxComidas = 0;
            tentativas=0;
            comidas = 0;
            levelAtual++;
           
            this.carregarFase(levelAtual);
        } else {
            // Jogo completado
            System.out.println("Parabéns! Você completou todos os níveis!");
        }
    }
       
       public void reiniciarJogo(){
           maxComidas = 0;
           tentativas =0;
           comidas =0;
           pontos=0;
           levelAtual=1;
           carregarFase(1);
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
            this.setTile(y, x, new Tile("ground.png", true, false, false));
        } else if (pixel == 0xFF000000) { // preto - parede
            this.setTile(y, x, new Tile("wall.png", false,false,false));
        }else if (pixel == 0xFF404040) { // cinza - backgorund
            this.setTile(y, x, new Tile("background.png", false, false, false)); 
        } else if (pixel == 0xFF0026FF) { // azul - Fim
//            this.setTile(y, x, new Tile("End.png", true, false , true));
              Tranca tranca = new Tranca("tranca.png", this);
                tranca.setPosicao(y, x);
                this.AdicionaEntidade(tranca);
                this.setTile(y, x, new Tile("ground.png", true, false, false));
        } 
        else if (pixel == 0xFFFF0000) { // vermelho - herói
            hero = new Hero("hero.png",this);
            hero.setPosicao(y, x);
            this.AdicionaEntidade(hero);
            this.setTile(y, x, new Tile("ground.png", true, false, false));
        }
      else if (pixel == 0xFF00FF00) { // VERDE - COMIDA
                Food comida = new Food("peixe.png", this);
                comida.setPosicao(y, x);
                this.AdicionaEntidade(comida);
                this.setTile(y, x, new Tile("ground.png", true, false, false));
            } 
       else if (pixel == 0xFFFFFF00) { // AMARELO - CHAVE
                Key chave = new Key("chave.png", this);
                chave.setPosicao(y, x);
                this.AdicionaEntidade(chave);
                this.setTile(y, x, new Tile("ground.png", true, false, false));
            }
    }

       
       public void recarregarRecursos() {
            for (Personagem p : entidades) {
                p.carregarImagem(); // Hero e outros devem implementar isso
            }

            // Recarrega imagens dos tiles
            for (int i = 0; i < mapaBase.length; i++) {
                for (int j = 0; j < mapaBase[i].length; j++) {
                    if (mapaBase[i][j] != null) {
                        mapaBase[i][j].recarregarImagem();
                    }
                }
            }
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
       
    public int getTentativas(){
        return tentativas;
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
   
    public void setTela(Tela tela) {
        this.tela = tela;
    }
    
    public Hero getHero(){
        return hero;
    }
    
    public int getPontos(){
        return this.pontos;
    }
    
    public void adicionarPontos(int qtd){
        this.pontos += qtd;
    }
    
    public int getComidas(){
        return this.comidas;
    }
    
    public int getMaxComidas(){
        return this.maxComidas;
    }
    
    public void addMaxComidas(){
        this.maxComidas++;
    }
    
    public void adicionarComida(){
        if(this.comidas<this.maxComidas){
            this.comidas++;
        }
    }
   
}
