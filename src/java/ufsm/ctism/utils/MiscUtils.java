/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * Classe com funções de utilidade genérica
 * @author SSI-Bruno
 */
public class MiscUtils {

    /**
     * Converte um objeto InputStream para byte[]
     * @param is objeto InputStream a ser convertido para byte[]
     * @return InputStream convertido para byte[]
     */
    public static byte[] inputStreamToByteArray(InputStream is) {
        int nRead;
        byte[] data = new byte[16384];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return data;
        } catch (IOException ex) {
            return new byte[0];
        }
    }
    
    /**
     * Converte um objeto File para byte[]
     * @param f objeto File  a ser convertido para byte[]
     * @return File convertido para byte[]
     */
    public static byte[] fileToByteArray(File f) {
        try {
            String absPath = f.getAbsolutePath();
            URI uri = new URI("file:///" + absPath.replace('\\', '/') );
            Path path = java.nio.file.Paths.get( uri );
            return java.nio.file.Files.readAllBytes( path );
        } catch (URISyntaxException | IOException ex) {
            return new byte[0]; 
        }
    }
    
}
