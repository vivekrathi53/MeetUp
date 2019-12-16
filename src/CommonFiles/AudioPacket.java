package CommonFiles;

import java.io.Serializable;
import java.sql.Timestamp;

public class AudioPacket implements Serializable
{
    byte[] audioData;
    Timestamp timestamp;

    public AudioPacket(byte[] audioData, Timestamp timestamp) {
        this.audioData = audioData;
        this.timestamp = timestamp;
    }
}
