package ClientFiles;

import ClientFiles.Controllers.Signupcontroller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Signup extends Application
{
    Stage window;
    Signupcontroller controller;
    FXMLLoader loader;
    AnchorPane Display;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        loader = new FXMLLoader(getClass().getResource("../ClientFiles/FXML_Files/Signup.fxml"));
        Display = loader.load();
        controller=loader.getController();
        controller.window=primaryStage;
        window=primaryStage;
        window.setScene(new Scene(Display));
        window.setTitle("SignUp Window");
        window.show();
    }
}
