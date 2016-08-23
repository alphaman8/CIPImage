/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cipimage;

import java.io.File;

/**
 *
 * @author Marcelo
 */
public class Sequencia {

    public Sequencia(String absolutePath, String sequencia, File[] imagens) {
        this.absolutePath = absolutePath;
        this.sequencia = sequencia;
        this.imagens = imagens;
    }
        
    private String absolutePath;
    private String sequencia;
    private File[] imagens;

    /**
     * @return the sequencia
     */
    public String getSequencia() {
        return sequencia;
    }

    /**
     * @param sequencia the sequencia to set
     */
    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    /**
     * @return the imagens
     */
    public File[] getImagens() {
        return imagens;
    }

    /**
     * @param imagens the imagens to set
     */
    public void setImagens(File[] imagens) {
        this.imagens = imagens;
    }

    /**
     * @return the absolutePath
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @param absolutePath the absolutePath to set
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
    
}
