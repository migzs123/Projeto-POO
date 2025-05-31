package Controler;

import Modelo.*;
import Auxiliar.Consts;
import Auxiliar.Desenho;
import Auxiliar.Som;
import auxiliar.Posicao;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.List;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;

public class Tela extends javax.swing.JFrame implements MouseListener, KeyListener {

    private ControleDeJogo cj = new ControleDeJogo();
    private Graphics g2;
    private int cameraLinha = 0;
    private int cameraColuna = 0;
    
    public Fase faseAtual;
    
    private Som musicaGameplay;
    
    private Image chaveIcon;
    private Image powerUp;
    
    private Font pixelFont;
    private final Set<Integer> teclasPressionadas = new HashSet<>();
    
/*------------CONSTRUTOR------------*/
    public Tela() {
        chaveIcon = new ImageIcon(getClass().getResource("/imgs/chave.png")).getImage();
        powerUp = new ImageIcon(getClass().getResource("/imgs/gelo.png")).getImage();
        Desenho.setCenario(this);
        initComponents();
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                    Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);
        carregarFontePixel();
        setLocationRelativeTo(null);
        faseAtual = new Fase(this, 1);
        this.atualizaCamera();
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
                deletarSave(); 
            }
        });
        
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable t = dtde.getTransferable();
                java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

                for (File file : files) {
                    if (file.getName().endsWith(".zip")) {
                        int linha = (dtde.getLocation().y - getInsets().top) / Consts.CELL_SIDE + cameraLinha;
                        int coluna = (dtde.getLocation().x - getInsets().left) / Consts.CELL_SIDE + cameraColuna;

                        File tempDir = new File("temp_zip");
                        if (!tempDir.exists()) tempDir.mkdirs();

                        unzip(file.getAbsolutePath(), tempDir.getAbsolutePath());

                        File objetoZipado = new File(tempDir, "personagem.obj");
                        if (objetoZipado.exists()) {
                            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(objetoZipado))) {
                                Personagem p = (Personagem) in.readObject();
                                p.setPosicao(linha, coluna);
                            if(faseAtual.getPersonagem(linha, coluna) != null || faseAtual.getTile(linha, coluna).isValido()){
                                return;
                            }
                               if (p instanceof Hero h) {
                                    faseAtual.RemoveEntidade(faseAtual.getHero());
                                    h.setFase(faseAtual); 
                                    faseAtual.setHero(h);
                                }
                                else if (p instanceof Botao b) {
                                    faseAtual.setTile(faseAtual.getBotao().getPosicao().getLinha(),
                                             faseAtual.getBotao().getPosicao().getColuna(),
                                             new Tile("ground.png", true, false,false));
                                    b.setBombas(faseAtual.getBotao().getBombas());
                                    faseAtual.RemoveEntidade(faseAtual.getBotao());
                                    b.setFase(faseAtual);
                                    faseAtual.setTile(linha, coluna, new Tile("wall.png", true, false, false));
                                }
                                else if (p instanceof Bomba b) {
                                    b.setFase(faseAtual);
                                    if (faseAtual.getBotao() != null) {
                                        faseAtual.getBotao().adicionarBomba(b);
                                    }
                                }
                                else if (p instanceof Food f) {
                                    f.setFase(faseAtual);
                                    faseAtual.addMaxComidas();
                                }
                                else if (p instanceof Tranca || p instanceof Key){
                                    p.setFase(faseAtual);
                                }
                               else if (p instanceof Inimigo){
                                    p.setFase(faseAtual);
                                }
                                // Adiciona o personagem à fase
                                faseAtual.AdicionaEntidade(p);
                                }
                        }
                        faseAtual.RemoveEntidade(faseAtual.getHero());
                        faseAtual.AdicionaEntidade(faseAtual.getHero());
                        deleteDirectory(tempDir);
                    }
                }

                repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });

        musicaGameplay = new Som("/sounds/ost.wav");
    }
    private void deleteDirectory(File dir) {
    File[] files = dir.listFiles();
    if (files != null) {
        for (File f : files) {
            if (f.isDirectory()) {
                deleteDirectory(f);
            } else {
                f.delete();
            }
        }
    }
    dir.delete();
}
    
    private void unzip(String zipFilePath, String destDir) throws IOException {
    byte[] buffer = new byte[1024];
    try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new FileInputStream(zipFilePath))) {
        java.util.zip.ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = new File(destDir, zipEntry.getName());
            try (FileOutputStream fos = new FileOutputStream(newFile)) {
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
    }
}

