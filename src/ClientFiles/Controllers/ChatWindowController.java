package ClientFiles.Controllers;

import ClientFiles.ClientReceiver;
import ClientFiles.VideoReceiver;
import ClientFiles.VideoSender;
import CommonFiles.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;

public class ChatWindowController
{
    public Connection connection;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Label currentUserStatus;
    @FXML
    public Button Send;
    @FXML
    public Button Logout;
    @FXML
    public Button refresh;
    @FXML
    public Button AddFriend;
    @FXML
    public TextArea textBox;
    @FXML
    public AnchorPane WindowPane;
    @FXML
    public VBox VerticalPane;
    @FXML
    public VBox AllChats;
    @FXML
    public Label currentUser;//USER WE ARE COMMUNICATING
    public Socket socket;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public Stage currentStage;
    public ClientReceiver reciever;
    public ArrayList<Message> chats;
    public ArrayList<String> friends;
    public String username;
    public ArrayList<Status> FriendStatus;
    public void addChat(String username)
    {
        Button name = new Button(username);
        AllChats.getChildren().add(name);//Add name of user to vbox
        name.setOnMouseClicked(e -> {
            seenMessagesof(username);
        });
    }

    public void seenMessagesof(String friend)
    {
        Timestamp seenTime=new Timestamp(System.currentTimeMillis());
        String q="UPDATE Local"+username+"Chats SET SeenTime = '"+seenTime.toString()+"' WHERE SeenTime = '2019-01-01 00:00:00' AND Sender ='"+friend+"'";
        try {
            PreparedStatement ps = connection.prepareStatement(q);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(int i=0;i<chats.size();i++)
        {
            if(((chats.get(i).getTo().equals(friend)||chats.get(i).getFrom().equals(friend))&&chats.get(i).getSeenTime()==null))// check for proper object
            {
                System.out.println("changed the value");
                chats.get(i).setSeenTime(seenTime);// update received time in message object
            }
        }
        try {
            display(friend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SystemMessage sm = new SystemMessage(friend,2,seenTime);
        try {
            oos.writeObject(sm);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void display(String username) throws IOException// Current Chats of user with username person displayed
    {
        int flag=0;
        currentUser.setText(username);
        VerticalPane.getChildren().clear();
        for(int i=0;i<chats.size();i++)
        {
            if(chats.get(i).getFrom().equals(username)||chats.get(i).getTo().equals(username))
            {
                addMessageToDisplay(chats.get(i));
            }
        }
        for(int i=0;i<FriendStatus.size();i++)
        {
            if(FriendStatus.get(i).getUser().equals(username))
            {
                flag=1;
                if(FriendStatus.get(i).getValid()==1)
                {
                    currentUserStatus.setText("Online");
                }
                else
                {
                    currentUserStatus.setText("Last Seen "+FriendStatus.get(i).getTime());
                }
                break;
            }
        }
        currentUser.setText(username);
    }

    public void fetchAllChats()// Fetch All chats of user from local database
    {
        if(chats!=null)
            chats.clear();
        String q="SELECT * FROM Local"+username+"Chats";
        PreparedStatement ps=null;
        try {
            ps = connection.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                Timestamp rt=rs.getTimestamp("ReceivedTime");
                Timestamp st=rs.getTimestamp("SeenTime");
                if(rt==null||rs.getTimestamp("ReceivedTime").toString().equals("2019-01-01 00:00:00"))
                    rt=null;
                if(st==null||rs.getTimestamp("SeenTime").toString().equals("2019-01-01 00:00:00"))
                    st=null;
                chats.add(new Message(rs.getString("Sender"),rs.getString("Receiver"),rs.getString("Message"),rs.getTimestamp("SentTime"),rt,st));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logout() throws IOException {

        Timestamp time=new Timestamp(System.currentTimeMillis());
        SystemMessage log=new SystemMessage(username,-1,time);
        oos.writeObject(log);
        oos.flush();
        System.exit(1);
       // ois.close();
      //  oos.close();
       // socket.close();
    }

    public void refresh()
    {
        scrollPane.setPrefWidth(WindowPane.getPrefWidth());
        scrollPane.setPrefHeight(WindowPane.getPrefHeight());
        textBox.clear();
        VerticalPane.getChildren().clear();
        AllChats.getChildren().clear();
        Send.setOnMouseClicked(e -> sendMessage());
        refresh.setOnMouseClicked(e -> {
                refresh();
        });
        Logout.setOnMouseClicked(e -> {
            try {
                logout();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        chats = new ArrayList<>();
        friends= new ArrayList<>();
        AddFriend.setOnMouseClicked(e-> addNewFriendChat());
        fetchAllChats();//To fetch all chat of user from Local_Database
        for(int i=0;i<chats.size();i++)
        {
            try
            {
                if(chats.get(i).getFrom().equals(currentUser.getText())||chats.get(i).getTo().equals(currentUser.getText()))
                    addMessageToDisplay(chats.get(i));
                if((!friends.contains(chats.get(i).getFrom()))&&(!chats.get(i).getFrom().equals(username)))
                {
                    friends.add(chats.get(i).getFrom());
                    addChat(chats.get(i).getFrom());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNewFriendChat()// to add new friend to chats list to talk to him
    {
        TextInputDialog dialog = new TextInputDialog("Give UserName of your friend");
        dialog.setTitle("UserName Input");
        dialog.setHeaderText("Username Of Friend");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
        {
            if(!result.get().equals("Give UserName of your friend"))
                addChat(result.get());
        }
    }

    public void sendMessage()
    {
        Message msg = new Message(username,currentUser.getText(),textBox.getText(),new Timestamp(System.currentTimeMillis()),null,null);
        textBox.clear();
        try {
            System.out.println(msg.getContent());
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        chats.add(msg);
        try {
            addMessageToDisplay(msg);
            insertIntoDatabase(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoDatabase(Message temp)
    {
        String q="INSERT INTO Local"+username+"Chats VALUES('"+(temp.getFrom())+"','"+(temp.getTo())+"','"+(temp.getContent())+"',"+(temp.getSentTime()==null?"null":("'"+temp.getSentTime()+"'"))+","+(temp.getReceivedTime()==null?"'2019-01-01 00:00:00'":("'"+temp.getReceivedTime()+"'"))+","+(temp.getSeenTime()==null?"'2019-01-01 00:00:00'":("'"+temp.getSeenTime()+"'"))+")";
        try {
            PreparedStatement ps = connection.prepareStatement(q);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMessageToDisplay(Message mesg) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML_Files/MessageDisplay.fxml"));
        VBox vbox = loader.load();
        MessageDisplayController mdc = loader.getController();
        mdc.MessageContent.setText(mesg.getContent());
        mdc.SenderName.setText(mesg.getFrom());
        mdc.TimeContent.setText((mesg.getSentTime()).toString());
        mdc.ReadReceipts.setOnMouseClicked(e-> showDetails(mesg));
        vbox.setPrefWidth(300);
        VerticalPane.getChildren().add(vbox);
    }

    private void showDetails(Message mesg)//To tell Receive time and Seen time
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time Receipts");
        alert.setHeaderText("Type       Time");
        String text="SentTime\t"+(mesg.getSentTime()).toString()+"\n";
        if(mesg.getReceivedTime()!=null)
            text+=("ReceivedTime\t"+(mesg.getReceivedTime()).toString())+"\n";
        else
           text+=("ReceivedTime\t"+"NOT RECEIVED\n");
        if(mesg.getSeenTime()!=null)
            text+=("SeenTime\t"+(mesg.getSeenTime()).toString())+"\n";
        else
            text+=("SeenTime\t"+"NOT SEEN\n");
        alert.setContentText(text);
        alert.show();
    }

    public void CallClicked() throws UnknownHostException {
        CallRequest cr = new CallRequest(currentUser.getText(),username,InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()),9890);
        try {
            oos.writeObject(cr);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartVideoChat(CallRequest finalObj)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../FXML_Files/VideoChatWindow.fxml"));
        AnchorPane anchorPane = null;
        try
        {
            anchorPane = (AnchorPane) fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        VideoChatWindowController controller = fxmlLoader.getController();
        controller.setTargetInetAddress(finalObj.getInetAddress());
        controller.setTargetPort(finalObj.getPort());
        controller.setCurrentStage(currentStage);
        currentStage.setTitle("Video Chat Window!");
        currentStage.setScene(new Scene(anchorPane));
        // set size of current window to maximum
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        currentStage.setWidth((primScreenBounds.getWidth()));
        currentStage.setHeight((primScreenBounds.getHeight()));
        anchorPane.setPrefWidth((primScreenBounds.getWidth()));
        anchorPane.setPrefHeight((primScreenBounds.getHeight()));
        // display window now
        currentStage.show();
        controller.StartVideoCall();
    }
}