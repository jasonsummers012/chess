package handler;

import com.google.gson.Gson;
import handler.request.LoginRequest;
import handler.result.LoginResult;

public class LoginHandler {
    private final Gson gson;

    public LoginHandler() {
        gson = new Gson();
    }

    public LoginRequest generateLoginRequest(String json) {
        return gson.fromJson(json, LoginRequest.class);
    }

    public String processLoginResult(LoginResult result) {
        return gson.toJson(result);
    }
}
