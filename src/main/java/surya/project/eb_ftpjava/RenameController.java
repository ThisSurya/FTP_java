package surya.project.eb_ftpjava;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import surya.project.GlobalAuth.Global;

import java.io.IOException;

public class RenameController {
    @FXML
    private TextField renamefield;
    @FXML
    private Label exceptionLabel;
    @FXML
    private Button submitbutton;

    private static String name;
    private String ftppath;
    private Stage stage;
    public void Initialize(Stage stage, String ftppath){
        this.stage = stage;
        this.ftppath = ftppath;

        submitbutton.setOnAction((event) -> {submitForm();});
    }

    private void submitForm(){
        exceptionLabel.setVisible(false);
        this.name = renamefield.getText();

        try{
            if(name.isEmpty()){
                exceptionLabel.setVisible(true);
                exceptionLabel.setText("harap untuk mengisinya!");
            }else{
                Global.globalClient.renameFolder(this.ftppath, name);
                stage.close();
            }
        }catch(Exception e){
            System.out.println(e);
        }

    }

    public String getRename(){
        return this.name;
    }
}
