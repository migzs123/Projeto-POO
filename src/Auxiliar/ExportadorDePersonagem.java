
package Auxiliar;

import Modelo.Personagem;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportadorDePersonagem {
   public static void salvarPersonagemZip(Personagem personagem, String caminhoZip) throws IOException {
    try (
        FileOutputStream fos = new FileOutputStream(caminhoZip);
        ZipOutputStream zos = new ZipOutputStream(fos)
    ) {
        // Cria uma entrada no zip
        ZipEntry entry = new ZipEntry("personagem.obj");
        zos.putNextEntry(entry);

        // Depois da entrada criada, crie o ObjectOutputStream
        ObjectOutputStream oos = new ObjectOutputStream(zos);
        oos.writeObject(personagem);
        oos.flush();  // garante que tudo foi escrito

        zos.closeEntry(); // fecha a entrada atual
    }
}

}
