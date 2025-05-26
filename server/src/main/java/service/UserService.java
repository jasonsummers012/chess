package service;

import dataaccess.*;
import handler.request.*;
import handler.result.*;
import model.UserData;

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

        UserData user = new UserData(request.username(), request.password(), request.email());
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

        if (!request.password().equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        String authToken = authService.generateAuthToken(request.username());
        return new LoginResult(request.username(), authToken);
    }

    public void clear() {
        userDAO.clear();
    }
}
