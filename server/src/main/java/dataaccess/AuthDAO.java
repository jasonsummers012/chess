package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private final Map <String, AuthData> data = new HashMap<>();

    public void createAuth(AuthData auth) {
        data.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) {
        return data.get(authToken);
    }

    public void deleteAuth(String authToken) {
        data.remove(authToken);
    }

    public String getUsername(String authToken) {
        return data.get(authToken).username();
    }

    public void clear() {
        data.clear();
    }
}
