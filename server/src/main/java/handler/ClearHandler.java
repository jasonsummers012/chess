package handler;

import dataaccess.DataAccessException;
import service.*;
import spark.*;

public class ClearHandler implements Route {
    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;

    public ClearHandler(UserService userService, GameService gameService, AuthService authService) {
        this.userService = userService;
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        userService.clear();
        gameService.clear();
        authService.clear();
        return "{}";
    }
}
