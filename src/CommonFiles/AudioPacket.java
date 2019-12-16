package CommonFiles;

import java.io.Serializable;
import java.sql.Timestamp;

public class AudioPacket implements Serializable
{
    private byte[] audioData;
    private Timestamp timestamp;

    public AudioPacket(byte[] audioData, Timestamp timestamp) {
        this.setAudioData(audioData);
        this.setTimestamp(timestamp);
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
