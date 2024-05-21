package surya.project.eb_ftpjava;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import surya.project.ftpservice.AuthService;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected Button submitButt;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected TextField username = new TextField();
    @FXML
    protected TextField password = new TextField();
    @FXML
    protected TextField port = new TextField();
    @FXML
    protected TextField hostname = new TextField();
    @FXML
    protected Label checklabel = new Label();

    @FXML
    protected void submitForm() throws Exception {

        AuthService ftp = new AuthService();

        ftp.loginFtp(hostname.getText(),
                Integer.parseInt(port.getText()), username.getText(), password.getText());

        boolean result = ftp.isConnected();
        checklabel.setVisible(true);
        if(result){
            checklabel.setText("Login Successful");
        }else {
            checklabel.setText("Login Failed");
        }
    }
}