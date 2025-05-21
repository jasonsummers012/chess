package service;

import dataaccess.AlreadyExistsException;
import handler.request.*;
import handler.result.*;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.authService = authService;
    }

    public RegisterResult register(RegisterRequest request) throws AlreadyExistsException {
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyExistsException("Error: username already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);

        String authToken = authService.generateAuthToken(request.username());
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = userDAO.getUser(request.username());
        if (user == null) {
            throw new DataAccessException("Error: user doesn't exist");
        }

        if (!request.password().equals(user.password())) {
            throw new DataAccessException("Error: incorrect password");
        }

        String authToken = authService.generateAuthToken(request.username());
        return new LoginResult(request.username(), authToken);
    }
}
