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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author Marcelo
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private CheckBox checkDeleteImages;
    @FXML
    private TextField dirField;
    @FXML
    private TreeView dirTree;
    @FXML
    private ComboBox<String> comboEstados;    
    @FXML
    private ComboBox<String> comboMunicipios;   
    
    @FXML
    private ComboBox<String> comboResizeImage;       
    
    @FXML
    private ComboBox<String> comboAnos;       
    
    private void listarEstadosNoCombo() {            
        String select = "Select * from estados order by nome";
        try {
            Connection conn = ConnectionFactory.getConnectionWeb();
            PreparedStatement ps = conn.prepareStatement(select);
            ResultSet rs = ps.executeQuery();
            
            ObservableList<String> estadosList = FXCollections.observableArrayList();
            
            while (rs.next()) {
                estadosList.add(rs.getString("nome"));
            }
            comboEstados.setItems(estadosList);
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }       
                
    }    
    
    @FXML
    private void selecionaEstado(ActionEvent event) {    
        String e = comboEstados.getSelectionModel().getSelectedItem();
        
        String select = "select distinct(cidade) from transformadores where estado=? order by cidade";
        
        PreparedStatement ps;
        try {
            
            Connection conn = ConnectionFactory.getConnectionWeb();
            ps = conn.prepareStatement(select);
            ps.setString(1, e);
            
            ResultSet rs = ps.executeQuery();
            
            ObservableList<String> municipioList = FXCollections.observableArrayList();
            
            while (rs.next()) {
                municipioList.add(rs.getString("cidade"));
            }                
            comboMunicipios.setItems(municipioList);
            conn.close();
                
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //System.out.println(e);
    }    
    
    @FXML
    private void selecionaMunicipio(ActionEvent event) {    
        String e = comboMunicipios.getSelectionModel().getSelectedItem();
        
        String select = "select distinct(ano_processamento) from transformadores\n" +
                        "where estado=? and cidade=? order by ano_processamento";
        
        PreparedStatement ps;
        try {
            
            Connection conn = ConnectionFactory.getConnectionWeb();
            ps = conn.prepareStatement(select);
            ps.setString(1, comboEstados.getSelectionModel().getSelectedItem());
            ps.setString(2, e);
            
            ResultSet rs = ps.executeQuery();
            
            ObservableList<String> anoList = FXCollections.observableArrayList();
            
            while (rs.next()) {
                anoList.add(rs.getString("ano_processamento"));
            }                
            comboAnos.setItems(anoList);
            conn.close();
                
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //System.out.println(e);
    }        
    
    @FXML
    private void enviarImagens(ActionEvent event) {    
        
        //TODO: colocar checkbox de apagar imagens no final da exportação
        
        //TODO: verificar se todos os campos necessário estão preenchidos antes de enviar imagens, se possível colocar um progresso de etapas
        //lista de trafos maior que 0
        //estado,municipio,ano,tamanho da imagem selecionados
        
        Connection conn = ConnectionFactory.getConnectionWeb();
        
        String insert = "update pontos set imagem=? "
                + "where estado=? and cidade=? and ano_processamento=? "
                + "and chave_fk=? and sequencia=? and length(utilizadores) > 0";
        try {
            PreparedStatement ps = conn.prepareStatement(insert);
            
            for(Trafo t : trafoList){
                for(Sequencia s : t.getSequencias()) {
                    
                    //TODO: colocar barra de progresso
                    //TODO: Colocar msg de erro nos exception (dialog)
          
                    //TODO: verificar quando houver pasta vazia sem imagem. Verificar se apresenta erro.
                    
                    int porcent = Integer.valueOf(comboResizeImage.getSelectionModel().getSelectedItem());
                    
                    File img = s.getImagens()[0];
                    String dest = s.getAbsolutePath()+"\\"+s.getSequencia()+"_thumb.jpg";
                    //Files.copy(img.toPath(), new File(dest).toPath(),StandardCopyOption.REPLACE_EXISTING);
                    //File imgResized = new File(dest);
                    BufferedImage bi = ImageIO.read(img);
                    int w = bi.getWidth();
                    int h = bi.getHeight();
                    float p = (float) porcent/100;
                    int newWidth = (int) (w*p);
                    int newHeight = (int) (h*p);
                    Thumbnails.of(img)
                            .size(newWidth, newHeight)
                            .outputFormat("jpg")
                            .toFile(new File(dest));
                    FileInputStream fis = new FileInputStream(new File(dest));
                    ps.setBlob(1, fis);
                    ps.setString(2, comboEstados.getSelectionModel().getSelectedItem());
                    ps.setString(3, comboMunicipios.getSelectionModel().getSelectedItem());
                    int ano = Integer.valueOf(comboAnos.getSelectionModel().getSelectedItem());
                    ps.setInt(4, ano);
                    ps.setString(5, t.getTrafo());
                    int seq = Integer.valueOf(s.getSequencia());
                    ps.setInt(6, seq);                    
                    
                    int i = ps.executeUpdate();    
                    
                    fis.close();

                    System.out.println(s.getImagens()[0].getAbsoluteFile()+":"+i);
                }
            }
            
            if (checkDeleteImages.isSelected()){
                for(Trafo t : trafoList) {
                    for(Sequencia s : t.getSequencias()) {
                        String file = s.getAbsolutePath()+"\\"+s.getSequencia()+"_thumb.jpg";
                        File delete = new File(file);
                        boolean isDeleted = delete.delete();
                        System.out.println(delete.getAbsoluteFile()+":"+isDeleted);
                    }
                }
            }
            
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Envio de Imagens");
            a.setContentText("Imagens enviadas com sucesso!");
            a.showAndWait();
            
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }        
                
    }
    
    private List<Trafo> trafoList;
    
    @FXML
    private void abrirDiretorio(ActionEvent event) throws IOException {
        //label.setText("Hello World!");
        DirectoryChooser dc = new DirectoryChooser();
        File file = dc.showDialog(null);
        
        if (file == null) {
            dirField.setText("Nenhum diretório selecionado");
        } else {
            dirField.setText(file.getAbsolutePath());
            
            //VERIFICA ARVORE DE DIRETORIO E IMAGENS
            trafoList = new ArrayList<>();
            List<Sequencia> seqList = new ArrayList<>();
                    
            for (File trafos : file.listFiles()) {
                if (trafos.isDirectory()){
                    for (File sequencias : trafos.listFiles()) {
                        if (sequencias.isDirectory()){
                            seqList.add(new Sequencia(sequencias.getAbsolutePath(), sequencias.getName(), sequencias.listFiles()));
                        }
                    }    
                    trafoList.add(new Trafo(trafos.getAbsolutePath(),trafos.getName(), seqList));
                    seqList = new ArrayList<>();
                }
            }
                        
            TreeItem raiz = new TreeItem("Trafos");
            raiz.setExpanded(true);
            for (Trafo t : trafoList) {
                TreeItem i = new TreeItem(t.getTrafo());        
                //i.setExpanded(true);
                
                for(Sequencia s : t.getSequencias()){
                    TreeItem ii = new TreeItem(s.getSequencia());
                    
                    for(File f:s.getImagens()){
                        
                        BufferedImage bi = ImageIO.read(f);
                        TreeItem i4 = new TreeItem("Resolução: "+bi.getWidth()+"x"+bi.getHeight());                       

                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        long fileSizeInKB = f.length() / (1024);
                        TreeItem i5 = new TreeItem("Tamanho: "+fileSizeInKB+" KB");                       
                        
                        TreeItem iii = new TreeItem(f.getAbsolutePath());                        
                        iii.getChildren().add(i4);
                        iii.getChildren().add(i5);
                        
                        iii.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent t) {
                                System.out.println("mouse clicked");
                            }
                        });                        
                        
                        ii.getChildren().add(iii);
                    }
                    
                    i.getChildren().add(ii);
                }
                
                raiz.getChildren().add(i);
            }
            dirTree.setRoot(raiz);
        }        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listarEstadosNoCombo();
        
        ObservableList<String> options = 
                FXCollections.observableArrayList("100","75","50","25");
        
        comboResizeImage.setItems(options);
    }    
    
}
