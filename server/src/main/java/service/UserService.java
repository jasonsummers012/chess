package service;

import dataaccess.AuthDAO;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (userDAO.getUser(request.username()) != null) {
            throw new DataAccessException("Error: username already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);

        return new RegisterResult(request.username(), AuthDAO.generateToken());
    }
}
