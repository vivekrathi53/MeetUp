package ClientFiles;

import CommonFiles.AudioPacket;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.sql.Timestamp;
import java.util.Date;

public class AudioSender implements Runnable
{
    @Override
    public void run() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        TargetDataLine microphone;
        SourceDataLine speakers;
        try {
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);


            int numBytesRead;
            int CHUNK_SIZE = 10000;
            byte[] data = new byte[10300];
            microphone.start();
            //int bytesRead = 0;
            //DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            //speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            //speakers.open(format);
            //  speakers.start();
            long i=0;// adjust condition of loop for extent of microphone use
            while (i==0) {
                // i++;
                microphone.read(data, 0, CHUNK_SIZE);
                //bytesRead += numBytesRead;
                // write the mic data to a stream for use later
                //out.write(data, 0, numBytesRead);

                AudioPacket ap = new AudioPacket(data,new Timestamp(new Date().getTime()));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(ap);
                byte[] data2 = baos.toByteArray();
                DatagramSocket ds = new DatagramSocket();
                DatagramPacket dp = new DatagramPacket(data2,data2.length, InetAddress.getLocalHost(),8189);
                ///System.out.println(data2.length);
                ds.send(dp);
                ds.close();
                // write mic data to stream for immediate playback
                //speakers.write(data, 0, numBytesRead);
            }
            //speakers.drain();
            //speakers.close();
            microphone.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
