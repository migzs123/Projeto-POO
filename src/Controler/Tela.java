package Controler;

import Modelo.*;
import Auxiliar.Consts;
import Auxiliar.Desenho;
import auxiliar.Posicao;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Tela extends javax.swing.JFrame implements MouseListener, KeyListener {

    private Hero hero;
    private ControleDeJogo cj = new ControleDeJogo();
    private Graphics g2;
    private int cameraLinha = 0;
    private int cameraColuna = 0;
    
    public Fase faseAtual;

    public Tela() {
        Desenho.setCenario(this);
        initComponents();
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                    Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);
        
        faseAtual = new Fase(this, 1);
    }
    
    public void keyPressed (KeyEvent e){
        if(hero == null) return;
        
        if(e.getKeyCode() == KeyEvent.VK_W) hero.moveUp();
        else if(e.getKeyCode() == KeyEvent.VK_S) hero.moveDown();
        else if(e.getKeyCode() == KeyEvent.VK_A) hero.moveLeft();
        else if(e.getKeyCode() == KeyEvent.VK_D) hero.moveRight();
        
        if(e.getKeyCode() == KeyEvent.VK_N){
            faseAtual.proximaFase();
        }
        this.atualizaCamera();
        this.setTitle("Level " + faseAtual.getFase() + " -> Pos: " + hero.getPosicao().getColuna() + ", " + 
                      hero.getPosicao().getLinha());
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

        g.dispose();
        g2.dispose();
        if (!getBufferStrategy().contentsLost()) {
            getBufferStrategy().show();
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

    public void keyReleased(KeyEvent e) {
    }
}
