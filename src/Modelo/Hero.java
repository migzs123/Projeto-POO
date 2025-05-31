package Modelo;

import Auxiliar.Desenho;
import Auxiliar.Som;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Hero extends Personagem {

    private transient Som falhouSom;
    private transient Som passouSom;
    private boolean morto = false;
    private ImageIcon upImage, downImage, leftImage, rightImage;
    private PowerUp powerupAtivo;
    private boolean hasKey = false;
    private boolean geloAcabouDeSerColetado = false;
    private boolean poderDeGeloEstavaAtivoNoInicioDoMovimentoAtual = false; // Nova flag

    public Hero(String nomeImagem, Fase faseAtual) {
        super(nomeImagem, faseAtual);
        this.carregarSprites();
        this.imagem = downImage;
        falhouSom = new Som("/sounds/fail.wav");
        passouSom = new Som("/sounds/win.wav");
        this.powerupAtivo = null;
    }

    @Override
    public void carregarSprites() {
        upImage = carregarImagem(nomeImagem.replace(".png", "") + "_up.png");
        downImage = carregarImagem(nomeImagem.replace(".png", "") + "_down.png");
        leftImage = carregarImagem(nomeImagem.replace(".png", "") + "_left.png");
        rightImage = carregarImagem(nomeImagem.replace(".png", "") + "_right.png");
    }

    public void getKey() {
        this.hasKey = true;
        System.out.println("hasKey status:" + this.hasKey);
    }

    public boolean getKeyStatus() {
        return this.hasKey;
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

        Tile tileAtual = faseAtual.getTile(this.getPosicao().getLinha(), this.getPosicao().getColuna());
        System.out.println("Hero pisou em tile: " + (tileAtual != null ? tileAtual.getNomeImagem() : "null"));

        if (tileAtual != null && "water.png".equals(tileAtual.getNomeImagem())) {
            System.out.println("Detectou água! Verificando poder de gelo...");
            if (this.powerupAtivo != null &&
                "gelo.png".equals(this.powerupAtivo.getNomeImagem()) &&
                this.powerupAtivo.estaAtivo()) {

                System.out.println("Herói tem poder de gelo ativo! Transformando água em gelo...");
                faseAtual.transformarAguaEmGelo(this.getPosicao().getLinha(), this.getPosicao().getColuna());
                this.powerupAtivo.usarChance(); // Consome a carga
                if (!this.powerupAtivo.estaAtivo()) { // Verifica se esgotou
                    System.out.println("Poder de Gelo esgotado APÓS usar chance.");
                    this.powerupAtivo = null; // Torna o poder inativo/nulo para as próximas validações
                }
                return true;
            } else {
                System.out.println("Herói não tem poder de gelo (ou esgotou antes)! Morrendo...");
                if (falhouSom != null) {
                    falhouSom.tocarUmaVez();
                }
                faseAtual.carregarFase(faseAtual.getFase());
                return false;
            }
        }

        // ... (resto do método validaPosicao igual ao anterior)
        if (tileAtual != null && tileAtual.isFim()) {
            if (passouSom != null) {
                passouSom.tocarUmaVez();
            }
            faseAtual.proximaFase();
            return false;
        }

        if (tileAtual != null && !tileAtual.isTransponivel()) {
            System.out.println("Hero.validaPosicao: Tile é uma barreira não transponível: " + tileAtual.getNomeImagem() + ". Revertendo movimento.");
            this.voltaAUltimaPosicao();
            return false;
        }

        for (Personagem p : new ArrayList<>(faseAtual.getPersonagens())) {
            if (p != null && p.getPosicao() != null && p.getPosicao().igual(this.getPosicao())) {
                if (p instanceof Food) {
                    ((Food) p).checarColisao();
                } else if (p instanceof Botao) {
                    ((Botao) p).checarColisao();
                } else if (p instanceof PowerUp) {
                    ((PowerUp) p).checarColisao();
                } else if (p instanceof Inimigo) {
                    ((Inimigo) p).checarColisao();
                } else if (p instanceof Key) {
                    ((Key) p).checarColisao();
                } else if (p instanceof Tranca) {
                    ((Tranca) p).checarColisao();
                }
            }
        }
        return true;
    }

    public void precisaMorrer() {
        if (morto) {
            return;
        }
        int linha = this.getPosicao().getLinha();
        int coluna = this.getPosicao().getColuna();
        Tile tileAtual = faseAtual.getTile(linha, coluna);
        if (tileAtual != null && tileAtual.isMortal()) {
            System.out.println("O herói caiu na água gelada!");
            if (falhouSom != null) {
                falhouSom.tocarUmaVez();
            }
            morto = true;
            faseAtual.reiniciarFase();
        }
    }

    // Assinatura de preencherComAgua modificada para receber o estado do poder no início do movimento
    private void preencherComAgua(int prevY, int prevX, boolean poderGeloEstavaAtivoNoInicio) {
        for (Personagem p : faseAtual.getPersonagens()) {
            if (p instanceof Botao && p.getPosicao().getLinha() == prevY && p.getPosicao().getColuna() == prevX) {
                System.out.println("Hero.preencherComAgua: Tile anterior (" + prevY + "," + prevX + ") é chão de botão, mantendo como está.");
                return;
            }
        }

        Tile tileAnterior = faseAtual.getTile(prevY, prevX);
        System.out.println("Hero.preencherComAgua: Verificando tile anterior (" + prevY + "," + prevX + "): " +
            (tileAnterior != null ? tileAnterior.getNomeImagem() : "null") +
            ". Poder de gelo no INÍCIO do movimento estava: " + poderGeloEstavaAtivoNoInicio);

        boolean aplicarLogicaDeGeloParaEsteRastro = poderGeloEstavaAtivoNoInicio;

        if (this.geloAcabouDeSerColetado && poderGeloEstavaAtivoNoInicio) {
            // Se o gelo foi pego neste movimento, E o poder de gelo se tornou ativo neste movimento,
            // o rastro deste movimento específico ainda não deve ser de gelo.
            System.out.println("Hero.preencherComAgua: Gelo recém-coletado (e poder tornou-se ativo neste mov). Rastro será SEM gelo para este movimento.");
            aplicarLogicaDeGeloParaEsteRastro = false;
            // O reset da flag geloAcabouDeSerColetado é feito aqui para garantir que só afete um preenchimento
        }
        // Importante: Resetar a flag geloAcabouDeSerColetado APÓS seu uso na decisão.
        if(this.geloAcabouDeSerColetado){
            this.geloAcabouDeSerColetado = false;
            System.out.println("Hero.preencherComAgua: Flag geloAcabouDeSerColetado resetada para false.");
        }


        if (aplicarLogicaDeGeloParaEsteRastro) {
            System.out.println("Hero.preencherComAgua: Aplicando lógica de rastro COM GELO.");
            if (tileAnterior != null && "ground.png".equals(tileAnterior.getNomeImagem())) {
                System.out.println("Hero.preencherComAgua: [COM GELO EFETIVO] Tile anterior é CHÃO - mantendo como chão!");
            } else if (tileAnterior != null) {
                System.out.println("Hero.preencherComAgua: [COM GELO EFETIVO] Transformando tile anterior ("+ tileAnterior.getNomeImagem() +") em CHÃO PERMANENTE!");
                faseAtual.setTile(prevY, prevX, new Tile("ground.png", false, true, false));
            }
        } else {
            System.out.println("Hero.preencherComAgua: Aplicando lógica de rastro SEM GELO.");
            if (tileAnterior != null && "ground.png".equals(tileAnterior.getNomeImagem())) {
                System.out.println("Hero.preencherComAgua: [SEM GELO EFETIVO] Transformando chão congelado anterior em água.");
                faseAtual.setTile(prevY, prevX, new Tile("water.png", true, true, false));
                return;
            }
            if (tileAnterior != null && "water.png".equals(tileAnterior.getNomeImagem())) {
                System.out.println("Hero.preencherComAgua: [SEM GELO EFETIVO] Tile anterior já é água - mantendo como água.");
                return;
            }
            if (tileAnterior != null) {
                System.out.println("Hero.preencherComAgua: [SEM GELO EFETIVO] Transformando tile anterior ("+ tileAnterior.getNomeImagem() +") em água.");
                faseAtual.setTile(prevY, prevX, new Tile("water.png", true, true, false));
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        falhouSom = new Som("/sounds/fail.wav");
        passouSom = new Som("/sounds/win.wav");
    }

    // Métodos de movimento modificados para usar a nova flag
    private void atualizarEstadoPoderGeloInicioMovimento() {
        this.poderDeGeloEstavaAtivoNoInicioDoMovimentoAtual =
            (this.powerupAtivo != null &&
             "gelo.png".equals(this.powerupAtivo.getNomeImagem()) &&
             this.powerupAtivo.estaAtivo());
    }

    public boolean moveUp() {
        this.imagem = upImage;
        atualizarEstadoPoderGeloInicioMovimento(); // Captura estado ANTES de qualquer coisa
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveUp() && validaPosicao()) { // validaPosicao pode mudar o estado de powerupAtivo
            preencherComAgua(linhaAnterior, colunaAnterior, this.poderDeGeloEstavaAtivoNoInicioDoMovimentoAtual);
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        this.imagem = downImage;
        atualizarEstadoPoderGeloInicioMovimento();
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveDown() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior, this.poderDeGeloEstavaAtivoNoInicioDoMovimentoAtual);
            return true;
        }
        return false;
    }

    public boolean moveLeft() {
        this.imagem = leftImage;
        atualizarEstadoPoderGeloInicioMovimento();
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveLeft() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior, this.poderDeGeloEstavaAtivoNoInicioDoMovimentoAtual);
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        this.imagem = rightImage;
        atualizarEstadoPoderGeloInicioMovimento();
        int linhaAnterior = pPosicao.getLinha();
        int colunaAnterior = pPosicao.getColuna();

        if (super.moveRight() && validaPosicao()) {
            preencherComAgua(linhaAnterior, colunaAnterior, this.poderDeGeloEstavaAtivoNoInicioDoMovimentoAtual);
            return true;
        }
        return false;
    }

    public void resetarMorte() {
        this.morto = false;
    }

    public void coletouPowerup(PowerUp powerupColetado) {
        this.powerupAtivo = powerupColetado;
        System.out.println("Hero.coletouPowerup: PowerUp recebido: " + (powerupColetado != null ? powerupColetado.getNomeImagem() : "null"));
        if (this.powerupAtivo != null) {
            this.powerupAtivo.ativar();
            System.out.println("Hero.coletouPowerup: PowerUp ativado via hero.ativar(). Estado: " + this.powerupAtivo.estaAtivo() + ", Chances: " + this.powerupAtivo.getChancesRestantes());
            if ("gelo.png".equals(this.powerupAtivo.getNomeImagem())) {
                System.out.println("Hero.coletouPowerup: É o PowerUp de GELO! Setando flag geloAcabouDeSerColetado = true.");
                this.geloAcabouDeSerColetado = true;
            }
        } else {
            System.err.println("Hero.coletouPowerup: Tentativa de coletar um power-up nulo.");
        }
    }
}