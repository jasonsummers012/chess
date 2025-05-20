package handler;

import com.google.gson.Gson;
import handler.request.LogoutRequest;
import handler.result.LogoutResult;

public class LogoutHandler {
    private final Gson gson;

    public LogoutHandler() {
        gson = new Gson();
    }

    public LogoutRequest generateLogoutRequest(String json) {
        return gson.fromJson(json, LogoutRequest.class);
    }

    public String processLogoutResult(LogoutResult result) {
        return gson.toJson(result);
    }
}
