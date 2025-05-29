package Modelo;

import Auxiliar.Consts;
import Controler.Tela;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TelaTutorial extends JFrame {
    public TelaTutorial() {
        setTitle("Tutorial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Consts.RES * Consts.CELL_SIDE, Consts.RES * Consts.CELL_SIDE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Carrega a imagem original de 1000x750
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/imgs/tutorial.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                Consts.RES * Consts.CELL_SIDE,
                Consts.RES * Consts.CELL_SIDE,
                Image.SCALE_SMOOTH
        );

        // Aplica imagem redimensionada como fundo
        JLabel background = new JLabel(new ImageIcon(scaledImage));
        setContentPane(background);

        // Remove layout automático para não interferir
        setLayout(null);
        setVisible(true);

        // Inicia o jogo ao pressionar qualquer tecla
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                iniciarJogo();
            }
        });
    }

    private void iniciarJogo() {
        dispose();
        Tela tela = new Tela();
        tela.setVisible(true);
        tela.createBufferStrategy(2);
        tela.go();
    }
}