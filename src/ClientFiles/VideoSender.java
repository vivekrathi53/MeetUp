package ClientFiles;

import CommonFiles.FramePacket;
import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.sql.Timestamp;
import java.util.Date;

import static java.lang.Thread.sleep;

public class VideoSender implements Runnable
{
    Webcam webcam;

    public VideoSender(InetAddress targetAddress, int port) {
        this.webcam = Webcam.getDefault();
        this.targetAddress = targetAddress;
        Port = port;
    }

    InetAddress targetAddress;
    int Port;
    @Override
    public void run(){
        while(true)
        {
            //   Image image = SwingFXUtils.toFXImage(webcam.getImage(), null);
            //  imageView.setImage(image);
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket =new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream(200000);
            try {
                ImageIO.write(webcam.getImage(),"jpg",baos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                byte[] data = baos.toByteArray();
                FramePacket fp = new FramePacket(data,new Timestamp(new Date().getTime()));
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos2);
                oos.writeObject(fp);
                byte[] data2 = baos2.toByteArray();
                DatagramPacket dp = new DatagramPacket(data2,data2.length, targetAddress,Port);
                //System.out.println(data.length);
                datagramSocket.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
