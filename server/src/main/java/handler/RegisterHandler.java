package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.*;

public class RegisterHandler implements Route {
    private final Gson gson;
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        gson = new Gson();
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        RegisterRequest registerRequest = generateRegisterRequest(request.body());
        RegisterResult registerResult = userService.register(registerRequest);
        return processRegisterResult(registerResult);
    }

    public RegisterRequest generateRegisterRequest(String json) {
        return gson.fromJson(json, RegisterRequest.class);
    }

    public String processRegisterResult(RegisterResult result) {
        return gson.toJson(result);
    }
}
