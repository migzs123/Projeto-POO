    package Modelo;

    import Auxiliar.Consts;
    import Auxiliar.Desenho;
    import java.awt.Graphics;
    import java.awt.Image;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.io.Serializable;
    import java.net.URL;
    import java.util.logging.Level;
    import java.util.logging.Logger;
    import javax.swing.ImageIcon;

    public class Hero extends Personagem implements Serializable{
        private ImageIcon upImage, downImage, leftImage, rightImage;
        private String baseName;

        private Fase faseAtual;

        public Hero(String sNomeImagePNG, Fase faseAtual) {
            super(sNomeImagePNG);
            this.baseName = sNomeImagePNG.replace(".png", ""); // "Robbo"
            this.loadDirectionalSprites();
            this.iImage = downImage; // começa olhando para baixo
            this.faseAtual = faseAtual;
        }

        private void loadDirectionalSprites() {
            upImage = carregarImagem(baseName + "_up.png");
            downImage = carregarImagem(baseName + "_down.png");
            leftImage = carregarImagem(baseName + "_left.png");
            rightImage = carregarImagem(baseName + "_right.png");
        }

          private ImageIcon carregarImagem(String nomeImagem) {
            try {
                URL imgURL = getClass().getResource("/imgs/" + nomeImagem);
                if (imgURL == null) throw new IOException("Imagem não encontrada: " + nomeImagem);
                Image img = new ImageIcon(imgURL).getImage();
                BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);
                g.dispose();
                return new ImageIcon(bi);
            } catch (IOException ex) {
                System.out.println("Erro ao carregar sprite: " + nomeImagem);
                return this.iImage; // fallback: imagem padrão carregada pelo Personagem
            }
        }
        public void voltaAUltimaPosicao() {
            this.pPosicao.volta();
        }

        public boolean setPosicao(int linha, int coluna) {
            return this.pPosicao.setPosicao(linha, coluna);

        }

        private boolean validaPosicao() {
            if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
                this.voltaAUltimaPosicao();
                return false;
            }

            // Checa se a nova posição é mortal
            Tile tileAtual = faseAtual.getTile(this.getPosicao().getLinha(), this.getPosicao().getColuna());
            if (tileAtual != null && tileAtual.isMortal()) {
                System.out.println("GAME OVER: O herói caiu na água gelada!");
                // aqui você pode reiniciar a fase ou encerrar o jogo
                Desenho.acessoATelaDoJogo().faseAtual.carregarFase(faseAtual.getFase()); // reinicia a fase atual
                return false;
            }
            
            if(tileAtual != null && tileAtual.isFim()){
                faseAtual.proximaFase();
                return false;
            }

            return true;
        }

        public boolean moveUp() {
            this.iImage = upImage;
            try {
                faseAtual.setTile(this.pPosicao.getLinha(),this.pPosicao.getColuna() , new Tile("water.png", true, true));
            } catch (IOException ex) {
                Logger.getLogger(Hero.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (super.moveUp()) return validaPosicao();
            return false;
        }

        public boolean moveDown() {
            this.iImage = downImage;
            try {
                faseAtual.setTile(this.pPosicao.getLinha(),this.pPosicao.getColuna() , new Tile("water.png", true, true));
            } catch (IOException ex) {
                Logger.getLogger(Hero.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (super.moveDown()) return validaPosicao();
            return false;
        }

        public boolean moveRight() {
            this.iImage = rightImage;
            try {
                faseAtual.setTile(this.pPosicao.getLinha(),this.pPosicao.getColuna() , new Tile("water.png", true, true));
            } catch (IOException ex) {
                Logger.getLogger(Hero.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (super.moveRight()) return validaPosicao();
            return false;
        }

        public boolean moveLeft() {
            this.iImage = leftImage;
            try {
                faseAtual.setTile(this.pPosicao.getLinha(),this.pPosicao.getColuna() , new Tile("water.png", true, true));
            } catch (IOException ex) {
                Logger.getLogger(Hero.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (super.moveLeft()) return validaPosicao();
            return false;
        }
    }
