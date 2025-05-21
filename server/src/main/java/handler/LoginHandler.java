package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handler.request.LoginRequest;
import handler.result.LoginResult;
import service.UserService;
import spark.*;

public class LoginHandler implements Route {
    private final Gson gson;
    private final UserService userService;

    public LoginHandler(UserService userService) {
        gson = new Gson();
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        LoginRequest loginRequest = generateLoginRequest(request.body());
        LoginResult loginResult = userService.login(loginRequest);
        return processLoginResult(loginResult);
    }

    public LoginRequest generateLoginRequest(String json) {
        return gson.fromJson(json, LoginRequest.class);
    }

    public String processLoginResult(LoginResult result) {
        return gson.toJson(result);
    }
}
