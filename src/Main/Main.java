package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Controllers.Login;
import Data.DBConnection;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Login.class.getResource("/Views/Login.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Scheduler Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        DBConnection.connect();
        launch(args);
        DBConnection.disconnect();
    }
}
