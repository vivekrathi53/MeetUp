package CommonFiles;

import java.io.Serializable;

public class Errors implements Serializable
{
    private String errormessage;

    public Errors(String errormessage) {
        this.setErrormessage(errormessage);
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }
}
