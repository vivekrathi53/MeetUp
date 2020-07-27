package ClientFiles.Controllers;

import ClientFiles.LoginWindow;
import CommonFiles.Signupclass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Signupcontroller
{
    @FXML
    Button Submit;
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    TextField ServerIP;
    @FXML
    TextField PortNo;
    private Socket socket;
    LoginWindow lw;
    public Stage window;
    public void Sign() throws Exception
    {
        Stage window = (Stage) password.getScene().getWindow();
        socket = new Socket(ServerIP.getText(), Integer.parseInt(PortNo.getText()));
        System.out.println("Connected to server");
        Signupclass data = new Signupclass(username.getText(),password.getText());
        ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(data);
        oos.flush();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://127.0.0.1:3306/Chat_App";
        try {
            Connection connection = DriverManager.getConnection(url, "vivek", "password");
            String q="CREATE TABLE `Local"+username.getText()+"Chats` (\n" +
                    "  `Sender` varchar(15) NOT NULL,\n" +
                    "  `Receiver` varchar(15) NOT NULL,\n" +
                    "  `Message` text NOT NULL,\n" +
                    "  `SentTime` timestamp NULL DEFAULT '2019-01-01 00:00:00',\n" +
                    "  `ReceivedTime` timestamp NULL DEFAULT '2019-01-01 00:00:00',\n" +
                    "  `SeenTime` timestamp NULL DEFAULT '2019-01-01 00:00:00'\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
            PreparedStatement ps=connection.prepareStatement(q);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LoginWindow lw = new LoginWindow();
        lw.start(window);
    }
}
