package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import handler.request.LogoutRequest;
import handler.result.LogoutResult;
import model.AuthData;
import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
    public String generateAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, authToken);
        authDAO.createAuth(auth);
        return authToken;
    }

    public boolean checkValidAuthToken(String authToken) {
        return (authToken != null && authDAO.getAuth(authToken) != null);
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: auth token doesn't exist");
        }

        authDAO.deleteAuth(authToken);
        return new LogoutResult();
    }
}
