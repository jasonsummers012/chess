package service;

import dataaccess.AuthDAO;
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
}
