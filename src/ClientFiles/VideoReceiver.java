package ClientFiles;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static java.lang.Thread.sleep;

public class VideoReceiver implements Runnable
{
    public ImageView CallerVideoView;
    @Override
    public void run()
    {

    }
}
