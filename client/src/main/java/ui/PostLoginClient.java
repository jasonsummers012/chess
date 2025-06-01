package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import handler.request.*;
import handler.result.*;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.List;

public class PostLoginClient {
    private String visitorName;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDIN;

    public PostLoginClient(String serverUrl, String visitorName) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.visitorName = visitorName;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "logout" -> logout(params);
                case "create game" -> createGame(params);
                case "list games" -> listGames(params);
                case "play game" -> playGame(params);
                case "observe game" -> observeGame(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
        LogoutRequest request = new LogoutRequest();
        LogoutResult result = server.logout(request);

        state = State.LOGGEDOUT;
        visitorName = null;
        return "You have been logged out.";
    }

    public String createGame(String... params) {
        if (params.length >= 1) {
            String gameName = params[0];

            CreateGameRequest request = new CreateGameRequest(gameName);
            CreateGameResult result = server.createGame(request);

            return String.format("You created a game with game ID: %s", result.gameID());
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String listGames(String... params) {
        ListGamesRequest request = new ListGamesRequest();
        ListGamesResult result = server.listGames(request);
        List<GameData> games = result.games();

        StringBuilder list = new StringBuilder();
        Gson gson = new Gson();
        for (GameData game : games) {
            list.append(gson.toJson(game)).append("\n");
        }
        return list.toString();
    }

    public String playGame(String... params) {
        if (params.length >= 2) {
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[0]);
            int gameID = Integer.parseInt(params[1]);

            JoinGameRequest request = new JoinGameRequest(color, gameID);
            JoinGameResult result = server.joinGame(request);

            return String.format("You joined game with ID %s as %s.", color.name(), gameID);
        }
        throw new ResponseException(400, "Expected <teamColor> <gameID>");
    }

    public String observeGame(String... params) {
        if (params.length >= 1) {
            int gameID = Integer.parseInt(params[0]);
            ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;

            JoinGameRequest request = new JoinGameRequest(color, gameID);
            JoinGameResult result = server.joinGame(request);

            return String.format("You are observing game with ID %s.", gameID);
        }
        throw new ResponseException(400, "Excpeted <gameID>");
    }

    public String help() {
        return """
            logout                           Log out of your account
            create game <gameName>           Create a new game
            list games                       List all games
            play game <gameID> <TeamColor>   Join a game
            observe game <gameID>            Spectate a game
            help                             Show possible commands
            """;
    }

}
