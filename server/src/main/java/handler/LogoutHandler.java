package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import result.LogoutResult;
import service.AuthService;
import spark.*;

public class LogoutHandler implements Route {
    private final Gson gson;
    private final AuthService authService;

    public LogoutHandler(AuthService authService) {
        gson = new Gson();
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        LogoutResult logoutResult = authService.logout(authToken);
        return processLogoutResult(logoutResult);
    }

    public String processLogoutResult(LogoutResult result) {
        return gson.toJson(result);
    }
}
