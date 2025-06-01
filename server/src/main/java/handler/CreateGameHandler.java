package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.AuthService;
import service.GameService;
import spark.*;

public class CreateGameHandler implements Route {
    private final Gson gson;
    private final GameService gameService;
    private final AuthService authService;

    public CreateGameHandler(GameService gameService, AuthService authService) {
        gson = new Gson();
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws UnauthorizedException, DataAccessException {
        String authToken = request.headers("authorization");
        authService.checkValidAuthToken(authToken);

        CreateGameRequest createGameRequest = generateCreateGameRequest(request.body());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        return processCreateGameResult(createGameResult);
    }

    public CreateGameRequest generateCreateGameRequest(String json) {
        return gson.fromJson(json, CreateGameRequest.class);
    }

    public String processCreateGameResult(CreateGameResult result) {
        return gson.toJson(result);
    }
}