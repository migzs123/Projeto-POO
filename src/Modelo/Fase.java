
package Modelo;

import Auxiliar.Consts;
import Auxiliar.Som;
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
       private Botao botao;
       private ArrayList<Personagem> personagens;
       private ArrayList<Personagem> personagensParaRemover;
       private Tile[][] mapaBase = new Tile[Consts.MUNDO_ALTURA][Consts.MUNDO_LARGURA];
       private int tentativas;
       private int pontos;
       private int pontosFase;
       private int comidas;
       private int maxComidas;
       private transient Som passouSom;
       
       public Fase(Tela tela, int levelAtual){
            this.tela = tela;
            this.levelAtual = levelAtual;
            this.personagensParaRemover = new ArrayList<>(); 
            this.personagens = new ArrayList<>();            
            pontos = 0;
            pontosFase =0;
            passouSom = new Som("/sounds/win.wav"); 
            carregarFase(levelAtual);
        }

       
       
       public void carregarFase(int n) {
        personagens = new ArrayList<>();
        personagensParaRemover = new ArrayList<>();
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
            passouSom.tocarUmaVez();
            this.carregarFase(levelAtual);
        } else {
           tela.irParaFim();
           
        }
    }
       
       public void reiniciarJogo(){
           maxComidas = 0;
           tentativas =0;
           comidas =0;
           pontos=0;
           pontosFase=0;
           levelAtual=1;
           carregarFase(1);
           tela.deletarSave();
            if (tela != null) {
                tela.atualizaCamera();
            }
       }
       
       public void reiniciarFase(){
           maxComidas = 0;
           tentativas++;
           comidas =0;
           this.pontos -= pontosFase; 
           carregarFase(levelAtual);
           if (tela != null) {
            tela.atualizaCamera();
            }   
       }
       
       public void ConstroiMundo(String path, Tela tela) throws IOException {
            this.tela = tela;
            Hero tempHero = null;

            try {
                BufferedImage map = ImageIO.read(getClass().getResource(path));
                int[] pixels = new int[map.getWidth() * map.getHeight()];
                map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
                for (int y = 0; y < map.getHeight(); y++) {
                    for (int x = 0; x < map.getWidth(); x++) {
                        int pixelAtual = pixels[x + (y * map.getWidth())];

                        if (pixelAtual == 0xFFFF00DC) { // Botão rosa claro
                            botao = new Botao("botao.png", this);
                            botao.setPosicao(y, x);
                            this.AdicionaEntidade(botao);
                            this.setTile(y, x, new Tile("wall.png", true, false, false));
                        }
                    }
                }

                // Depois: processar todo o resto, herói, bombas, comida, tiles etc
                for (int y = 0; y < map.getHeight(); y++) {
                    for (int x = 0; x < map.getWidth(); x++) {
                        int pixelAtual = pixels[x + (y * map.getWidth())];

                        if (pixelAtual == 0xFFFF0000) { // herói
                            tempHero = new Hero("hero.png", this);
                            tempHero.setPosicao(y, x);
                            this.setTile(y, x, new Tile("ground.png", true, false, false));
                        } else if (pixelAtual != 0xFFFF00DC) { // já processou botão no primeiro loop
                            processaPixel(pixelAtual, y, x);
                        }
                    }
                }

                if (tempHero != null) {
                    tempHero.resetarMorte();
                    this.AdicionaEntidade(tempHero);
                    this.hero = tempHero;
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
                this.setTile(y, x, new Tile("End.png", true, false , true)); 
            } 
            else if (pixel == 0xFF57007F){ // Rosa Escuro - Bombas
                Bomba b = new Bomba("dinamite.png" ,this);
                b.setPosicao(y, x);
                this.AdicionaEntidade(b);
                botao.adicionarBomba(b);
                this.setTile(y, x, new Tile("ground.png", true, false, false));
            }
           else if (pixel == 0xFFFFF200) { // Verde - PowerUp
                PowerUp powerUpItem = new PowerUp("gelo.png", this); 
                powerUpItem.setPosicao(y, x);

                this.AdicionaEntidade(powerUpItem);
                this.setTile(y, x, new Tile("ground.png", true, false, false)); 
            } 
            else if (pixel == 0xFF00FF00) { // Verde - Comida
                  Food comida = new Food("peixe.png", this);
                  comida.setPosicao(y, x);
                  this.addMaxComidas();
                  this.AdicionaEntidade(comida);
                  this.setTile(y, x, new Tile("ground.png", true, false, false));
              } 
            else if (pixel == 0xFF4800FF) { // Roxo - Inimigo
                  Inimigo inimigo = new Inimigo("inimigo.png", this);
                  inimigo.setPosicao(y, x);
                  this.AdicionaEntidade(inimigo);
                  this.setTile(y, x, new Tile("ground.png", true, false, false));
              }  
            else if (pixel == 0xFFFFFF00) { // AMARELO CLARO - CHAVE
                     Key chave = new Key("chave.png", this);
                     chave.setPosicao(y, x);
                     this.AdicionaEntidade(chave);
                     this.setTile(y, x, new Tile("ground.png", true, false, false));
                 }
            else if (pixel == 0xFF7F6A00){ //AMARELO ESCURO - TRANCA
                 Tranca tranca = new Tranca("tranca.png", this);
                 tranca.setPosicao(y, x);
                 this.AdicionaEntidade(tranca);
                 this.setTile(y, x, new Tile("ground.png", true, false, false));
            }
    }
  
       public void recarregarRecursos() {
            for (Personagem p : personagens) {
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
    
    public Personagem getPersonagem(int linha, int coluna) {
        for (Personagem p : personagens) {
            if (p.getPosicao().getLinha() == linha && p.getPosicao().getColuna() == coluna) {
                return p;
            }
        }
        return null;
    }  
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();  // desserializa campos normais
        // recria o som
        passouSom = new Som("/sounds/win.wav");
    }
       
    public ArrayList<Personagem> getPersonagens(){
         return personagens;
    }
       
    public int getTentativas(){
        return tentativas;
    }
       
    public void AdicionaEntidade(Personagem p){
       personagens.add(p);
    }

   public void RemoveEntidade(Personagem p){
       personagens.remove(p);
    }

   public boolean estaVazia(){
       return personagens.isEmpty();
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
        this.pontosFase += qtd;
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
    public void transformarAguaEmGelo(int y, int x) {
        Tile novoTileDeGelo = new Tile("ground.png", true, false, false);
        this.setTile(y, x, novoTileDeGelo);
        System.out.println("Tile em (" + y + "," + x + ") foi transformado de água para gelo!");
    }
   
    public void marcarParaRemocao(Personagem p) {
        personagensParaRemover.add(p);
    }
    
    public ArrayList<Personagem> getPersonagensMarcados(){
        return this.personagensParaRemover;
    }
}
