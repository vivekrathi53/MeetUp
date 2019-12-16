package ClientFiles.Controllers;

import ClientFiles.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class VideoChatWindowController
{
    @FXML
    public Button StopCall;
    @FXML
    public ImageView MyVideoView;
    @FXML
    public ImageView CallerVideoView;
    @FXML
    public Label loadingLabel;
    private InetAddress TargetInetAddress;
    private int TargetPort;

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    private Stage currentStage;
    public void StopCallClicked()
    {

    }
    public void StartVideoCall()
    {
        VideoSender vs=null;
        VideoReceiver vr=null;
        BufferedPlayer bp=null;
        AudioReceiver ar = null;
        AudioSender as = null;
        try {
            bp = new BufferedPlayer(CallerVideoView,loadingLabel,1000);
            vs = new VideoSender(TargetInetAddress,TargetPort,MyVideoView);
            vr = new VideoReceiver(CallerVideoView,bp);
            ar = new AudioReceiver(bp);
            as = new AudioSender(TargetInetAddress,TargetPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread videoSenderThread = new Thread(vs);
        Thread videoreceiverThread = new Thread(vr);
        Thread audioSenderThread = new Thread(as);
        Thread audioReceiverThread = new Thread(ar);
        Thread bufferedPlayerThread = new Thread(bp);
        bufferedPlayerThread.start();
        videoSenderThread.start();
        audioSenderThread.start();
        videoreceiverThread.start();
        audioReceiverThread.start();
    }

    public InetAddress getTargetInetAddress() {
        return TargetInetAddress;
    }

    public void setTargetInetAddress(InetAddress targetInetAddress) {
        TargetInetAddress = targetInetAddress;
    }

    public int getTargetPort() {
        return TargetPort;
    }

    public void setTargetPort(int targetPort) {
        TargetPort = targetPort;
    }
}
