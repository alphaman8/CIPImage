/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cipimage;

import conexao.ConnectionFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author Marcelo
 */
public class ServiceProgressBar extends Service<String> {
    
    private List<Trafo> trafoList;
    private int porcentResize;
    private String estado;
    private String municipio;
    private int ano;
    private boolean deleteImagens;

    public ServiceProgressBar(List<Trafo> trafoList, int porcentResize, String estado, String municipio, int ano, boolean deleteImagens) {
        this.trafoList = trafoList;
        this.porcentResize = porcentResize;
        this.estado = estado;
        this.municipio = municipio;
        this.ano = ano;
        this.deleteImagens = deleteImagens;
    }


    @Override
    protected Task<String> createTask() {
        return new Task<String>() {

            @Override
            protected String call() throws Exception {

                //lista de trafos maior que 0
                //estado,municipio,ano,tamanho da imagem selecionados

                Connection conn = ConnectionFactory.getConnectionWeb();

                String insert = "update pontos set imagem=? "
                        + "where estado=? and cidade=? and ano_processamento=? "
                        + "and chave_fk=? and sequencia=? and length(utilizadores) > 0";
                try {
                    PreparedStatement ps = conn.prepareStatement(insert);

                    int total = 0;
                    for (Trafo trafo : trafoList) {
                        for (Sequencia s : trafo.getSequencias()) {
                            total++;
                        }
                    }
                    total*=2;

                    int count = 0;
                    for(Trafo t : trafoList){
                        for(Sequencia s : t.getSequencias()) {

                            //TODO: Colocar msg de erro nos exception (dialog)

                            //TODO: verificar quando houver pasta vazia sem imagem. Verificar se apresenta erro.


                            File img = s.getImagens()[0];
                            String dest = s.getAbsolutePath()+"\\"+s.getSequencia()+"_thumb.jpg";
                            //Files.copy(img.toPath(), new File(dest).toPath(),StandardCopyOption.REPLACE_EXISTING);
                            //File imgResized = new File(dest);
                            BufferedImage bi = ImageIO.read(img);
                            int w = bi.getWidth();
                            int h = bi.getHeight();
                            float p = (float) porcentResize/100;
                            int newWidth = (int) (w*p);
                            int newHeight = (int) (h*p);
                            Thumbnails.of(img)
                                    .size(newWidth, newHeight)
                                    .outputFormat("jpg")
                                    .toFile(new File(dest));
                            FileInputStream fis = new FileInputStream(new File(dest));
                            ps.setBlob(1, fis);
                            ps.setString(2, estado);
                            ps.setString(3, municipio);
                            ps.setInt(4, ano);
                            ps.setString(5, t.getTrafo());
                            int seq = Integer.valueOf(s.getSequencia());
                            ps.setInt(6, seq);                    

                            int i = ps.executeUpdate();    

                            fis.close();
                           
                            System.out.println(s.getImagens()[0].getAbsoluteFile()+":"+i);
                            
                            count++;
                            updateProgress(count, total);

                       }

                    }

                    if (deleteImagens){
                        for(Trafo t : trafoList) {
                            for(Sequencia s : t.getSequencias()) {
                                String file = s.getAbsolutePath()+"\\"+s.getSequencia()+"_thumb.jpg";
                                File delete = new File(file);
                                boolean isDeleted = delete.delete();
                                System.out.println(delete.getAbsoluteFile()+":"+isDeleted);
                                
                                //update progress
                                count++;
                                updateProgress(count, total);
                            }
                        }
                    }

//                    Alert a = new Alert(Alert.AlertType.INFORMATION);
//                    a.setHeaderText("Envio de Imagens");
//                    a.setContentText("Imagens enviadas com sucesso!");
//                    a.showAndWait();
                    return "sucesso";

                } catch (Exception ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    return ex.getMessage();
//                    Alert a = new Alert(Alert.AlertType.ERROR);
//                    a.setHeaderText("Problema ao enviar");
//                    a.setContentText(ex.getMessage());
//                    a.showAndWait();
                    
                } 
        //        catch (FileNotFoundException ex) {
        //            Alert a = new Alert(Alert.AlertType.INFORMATION);
        //            a.setHeaderText("Problema ao enviar");
        //            a.setContentText(ex.getMessage());
        //            a.showAndWait();            
        //            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (IOException ex) {
        //            Alert a = new Alert(Alert.AlertType.INFORMATION);
        //            a.setHeaderText("Problema ao enviar");
        //            a.setContentText(ex.getMessage());
        //            a.showAndWait();            
        //            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        //        }                        
            }//fim do call
        };
    }
    
}
