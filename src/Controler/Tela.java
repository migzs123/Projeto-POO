package Controler;

import Modelo.*;
import Auxiliar.Consts;
import Auxiliar.Desenho;
import auxiliar.Posicao;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashSet;
import java.util.Set;

public class Tela extends javax.swing.JFrame implements MouseListener, KeyListener {

    private Hero hero;
    private ControleDeJogo cj = new ControleDeJogo();
    private Graphics g2;
    private int cameraLinha = 0;
    private int cameraColuna = 0;
    
    public Fase faseAtual;
    
    private Font pixelFont;
    private final Set<Integer> teclasPressionadas = new HashSet<>();

    public Tela() {
        Desenho.setCenario(this);
        initComponents();
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                    Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);
        carregarFontePixel();
        faseAtual = new Fase(this, 1);
    }
    
     @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Ignora se já está pressionada
        if (teclasPressionadas.contains(key)) return;
        teclasPressionadas.add(key);

        if (hero == null) return;

        switch (key) {
            case KeyEvent.VK_W -> hero.moveUp();
            case KeyEvent.VK_S -> hero.moveDown();
            case KeyEvent.VK_A -> hero.moveLeft();
            case KeyEvent.VK_D -> hero.moveRight();
            case KeyEvent.VK_N -> faseAtual.proximaFase();
        }

        this.atualizaCamera();
        this.setTitle("Level " + faseAtual.getFase() + " -> Pos: "
                + hero.getPosicao().getColuna() + ", " + hero.getPosicao().getLinha());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        teclasPressionadas.remove(e.getKeyCode());
    }
    


    public void setHero(Hero h) {
        this.hero = h;
    }

    public Hero getHero() {
        return this.hero;
    }

    public int getCameraLinha() {
        return cameraLinha;
    }

    public int getCameraColuna() {
        return cameraColuna;
    }
    
    public boolean ehPosicaoValida(Posicao p) {
        // Verifica se está dentro do mundo
        if (p.getLinha() < 0 || p.getColuna() < 0 ||
            p.getLinha() >= Consts.MUNDO_ALTURA || p.getColuna() >= Consts.MUNDO_LARGURA) {
            return false;
        }

        // Verifica se o tile é transponível
        Tile t = faseAtual.getTile(p.getLinha(), p.getColuna());
        return t == null || t.isTransponivel();
    }

    public Graphics getGraphicsBuffer() {
        return g2;
    }

    public void paint(Graphics gOld) {
        Tile[][] mapaBase = faseAtual.getMapaBase();
        Graphics g = this.getBufferStrategy().getDrawGraphics();
        g2 = g.create(getInsets().left, getInsets().top, getWidth() - getInsets().right, getHeight() - getInsets().top);

        // Desenhar camada de fundo (tiles)
        for (int i = 0; i < Consts.RES; i++) {
            for (int j = 0; j < Consts.RES; j++) {
                int mapaLinha = cameraLinha + i;
                int mapaColuna = cameraColuna + j;

                if (mapaLinha < Consts.MUNDO_ALTURA && mapaColuna < Consts.MUNDO_LARGURA) {
                    Tile tile = mapaBase[mapaLinha][mapaColuna];
                    if (tile != null) {
                        tile.desenhar(mapaLinha, mapaColuna);
                    }
                }
            }
        }

        // Desenhar personagens
        if (!faseAtual.estaVazia()) {
            cj.desenhaTudo(faseAtual.getEntidades());
            cj.processaTudo(faseAtual.getEntidades());
        }

        desenharHUD(g2);
        
        g.dispose();
        g2.dispose();
        if (!getBufferStrategy().contentsLost()) {
            getBufferStrategy().show();
        }
    }
    
    private void desenharHUD(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        int larguraTela = getWidth();
        int alturaTela = getHeight();
        int alturaHUD = 60;
        int yTextoTopo = 40;
        int yTextoBase = alturaTela - 20; // Ajuste para ficar dentro do HUD inferior

        g2d.setFont(pixelFont);
        FontMetrics fm = g2d.getFontMetrics();

        String textoFase = "Fase " + faseAtual.getFase();
        String textoTentativas = "Tentativas: " + faseAtual.getTentativas();
        String textoFrutas = "2/2";
        String textoPontos = "Pontos " + 1000;

        // === HUD SUPERIOR ===
        g2d.setColor(new Color(0xFFDAF1FF));
        g2d.fillRect(0, 0, larguraTela, alturaHUD);

        g2d.setColor(new Color(0xFF0448A2)); // cor da borda
        
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(0, 0, larguraTela -1 , alturaHUD  -1); // borda superior
        g2d.setStroke(new BasicStroke(1));
        
        g2d.setColor(new Color(0xFF0448A2));
        g2d.drawString(textoFase, 20, yTextoTopo);

        int larguraTentativas = fm.stringWidth(textoTentativas);
        g2d.drawString(textoTentativas, larguraTela - larguraTentativas - 50, yTextoTopo);

        // === HUD INFERIOR ===
        g2d.setColor(new Color(0xFFDAF1FF));
        g2d.fillRect(0, 690, larguraTela, alturaHUD);

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(0xFF0448A2)); // borda inferior
        g2d.drawRect(0, 690, larguraTela, alturaHUD);
        g2d.setStroke(new BasicStroke(1));

        g2d.setColor(new Color(0xFF0448A2));
        g2d.drawString(textoFrutas, 20, 730);

        int larguraPontos = fm.stringWidth(textoPontos);
        g2d.drawString(textoPontos, larguraTela - larguraPontos - 50, 730);
    }


    private void carregarFontePixel() {
    try {
        InputStream is = getClass().getResourceAsStream("/fonts/pixel2.ttf");
        Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
        pixelFont = baseFont.deriveFont(Font.PLAIN, 20f); // tamanho ajustável
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(baseFont);
    } catch (Exception e) {
        e.printStackTrace();
        pixelFont = new Font("Monospaced", Font.PLAIN, 24); // fallback
    }
}
    
    
    public void atualizaCamera() {
        int linha = hero.getPosicao().getLinha();
        int coluna = hero.getPosicao().getColuna();

        cameraLinha = Math.max(0, Math.min(linha - Consts.RES / 2, Consts.MUNDO_ALTURA - Consts.RES));
        cameraColuna = Math.max(0, Math.min(coluna - Consts.RES / 2, Consts.MUNDO_LARGURA - Consts.RES));
    }

    public void go() {
    new Thread(() -> {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0; // 60 FPS alvo
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                repaint(); // Renderização
                delta--;
            }

            try {
                Thread.sleep(1); // Evita consumo excessivo da CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }).start();
}


    public void mousePressed(MouseEvent e) {
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POO2023-1 - Skooter");
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    
}