/*------------SALVAMENTO------------*/
    public void salvarJogo() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("save.dat"))) {
            out.writeObject(faseAtual);
            System.out.println("Jogo salvo com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void carregarJogo() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("save.dat"))) {
            faseAtual = (Fase) in.readObject();
            faseAtual.setTela(this);
            faseAtual.recarregarRecursos();

            // Atualiza imagens dos botões (pra refletir o estado apertado)
            for (Personagem p : faseAtual.getPersonagens()) {
                if (p instanceof Botao) {
                    ((Botao)p).atualizaImagem();
                }
            }
            this.atualizaCamera();
            System.out.println("Jogo carregado com sucesso."); 
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void deletarSave() {
        java.io.File saveFile = new java.io.File("save.dat");
        if (saveFile.exists()) {
            if (saveFile.delete()) {
                System.out.println("Save deletado com sucesso.");
            } else {
                System.out.println("Falha ao deletar o save.");
            }
        } else {
            System.out.println("Arquivo de save não existe.");
        }
    }
     
    public static boolean existeSave() {
        java.io.File saveFile = new java.io.File("save.dat");
        return saveFile.exists() && saveFile.length() > 0;
    }
    
/*------------CAMERA------------*/
    public void atualizaCamera() {
        int linha = faseAtual.getHero().getPosicao().getLinha();
        int coluna = faseAtual.getHero().getPosicao().getColuna();

        // === Vertical: sempre centraliza ===
        cameraLinha = linha - Consts.RES / 2;
        if (cameraLinha < 0) {
            cameraLinha = 0;
        } else if (cameraLinha > Consts.MUNDO_ALTURA - Consts.RES) {
            cameraLinha = Consts.MUNDO_ALTURA - Consts.RES;
        }

        // === Horizontal: mantém a lógica da margem ===
        int limiteEsquerdo = cameraColuna + Consts.MARGEM;
        int limiteDireito = cameraColuna + Consts.RES - Consts.MARGEM - 1;

        if (coluna < limiteEsquerdo) {
            cameraColuna = Math.max(0, cameraColuna - (limiteEsquerdo - coluna));
        } else if (coluna > limiteDireito) {
            cameraColuna = Math.min(Consts.MUNDO_LARGURA - Consts.RES, cameraColuna + (coluna - limiteDireito));
        }
}
    
    public int getCameraLinha() {
        return cameraLinha;
    }

    public int getCameraColuna() {
        return cameraColuna;
    }
    
/*------------POSIÇÕES------------*/    
    public boolean ehPosicaoValida(Posicao p) {
        // Verifica se está dentro do mundo
        if (p.getLinha() < 0 || p.getColuna() < 0 ||
            p.getLinha() >= Consts.MUNDO_ALTURA || p.getColuna() >= Consts.MUNDO_LARGURA) {
            return false;
        }

        // Verifica se o tile é transponível
        Tile t = faseAtual.getTile(p.getLinha(), p.getColuna());
        Personagem c = faseAtual.getPersonagem(p.getLinha(), p.getColuna());
        if(c instanceof Tranca && faseAtual.getHero().getKeyStatus()){
            return true;
        }
        return (t == null || t.isTransponivel()) && (c==null || c.isTransponivel());
    }

/*------------GRAFICOS------------*/
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
            cj.desenhaTudo(faseAtual.getPersonagens());
            cj.processaTudo(faseAtual.getPersonagens(), faseAtual.getPersonagensMarcados());
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
        String textoFrutas = faseAtual.getComidas() + "/" + faseAtual.getMaxComidas();
        String textoPontos = "Pontos " + faseAtual.getPontos();

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
        Hero hero = faseAtual.getHero();
        
        int xIcon = 100;
        int yIcon = 685; // um pouco acima da borda inferior
        int tamanhoIcone = 70;
        int espacamento = 10;
        
        
        
        if (hero.getKeyStatus()) {
            g2d.drawImage(chaveIcon, xIcon, yIcon, tamanhoIcone, tamanhoIcone, null);

            xIcon += tamanhoIcone + espacamento;
        }

        
        if (hero.getPowerUp() != null && hero.getPowerUp().estaAtivo()) {
            g2d.drawImage(powerUp, xIcon, yIcon, tamanhoIcone, tamanhoIcone, null);
            g2d.setColor(new Color(0xFFFF0000));
    
            String texto = "" + hero.getPowerUp().getChancesRestantes();
            fm = g2d.getFontMetrics();
            int textoLargura = fm.stringWidth(texto);
            int textoAltura = fm.getAscent(); 

            int textoX = xIcon + (tamanhoIcone - textoLargura) / 2;
            int textoY = yIcon + (tamanhoIcone + textoAltura) / 2;

            g2d.drawString(texto, textoX, textoY+12);
        }
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
    
    public void go() {
        musicaGameplay.tocarLoop();
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
  
/*------------INPUTS------------*/
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Ignora se já está pressionada
        if (teclasPressionadas.contains(key)) return;
        teclasPressionadas.add(key);

        if (faseAtual.getHero() == null) return;

        switch (key) {
            case KeyEvent.VK_W -> faseAtual.getHero().moveUp();
            case KeyEvent.VK_S -> faseAtual.getHero().moveDown();
            case KeyEvent.VK_A -> faseAtual.getHero().moveLeft();
            case KeyEvent.VK_D -> faseAtual.getHero().moveRight();
            case KeyEvent.VK_G -> salvarJogo(); // G de gravar
            case KeyEvent.VK_L -> carregarJogo(); // L de load
            case KeyEvent.VK_R -> this.faseAtual.reiniciarFase();
            case KeyEvent.VK_N -> this.faseAtual.reiniciarJogo();
        }

        this.atualizaCamera();
        this.setTitle("Level " + faseAtual.getFase() + " -> Pos: "
                + faseAtual.getHero().getPosicao().getColuna() + ", " + faseAtual.getHero().getPosicao().getLinha());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        teclasPressionadas.remove(e.getKeyCode());
    }
    
    public void mousePressed(MouseEvent e) {
    }
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

    public void irParaFim() {
        dispose();
        musicaGameplay.parar();
        this.deletarSave();
        new TelaFim(faseAtual.getTentativas(), faseAtual.getPontos());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
