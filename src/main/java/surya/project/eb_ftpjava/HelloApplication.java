package surya.project.eb_ftpjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import surya.project.ftpservice.AuthService;

import java.io.IOException;

public class HelloApplication extends Application {

    private Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        showLogin();
    }

    private void showLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        LoginController loginController = fxmlLoader.getController();
        loginController.setApp(this);
        stage.setTitle("Login!");
        stage.setScene(scene);
        stage.show();
    }

    public void showDashboard() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        DashboardController dashboardController = fxmlLoader.getController();
        dashboardController.Initialize(this);
        stage.setTitle("Dashboard!");
        stage.setScene(scene);
        stage.show();
    }

    public void showRename(){
        
    }

    public static void main(String[] args) {
        launch();
    }
}