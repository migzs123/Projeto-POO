package Modelo;

import Auxiliar.Consts;
import Controler.Tela;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Menu Principal");
        setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                    Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setResizable(false);

        // Layout
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0xFFDAF1FF));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Título
        JLabel titulo = new JLabel("Passo Frágil");
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titulo);

        // Botão Jogar
        JButton jogarBtn = new JButton("Novo Jogo");
        jogarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        jogarBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        jogarBtn.addActionListener(e -> iniciarJogo());
        panel.add(jogarBtn);

        // Botão Sair
        JButton sairBtn = new JButton("Sair");
        sairBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        sairBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        sairBtn.setMaximumSize(jogarBtn.getMaximumSize());
        sairBtn.addActionListener(e -> System.exit(0));
        panel.add(Box.createVerticalStrut(10));
        panel.add(sairBtn);

        add(panel);
        setVisible(true);
    }

    private void iniciarJogo() {
        this.dispose(); // Fecha o menu
        Tela tela = new Tela(); // Cria o jogo
        tela.setVisible(true);
        tela.createBufferStrategy(2);
        tela.go(); // Inicia o loop do jogo
    }
}
