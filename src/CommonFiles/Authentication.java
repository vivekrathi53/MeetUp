package CommonFiles;

import java.io.Serializable;

public class Authentication implements Serializable
{
    private boolean auth;
    private String Error;

    public Authentication(boolean auth, String error) {
        this.setAuth(auth);
        setError(error);
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }
}
