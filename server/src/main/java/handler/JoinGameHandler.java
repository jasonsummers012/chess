package handler;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import handler.request.JoinGameRequest;
import handler.request.ListGamesRequest;
import handler.result.JoinGameResult;
import handler.result.ListGamesResult;
import service.AuthService;
import service.GameService;
import spark.*;

public class JoinGameHandler implements Route {
    private final Gson gson;
    private final GameService gameService;
    private final AuthService authService;

    public JoinGameHandler(GameService gameService, AuthService authService) {
        gson = new Gson();
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    public Object handle(Request request, Response response) throws UnauthorizedException, AlreadyTakenException {
        String authToken = request.headers("authorization");
        authService.checkValidAuthToken(authToken);

        JoinGameRequest joinGameRequest = generateJoinGameRequest(request.body());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, authToken);
        return processJoinGameResult(joinGameResult);
    }

    public JoinGameRequest generateJoinGameRequest(String json) {
        return gson.fromJson(json, JoinGameRequest.class);
    }

    public String processJoinGameResult(JoinGameResult result) {
        return gson.toJson(result);
    }
}
