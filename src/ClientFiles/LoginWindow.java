package ClientFiles;

import ClientFiles.Controllers.LoginWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginWindow extends Application {
    Stage window;
    FXMLLoader loader;
    LoginWindowController controller;
    AnchorPane MainDisplay;
    @Override

    public void start(Stage primaryStage) throws Exception
    {
        loader = new FXMLLoader(getClass().getResource("../ClientFiles/FXML_Files/LoginWindowDesign.fxml"));
        MainDisplay  = loader.load();
        controller = loader.getController();
        primaryStage.setScene(new Scene(MainDisplay));
        controller.window=primaryStage;
        window = primaryStage;
        window.setTitle("Log in Page");
        window.show();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
