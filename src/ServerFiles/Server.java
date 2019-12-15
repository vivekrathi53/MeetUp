package ServerFiles;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class Server
{

    public  ArrayList<Pair<String, Socket>> activelist;
    public ArrayList<Pair<ObjectInputStream, ObjectOutputStream>> activeUserStreams;
    public MessageManager msh;
    public ArrayList<ClientHandler> handlers;

    public static void main(String[] args) throws Exception
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://127.0.0.1:3306/Chat_App";
        Connection connection = DriverManager.getConnection(url,"vivek","password");
        Server server=new Server();
        server.handlers=new ArrayList<>();
        server.activelist=new ArrayList<Pair<String, Socket>>();
        server.activeUserStreams=new ArrayList<>();
        server.msh = new MessageManager(server);
        server.msh.connection=connection;
        try
        {
            ServerSocket ss=new ServerSocket(8188);
            while(true)
            {
                Socket sc = ss.accept();//request is received
                ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
                ClientHandler auth=new ClientHandler(sc,server,server.msh,oos,ois,connection);
                Thread t=new Thread(auth);

                auth.oos=oos;
                auth.ois=ois;
                t.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
