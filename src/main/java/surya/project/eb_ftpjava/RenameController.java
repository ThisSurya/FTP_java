package surya.project.eb_ftpjava;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import surya.project.GlobalAuth.Global;

public class RenameController {
    @FXML
    private TextField renamefield;
    @FXML
    private Button submitbutton;

    private String ftppath;
    private void Initialize(String ftppath){
        this.ftppath = ftppath;
    }

    private String submitForm() throws Exception {
        String name = renamefield.getText();

        if(name.isEmpty()){
            throw new Exception("Rename field tidak boleh kosong!");
        }else{
            return name;
        }
    }
}
