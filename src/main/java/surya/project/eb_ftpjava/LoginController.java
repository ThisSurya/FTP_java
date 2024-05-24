package surya.project.eb_ftpjava;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import surya.project.GlobalAuth.Global;
import surya.project.ftpservice.AuthService;

public class LoginController {
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

    private HelloApplication app;

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    @FXML
    protected void submitForm() throws Exception {

        AuthService ftp = new AuthService();

        ftp.loginFtp(hostname.getText(),
                Integer.parseInt(port.getText()), username.getText(), password.getText());

        boolean result = ftp.isConnected();
        if(result){
            Global.globalClient = ftp;
            app.showDashboard();
        }else {
            checklabel.setText("Login Failed");
        }
    }
}