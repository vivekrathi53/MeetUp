package ClientFiles;

import ClientFiles.Controllers.ChatWindowController;
import CommonFiles.Errors;
import CommonFiles.Message;
import CommonFiles.Status;
import CommonFiles.SystemMessage;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ClientReceiver implements Runnable
{
    public ChatWindowController controller;
    Connection connection;
    ObjectInputStream ois;
    String username;
    @Override
    public void run()
    {
        while(true)
        {
            Object obj=null;
            try {
                obj = ois.readObject();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
            if(obj instanceof Message)
            {
                Message temp = (Message)obj;
               temp.setReceivedTime(new Timestamp(System.currentTimeMillis()));//Receiver time is current time
                String q="INSERT INTO Local"+username+"Chats VALUES('"+(temp.getFrom())+"','"+(temp.getTo())+"','"+(temp.getContent())+"',"+(temp.getSentTime()==null?"null":("'"+temp.getSentTime()+"'"))+","+(temp.getReceivedTime()==null?"'2019-01-01 00:00:00'":("'"+temp.getReceivedTime()+"'"))+","+(temp.getSeenTime()==null?"'2019-01-01 00:00:00'":("'"+temp.getSeenTime()+"'"))+")";
                try {
                    PreparedStatement ps = connection.prepareStatement(q);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(temp.getFrom().equals(controller.currentUser.getText()))
                {

                        Platform.runLater(new Runnable()//To perform UI work from different Thread
                        {
                            @Override
                            public void run() {
                                System.out.println("Changing UI");
                                try
                                {
                                    controller.addMessageToDisplay(temp);
                                    controller.seenMessagesof(temp.getFrom());
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                }

                controller.chats.add(temp);
                if(!controller.friends.contains(temp.getFrom()))
                {
                    Platform.runLater(new Runnable()//To perform UI work from different Thread
                    {
                        @Override
                        public void run() {
                            System.out.println(temp.getFrom());
                            controller.addChat(temp.getFrom());
                        }
                    });

                }

            }
            else if(obj instanceof SystemMessage)
            {
                SystemMessage temp = (SystemMessage) obj;
                System.out.println(temp.getValid());
                if(temp.getValid()==1)// recieved Time LocalVivekTable for example tables are named with username in between
                {
                    String q="UPDATE Local"+username+"Chats SET ReceivedTime = '"+temp.getTime()+"' WHERE ReceivedTime = '2019-01-01 00:00:00' AND Receiver ='"+temp.getSender()+"'";
                    try {
                        PreparedStatement ps = connection.prepareStatement(q);
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                else if(temp.getValid()==2)// seen Time
                {
                    String q="UPDATE Local"+username+"Chats SET SeenTime = '"+temp.getTime()+"' WHERE    SeenTime = '2019-01-01 00:00:00' AND Receiver='"+temp.getSender()+"'";
                    try {
                        PreparedStatement ps = connection.prepareStatement(q);
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if(temp.getValid()==-2)// someone logged out
                {
                    int flag=0;
                    for(int i=0;i<controller.FriendStatus.size();i++)
                    {
                        if(controller.FriendStatus.get(i).getUser().equals(temp.getSender()))
                        {
                            flag=1;
                            controller.FriendStatus.get(i).setValid(0);
                            controller.FriendStatus.get(i).setTime(temp.getTime());
                            break;
                        }
                    }
                    if(flag==0)
                    {
                        controller.FriendStatus.add(new Status(temp.getSender(),temp.getTime(),0));
                    }
                    if(controller.currentUser.getText().equals(temp.getSender()))
                    {
                        Platform.runLater(new Runnable()//To perform UI work from different Thread
                        {
                            @Override
                            public void run() {
                                System.out.println("Changing Status Of CurrentUSer");
                                controller.currentUserStatus.setText("Last Seen "+temp.getTime().toString());
                            }
                        });
                    }
                }
                else if(temp.getValid()==-3)// someone logged in
                {
                    int flag=0;
                    for(int i=0;i<controller.FriendStatus.size();i++)
                    {
                        if(controller.FriendStatus.get(i).getUser().equals(temp.getSender()))
                        {
                            flag=1;
                            controller.FriendStatus.get(i).setValid(1);
                            break;
                        }
                    }
                    if(flag==0)
                    {
                        controller.FriendStatus.add(new Status(temp.getSender(),temp.getTime(),1));
                    }
                    if(controller.currentUser.getText().equals(temp.getSender()))
                    {
                        Platform.runLater(new Runnable()//To perform UI work from different Thread
                        {
                            @Override
                            public void run() {
                                controller.currentUserStatus.setText("Online");
                            }
                        });
                    }
                }
                else if(temp.getValid()==1||temp.getValid()==2)
                {
                    Platform.runLater(new Runnable()//To perform UI work from different Thread
                    {
                        @Override
                        public void run() {
                            System.out.println("Received receiving time refreshing");
                            for(int i=0;i<controller.chats.size();i++)
                            {
                                //System.out.println(controller.chats.get(i).getTo());
                                //System.out.println(temp.getSender());
                                //System.out.println(controller.chats.get(i).getFrom());
                                //System.out.println(controller.chats.get(i).getReceivedTime());
                                if(temp.getValid()==1&&((controller.chats.get(i).getTo().equals(temp.getSender())||controller.chats.get(i).getFrom().equals(temp.getSender()))&&controller.chats.get(i).getReceivedTime()==null))// check for proper object
                                {
                                    System.out.println("changed the value");
                                    controller.chats.get(i).setReceivedTime(temp.getTime());// update received time in message object
                                }
                                else if(temp.getValid()==2&&((controller.chats.get(i).getTo().equals(temp.getSender())||controller.chats.get(i).getFrom().equals(temp.getSender()))&&controller.chats.get(i).getSeenTime()==null))// check for proper object
                                {
                                    System.out.println("changed the value");
                                    controller.chats.get(i).setSeenTime(temp.getTime());// update received time in message object
                                }
                            }
                            try {
                                System.out.println("Updating Display");
                                controller.display(temp.getSender());// update in UI
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


            }
            else if(obj instanceof Errors)//When is user is invalid
            {
                Errors temp=(Errors) obj;
                System.out.println(temp.getErrormessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Some Error Occured");
                alert.setHeaderText("Error");
                alert.setContentText(temp.getErrormessage());
                alert.show();
            }
        }

    }
}
