package ServerFiles;

import CommonFiles.*;
import javafx.util.Pair;
import sun.net.ConnectionResetException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.*;

public class ClientHandler implements Runnable,Serializable
{
    Socket sc;
    Server server;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    MessageManager msh;
    String username, password;
    Connection connection;
    int flag=0;

    public ClientHandler(Socket so, Server ss, MessageManager ms, ObjectOutputStream oos, ObjectInputStream ois, Connection connection)
    {
        sc = so;
        server=ss;
        msh=ms;
        this.oos=oos;
        this.ois=ois;
        this.connection=connection;
    }

    public ObjectOutputStream find(String sender)
    {
        int i=0;
        for(i=0;i<server.activelist.size();i++)
        {
            if (server.activelist.get(i).getKey().equals(sender))
            {
                return server.activeUserStreams.get(i).getValue();
            }
        }
        return null;
    }

    public void logout(Timestamp time)
    {
        for(int i=0;i<server.activelist.size();i++) {
            if (server.activelist.get(i).getKey().equals(username)) {
                server.activelist.remove(i);//To remove user from current list
                server.activeUserStreams.remove(i);
                server.handlers.remove(i);
                break;
            }
        }
        for(int i=0;i<server.handlers.size();i++)
        {
            server.handlers.get(i).broadcast(username,time,-2);
        }
        String query = "UPDATE UserTable SET LastSeen = '"+time+"' WHERE UserName='" + (username) + "'";
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            preStat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String user,Timestamp time,int valid)
    {
        SystemMessage sm = new SystemMessage(user,valid,time);
        try {
            oos.writeObject(sm);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void callAlert(CallRequest cr)
    {
        try {
            oos.writeObject(cr);
            oos.flush();
            flag=1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void callRespond(CallRequestRespond obj) {
        try {
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        Timestamp time = null;
        Object obj = null;
        try
        {
            obj = ois.readObject();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        if (obj instanceof user)
        {
            user temp = (user) obj;
            username = temp.getUsername();
            password = temp.getPassword();
            try {
                if (authenticate()) {
                    msh.oos = oos;
                    msh.remove(username);//To empty table of user
                    System.out.println("Fine");
                    while (true) {
                        try {
                            obj = ois.readObject();
                        } catch (ConnectionResetException e) {
                            e.printStackTrace();
                            break;
                        }
                        if (obj instanceof Message) {
                            Message ms = (Message) obj;
                            String receiver = ms.getTo();
                            System.out.println("----------------" + receiver);
                            ObjectOutputStream oosTo = find(receiver);
                            System.out.println(oosTo);
                            if (oosTo != null)// IF USER IS ONLINE
                            {
                                System.out.println("User is Active");
                                ms.setReceivedTime(ms.getSentTime());
                                oosTo.writeObject(ms);
                                oosTo.flush();
                                SystemMessage sm = new SystemMessage(ms.getTo(), 1, ms.getSentTime());
                                oos.writeObject(sm);
                                oos.flush();
                            } else// IF USER IS OFFLINE
                            {
                                msh.insert(receiver, ms);
                            }
                        }
                        else if (obj instanceof SystemMessage) {
                            SystemMessage sm = (SystemMessage) obj;
                            if(sm.getValid()==-1) {// SystemMessage object containing information of logout
                                time=sm.getTime();
                                break;
                            }
                            String receiver = sm.getSender();
                            System.out.println("----------------" + receiver);
                            ObjectOutputStream oosTo = find(receiver);
                            System.out.println(oosTo);
                            if (oosTo != null)// IF USER IS ONLINE
                            {
                                System.out.println("User is Active");
                                sm.setSender(username);//for person who receives seen receipt will have sender as person who sends this to him
                                oosTo.writeObject(sm);
                                oosTo.flush();
                            } else// IF USER IS OFFLINE
                            {
                                sm.setSender(username);//for person who receives seen receipt will have sender as person who sends this to him
                                msh.insert(receiver, sm);
                            }

                        }
                        else if((obj instanceof CallRequestRespond))
                        {
                            System.out.println("Call Respond from "+((CallRequestRespond) obj).getTargetUser()+" to "+((CallRequestRespond) obj).getCallerUser());
                            Pair<ClientHandler,Thread> cht = server.getHandler(((CallRequestRespond) obj).getCallerUser());
                            cht.getKey().callRespond((CallRequestRespond)obj);
                        }
                        else if(obj instanceof CallRequest)// Call Request received from user
                        {
                            // generate Alert Request to user if he is online
                            System.out.println("Call Request from "+((CallRequest) obj).getCallerUser()+" to "+((CallRequest) obj).getTargetUser());
                            Pair<ClientHandler,Thread> cht = server.getHandler(((CallRequest) obj).getTargetUser());
                            if(cht.getKey()==null)// Target user is not online
                            {
                                oos.writeObject(new CallRequestRespond(InetAddress.getLocalHost(),0000,"User Not Online",false,((CallRequest) obj).getCallerUser(),((CallRequest) obj).getTargetUser()));
                                oos.flush();
                            }
                            else {
                                cht.getKey().callAlert((CallRequest) obj);
                            }
                        }

                        }
                    }
                System.out.println("Logging Out");
                    logout(time);
                    this.oos.close();
                    this.ois.close();
                    this.sc.close();
                    return;
                } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Signupclass temp=(Signupclass)obj;
            System.out.println("USER IS SIGN UP");
            try {
                msh.insertuser(temp);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public boolean authenticate() throws ClassNotFoundException, SQLException //To authentication
    {
        String query = "SELECT Password FROM UserTable WHERE UserName='" + (username) + "'";
        PreparedStatement preStat = connection.prepareStatement(query);
        ResultSet rs = preStat.executeQuery(query);
        if (rs.next())
        {
            String CheckPassword = rs.getString("Password");
            if (CheckPassword.equals(password))
            {
                Authentication auth = new Authentication(true,"Successful");
                try {
                    oos.writeObject(auth);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server.activelist.add(new Pair<String,Socket>(username,sc));
                server.activeUserStreams.add(new Pair<>(ois,oos));
                server.handlers.add(this);
                String query2 = "SELECT * FROM UserTable ";
                PreparedStatement ps = connection.prepareStatement(query2);
                ResultSet rs2 = ps.executeQuery();
                while(rs2.next())
                {
                    try {
                        oos.writeObject(new SystemMessage(rs2.getString("Username"),-2,rs2.getTimestamp("LastSeen")));
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                for(int i=0;i<server.handlers.size();i++)
                {
                    if(!server.handlers.get(i).equals(this))
                    {
                        server.handlers.get(i).broadcast(username,null,-3);
                    }
                }
                for(int i=0;i<server.activelist.size();i++)
                {
                    if(!server.activelist.get(i).getKey().equals(username))
                    {
                        try
                        {
                            oos.writeObject(new SystemMessage(server.activelist.get(i).getKey(),-3,null));
                            oos.flush();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
            else
            {
                Authentication auth = new Authentication(false,"Invalid Login Credientials");
                try {
                    oos.writeObject(auth);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        else
            return false;
    }
}