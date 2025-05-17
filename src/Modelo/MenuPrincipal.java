package Modelo;

import Auxiliar.Consts;
import Controler.Tela;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {
    public MenuPrincipal() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                    Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fundo com imagem
        ImageIcon bgImage = new ImageIcon(getClass().getResource("/imgs/menu_background.png"));
        JLabel background = new JLabel(bgImage);
        background.setLayout(new GridBagLayout()); // permite centralizar os botões
        setContentPane(background);

        // Painel transparente para os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setOpaque(false); // deixa o fundo transparente
        painelBotoes.setLayout(new GridLayout(1, 3, 5, 20)); 

        JButton btnJogar = criarBotaoComImagem("newGameN.png", "newGameH.png");

        JButton btnCreditos = criarBotaoComImagem("CreditosN.png", "CreditosH.png");
        JButton btnSair = criarBotaoComImagem("sairN.png", "sairH.png");

        btnJogar.addActionListener(e -> iniciarJogo());
        btnCreditos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Feito por você :)"));
        btnSair.addActionListener(e -> System.exit(0));

        painelBotoes.add(btnJogar);
        painelBotoes.add(btnCreditos);
        painelBotoes.add(btnSair);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.PAGE_END; // ou SOUTH
        background.add(painelBotoes, gbc);

        setVisible(true);
    }

    private void iniciarJogo() {
        dispose();
        Tela tela = new Tela();
        tela.setVisible(true);
        tela.createBufferStrategy(2);
        tela.go();
    }
    
    private JButton criarBotaoComImagem(String normal, String hover) {
        JButton botao = new JButton(new ImageIcon(getClass().getResource("/imgs/" + normal)));

        botao.setRolloverIcon(new ImageIcon(getClass().getResource("/imgs/" + hover)));

        botao.setContentAreaFilled(false); // tira o fundo cinza
        botao.setBorderPainted(false);     // tira a borda padrão
        botao.setFocusPainted(false);      // tira o contorno de foco
        botao.setOpaque(false);            // total transparência

        return botao;
    }
    
}
