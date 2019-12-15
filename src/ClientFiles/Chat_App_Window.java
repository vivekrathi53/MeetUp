package ClientFiles;

import ClientFiles.Controllers.ChatWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Chat_App_Window extends Application {

    private Stage window;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private FXMLLoader loader;
    private AnchorPane DisplayPane;
    private ChatWindowController controller;
    private ClientReceiver reciever;
    private Connection connection;
    private String username;

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException
    {

        setLoader(new FXMLLoader(getClass().getResource("../ClientFiles/FXML_Files/Chat_Window.fxml")));
        try
        {
            setDisplayPane((AnchorPane) getLoader().load());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        setController(getLoader().getController());
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://127.0.0.1:3306/Chat_App";
        try
        {
            setConnection(DriverManager.getConnection(url, "vivek", "password"));
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        setReciever(new ClientReceiver());
        getReciever().ois= getOis();//inputstream of client
        getReciever().oos=getOos();
        getReciever().controller= getController();
        getReciever().connection= getConnection();
        getReciever().username= getUsername();
        Thread t = new Thread(getReciever());
        setWindow(primaryStage);
        getController().socket = getSocket();//ChatWindowController getting client object
        getController().ois = getOis();
        getController().oos = getOos();
        getController().currentStage = primaryStage;
        getController().connection= getConnection();
        getController().username= getUsername();
        getController().FriendStatus=new ArrayList<>();
        getController().refresh();
        getController().WindowPane= getDisplayPane();
        t.start();
        primaryStage.setTitle("Chat Window!");
        primaryStage.setScene(new Scene(getDisplayPane()));
        // set size of current window to maximum
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth((primScreenBounds.getWidth()));
        primaryStage.setHeight((primScreenBounds.getHeight()));
        getDisplayPane().setPrefWidth((primScreenBounds.getWidth()));
        getDisplayPane().setPrefHeight((primScreenBounds.getHeight()));
        // display window now
        primaryStage.show();
    }

    public Stage getWindow() {
        return window;
    }

    public void setWindow(Stage window) {
        this.window = window;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public FXMLLoader getLoader() {
        return loader;
    }

    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }

    public AnchorPane getDisplayPane() {
        return DisplayPane;
    }

    public void setDisplayPane(AnchorPane displayPane) {
        DisplayPane = displayPane;
    }

    public ChatWindowController getController() {
        return controller;
    }

    public void setController(ChatWindowController controller) {
        this.controller = controller;
    }

    public ClientReceiver getReciever() {
        return reciever;
    }

    public void setReciever(ClientReceiver reciever) {
        this.reciever = reciever;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
