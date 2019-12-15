package ClientFiles.Controllers;

import ClientFiles.Chat_App_Window;
import ClientFiles.Signup;
import CommonFiles.Authentication;
import CommonFiles.user;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginWindowController
{
    public TextField ServerIP;
    public TextField PortNo;
    Socket socket;
    @FXML
    public TextField name;
    @FXML
    public TextField pass;
    @FXML
    public Button customer;
    @FXML
    public Button retailer;
    @FXML
    public Button userLogin;
    int type;
    public Stage window;
    public void signUp()
    {
        try {
            Signup signup = new Signup();
            signup.start(window);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Login() throws IOException
    {
        window = (Stage)name.getScene().getWindow();
        socket = new Socket(ServerIP.getText(),Integer.parseInt(PortNo.getText()));
        System.out.println("Connected to server");
        user data = new user(name.getText(),pass.getText());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(data);
        oos.flush();
        System.out.println("Waiting for Approval!");
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        try
        {
            System.out.println("reading letter");
            Object temp=ois.readObject();
            Authentication a = (Authentication) temp;
            if(a.isAuth())
            {
                Chat_App_Window ChattingWindow = new Chat_App_Window();
                ChattingWindow.setOos(oos);
                ChattingWindow.setOis(ois);
                ChattingWindow.setSocket(socket);
                ChattingWindow.setUsername(name.getText());
                ChattingWindow.start(window);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Authentication Failed");
                alert.setHeaderText("Please Check Your Login Credentials");
                alert.setContentText(a.getError());
                alert.show();
            }
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Class Not Found");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
