package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map <String, AuthData> data = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        data.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return data.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        data.remove(authToken);
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        AuthData authData = data.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: invalid auth token");
        }
        return authData.username();
    }

    @Override
    public void clear() {
        data.clear();
    }
}
