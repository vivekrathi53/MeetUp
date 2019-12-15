package CommonFiles;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class CallRequestRespond implements Serializable
{
    private InetAddress userInetAddress;
    private int dedicatedPort;
    private String error;
    private boolean accept;

    public CallRequestRespond(InetAddress userInetAddress, int dedicatedPort, String error, boolean accept) {
        this.userInetAddress = userInetAddress;
        this.dedicatedPort = dedicatedPort;
        this.error = error;
        this.accept = accept;
    }

    public InetAddress getUserInetAddress() {
        return userInetAddress;
    }

    public void setUserInetAddress(InetAddress userInetAddress) {
        this.userInetAddress = userInetAddress;
    }

    public int getDedicatedPort() {
        return dedicatedPort;
    }

    public void setDedicatedPort(int dedicatedPort) {
        this.dedicatedPort = dedicatedPort;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isAccepted() {
        return accept;
    }

    public void setAcceptance(boolean accept) {
        this.accept = accept;
    }
}
