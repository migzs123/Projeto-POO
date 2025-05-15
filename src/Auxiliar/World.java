package Auxiliar;

import Controler.Tela;
import Modelo.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class World {
    private Tela tela;

    public World(String path, Tela tela) throws IOException {
        this.tela = tela;
        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            int[] pixels = new int[map.getWidth() * map.getHeight()];
            map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    int pixelAtual = pixels[x + (y * map.getWidth())];
                    processPixel(pixelAtual, y, x);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o mapa: " + path);
            throw e;
        }
    }

    private void processPixel(int pixel, int y, int x) throws IOException {
        if (pixel == 0xFFFFFFFF) { // branco - chão
            tela.setTile(y, x, new Tile(tela.getLevelAtual() + "ground.png", true));
        } else if (pixel == 0xFF000000) { // preto - parede
            tela.setTile(y, x, new Tile(tela.getLevelAtual() +"wall.png", false));
        } else if (pixel == 0xFFFF0000) { // vermelho - herói
            Hero h = new Hero("hero.png");
            h.setPosicao(y, x);
            tela.setHero(h);
            tela.addPersonagem(h);
            tela.setTile(y, x, new Tile(tela.getLevelAtual() + "ground.png", true));
            tela.atualizaCamera();
        }
        // Adicione mais condições para outros elementos do jogo
        else if (pixel == 0xFF00FF00) { // verde - saída do nível
            tela.setTile(y, x, new Tile("exit.png", true)); // Tile especial de saída
        }
    }
}
