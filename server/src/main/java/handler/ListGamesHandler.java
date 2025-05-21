package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handler.request.CreateGameRequest;
import handler.request.ListGamesRequest;
import handler.result.CreateGameResult;
import handler.result.ListGamesResult;
import service.AuthService;
import service.GameService;
import spark.*;

public class ListGamesHandler implements Route{
    private final Gson gson;
    private final GameService gameService;
    private final AuthService authService;

    public ListGamesHandler(GameService gameService, AuthService authService) {
        gson = new Gson();
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        if (!authService.checkValidAuthToken(authToken)) {
            throw new DataAccessException("Error: invalid auth token");
        }

        ListGamesRequest listGamesRequest = generateListGamesRequest(request.body());
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        return processListGamesResult(listGamesResult);
    }

    public ListGamesRequest generateListGamesRequest(String json) {
        return gson.fromJson(json, ListGamesRequest.class);
    }

    public String processListGamesResult(ListGamesResult result) {
        return gson.toJson(result);
    }
}
