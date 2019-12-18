package ClientFiles;

import CommonFiles.FramePacket;
import javafx.scene.image.ImageView;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class VideoReceiver implements Runnable
{
    public VideoReceiver(ImageView callerVideoView, BufferedPlayer bp) {
        this.CallerVideoView = callerVideoView;
        this.bp = bp;
    }

    private ImageView CallerVideoView;
    private BufferedPlayer bp;
    int stopper;
    @Override
    public void run() throws NullPointerException
    {
        int flag=0;
        try
        {
            DatagramSocket ds = new DatagramSocket(9890);
            while(stopper==0)
            {
                //Image image = SwingFXUtils.toFXImage(webcam.getImage(), null);
                //  imageView.setImage(image);
                byte[] data;
                byte[] data2 = new byte[200000];
                DatagramPacket dp = new DatagramPacket(data2,data2.length);
                //System.out.printf("Receiving");
                ds.receive(dp);
                //System.out.printf("Received");
                ByteArrayInputStream bais2 = new ByteArrayInputStream(data2);
                ObjectInputStream ois = new ObjectInputStream(bais2);
                FramePacket fp = (FramePacket) ois.readObject();
                data = fp.getFrameData();
                getBp().addFramePacket(fp);
                //System.out.println(fp.counter);

            }

        }
        catch(Exception e)
        {
            System.out.println("Video Receiver Error");
            e.printStackTrace();
        }
    }

    public ImageView getCallerVideoView() {
        return CallerVideoView;
    }

    public void setCallerVideoView(ImageView callerVideoView) {
        CallerVideoView = callerVideoView;
    }

    public BufferedPlayer getBp() {
        return bp;
    }

    public void setBp(BufferedPlayer bp) {
        this.bp = bp;
    }
}
