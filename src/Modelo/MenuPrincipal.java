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

        ImageIcon bgImage = new ImageIcon(getClass().getResource("/imgs/menu_background.png"));
        JLabel background = new JLabel(bgImage);
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setOpaque(false);
        painelBotoes.setLayout(new GridLayout(1, 3, 5, 20));

        // Verifica se há um save
        boolean temSave = Tela.existeSave();

        JButton btnJogarOuContinuar;
        if (temSave) {
            btnJogarOuContinuar = criarBotaoComImagem("continuarN.png", "continuarH.png");
            btnJogarOuContinuar.addActionListener(e -> carregarJogo());
        } else {
            btnJogarOuContinuar = criarBotaoComImagem("newGameN.png", "newGameH.png");
            btnJogarOuContinuar.addActionListener(e -> iniciarJogo());
        }

        JButton btnCreditos = criarBotaoComImagem("CreditosN.png", "CreditosH.png");
        JButton btnSair = criarBotaoComImagem("sairN.png", "sairH.png");

        btnCreditos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Miguel Zalochi Saffioti 15480474\n"
                + "Cibele Soares de Almeida 15676493\n"
                + "João Victor do Prado Souza 15582071"));
        btnSair.addActionListener(e -> System.exit(0));

        painelBotoes.add(btnJogarOuContinuar);
        painelBotoes.add(btnCreditos);
        painelBotoes.add(btnSair);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.PAGE_END;
        background.add(painelBotoes, gbc);

        setVisible(true);
    }

    private void iniciarJogo() {
        dispose();
        new TelaTutorial();
    }

    private void carregarJogo() {
        dispose();
        Tela tela = new Tela();
        tela.carregarJogo(); // << carrega save
        tela.setVisible(true);
        tela.createBufferStrategy(2);
        tela.go();
    }

    private JButton criarBotaoComImagem(String normal, String hover) {
        JButton botao = new JButton(new ImageIcon(getClass().getResource("/imgs/" + normal)));
        botao.setRolloverIcon(new ImageIcon(getClass().getResource("/imgs/" + hover)));
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        return botao;
    }
}

