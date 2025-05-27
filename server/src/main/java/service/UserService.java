package service;

import dataaccess.*;
import handler.request.*;
import handler.result.*;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.authService = authService;
    }

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        UserData user = new UserData(request.username(), hashPassword(request.password()), request.email());
        userDAO.createUser(user);

        String authToken = authService.generateAuthToken(request.username());
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData user = userDAO.getUser(request.username());
        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (!verifyUser(request.username(), request.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String authToken = authService.generateAuthToken(request.username());
        return new LoginResult(request.username(), authToken);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    boolean verifyUser(String username, String password) throws DataAccessException {
        var hashedPassword = userDAO.getUser(username).password();
        return BCrypt.checkpw(password, hashedPassword);
    }
}
