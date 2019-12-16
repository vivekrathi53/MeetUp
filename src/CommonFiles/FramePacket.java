package CommonFiles;

import java.io.Serializable;
import java.sql.Timestamp;

public class FramePacket implements Serializable
{
    byte[] frameData;
    Timestamp timestamp;

    public FramePacket(byte[] frameData, Timestamp timestamp) {
        this.frameData = frameData;
        this.timestamp = timestamp;
    }
}
