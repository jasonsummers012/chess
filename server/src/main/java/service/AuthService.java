package service;

import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import handler.request.LogoutRequest;
import handler.result.LogoutResult;
import model.AuthData;
import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
    public String generateAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, authToken);
        authDAO.createAuth(auth);
        return authToken;
    }

    public String checkValidAuthToken(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return authDAO.getAuth(authToken).username();
    }

    public LogoutResult logout(String authToken) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (authToken == null) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
        return new LogoutResult();
    }

    public void clear() throws DataAccessException{
        authDAO.clear();
    }
}
