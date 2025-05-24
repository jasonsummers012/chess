package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    String getUsername(String authToken) throws DataAccessException;
    void clear();
}
