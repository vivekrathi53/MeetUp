package CommonFiles;

import java.io.Serializable;

public class CallRequest implements Serializable
{
    private String targetUser;// the username of person to whom we are calling
    private String callerUser;// the username of person who is calling

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getCallerUser() {
        return callerUser;
    }

    public void setCallerUser(String callerUser) {
        this.callerUser = callerUser;
    }

    public CallRequest(String targetUser, String callerUser) {
        this.targetUser = targetUser;
        this.callerUser = callerUser;
    }
}
