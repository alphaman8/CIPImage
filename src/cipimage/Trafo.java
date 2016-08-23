/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cipimage;

import java.util.List;

/**
 *
 * @author Marcelo
 */
public class Trafo {

    public Trafo(String absolutePath, String trafo, List<Sequencia> sequencias) {
        this.absolutePath = absolutePath;
        this.trafo = trafo;
        this.sequencias = sequencias;
    }

    
        
    private String absolutePath;
    private String trafo;
    private List<Sequencia> sequencias;

    /**
     * @return the trafo
     */
    public String getTrafo() {
        return trafo;
    }

    /**
     * @param trafo the trafo to set
     */
    public void setTrafo(String trafo) {
        this.trafo = trafo;
    }

    /**
     * @return the sequencias
     */
    public List<Sequencia> getSequencias() {
        return sequencias;
    }

    /**
     * @param sequencias the sequencias to set
     */
    public void setSequencias(List<Sequencia> sequencias) {
        this.sequencias = sequencias;
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
