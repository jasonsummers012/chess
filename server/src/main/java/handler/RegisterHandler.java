package handler;

import com.google.gson.Gson;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;

public class RegisterHandler {
    private final Gson gson;

    public RegisterHandler() {
        gson = new Gson();
    }

    public RegisterRequest generateRegisterRequest(String json) {
        return gson.fromJson(json, RegisterRequest.class);
    }

    public RegisterResult generateRegisterResult(String json) {
        return gson.fromJson(json, RegisterResult.class);
    }
}
