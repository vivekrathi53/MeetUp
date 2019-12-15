package CommonFiles;

import java.io.Serializable;
import java.sql.Timestamp;


public class SystemMessage implements Serializable
{
    private String sender;
    private int valid;
    private Timestamp time;



    public SystemMessage(String sender, int valid, Timestamp time) {
        this.setSender(sender);
        this.setValid(valid);
        this.setTime(time);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
