package Modelo;

import Auxiliar.Consts;
import Auxiliar.Som;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TelaFim extends JFrame {

    private Font pixelFont;
    private Som endSom = new Som("/sounds/end.wav");

    public TelaFim(int tentativas, int pontos) {
        endSom.tocarUmaVez();
        setTitle("Fim de Jogo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Consts.RES * Consts.CELL_SIDE, Consts.RES * Consts.CELL_SIDE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Carrega a fonte personalizada
        carregarFontePixel();

        // Carrega imagem de fundo
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/imgs/TelaFim.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                Consts.RES * Consts.CELL_SIDE,
                Consts.RES * Consts.CELL_SIDE,
                Image.SCALE_SMOOTH
        );

        JLabel background = new JLabel(new ImageIcon(scaledImage));
        background.setLayout(null); // necessário para posicionar os textos sobre a imagem
        setContentPane(background);

        int screenWidth = Consts.RES * Consts.CELL_SIDE;
        int screenHeight = Consts.RES * Consts.CELL_SIDE;

        Font textoFont = pixelFont.deriveFont(30f);

        // Texto Tentativas (mais acima do centro)
        JLabel texto1 = new JLabel("Tentativas " + tentativas, SwingConstants.CENTER);
        texto1.setFont(textoFont);
        texto1.setForeground(new Color(0xFF0448A2));
        texto1.setBounds(0, screenHeight / 2 - 40, screenWidth, 40);
        background.add(texto1);

        // Texto Pontos (mais abaixo do centro)
        JLabel texto2 = new JLabel("Pontos " + pontos, SwingConstants.CENTER);
        texto2.setFont(textoFont);
        texto2.setForeground(new Color(0xFF0448A2));
        texto2.setBounds(0, screenHeight / 2 + 10, screenWidth, 40);
        background.add(texto2);
        
        setLayout(null);
        setVisible(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                dispose();
                new MenuPrincipal();
            }
        });
    }

    private void carregarFontePixel() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/pixel2.ttf");
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            pixelFont = baseFont; // só define aqui, deriveFont será feito depois
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Monospaced", Font.PLAIN, 24); // fallback
        }
    }
}
