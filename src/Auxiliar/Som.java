package Auxiliar;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Som {
    private Clip clip;

    public Som(String caminho) {
        try {
            URL soundURL = getClass().getResource(caminho);
            if (soundURL == null) {
                System.err.println("Arquivo de som não encontrado: " + caminho);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void tocarLoop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void tocarUmaVez() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);  // Reinicia do início
            clip.start();
        }
    }
}
