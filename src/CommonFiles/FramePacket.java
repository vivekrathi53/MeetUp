package CommonFiles;

import java.io.Serializable;
import java.sql.Timestamp;

public class FramePacket implements Serializable
{
    private byte[] frameData;
    private Timestamp timestamp;

    public FramePacket(byte[] frameData, Timestamp timestamp) {
        this.setFrameData(frameData);
        this.setTimestamp(timestamp);
    }

    public byte[] getFrameData() {
        return frameData;
    }

    public void setFrameData(byte[] frameData) {
        this.frameData = frameData;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
