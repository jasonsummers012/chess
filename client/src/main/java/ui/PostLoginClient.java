package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import request.*;
import result.*;

import java.util.Arrays;
import java.util.List;

public class PostLoginClient {
    private final Repl repl;
    private String visitorName;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDIN;

    public PostLoginClient(String serverUrl, Repl repl, String visitorName, ServerFacade server) {
        this.repl = repl;
        this.server = server;
        this.serverUrl = serverUrl;
        this.visitorName = visitorName;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (command.equals("create") && tokens.length > 1 && tokens[1].toLowerCase().equals("game")) {
                return createGame(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("list") && tokens.length > 1 && tokens[1].toLowerCase().equals("games")) {
                return listGames(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("play") && tokens.length > 1 && tokens[1].toLowerCase().equals("game")) {
                return playGame(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("observe") && tokens.length > 1 && tokens[1].toLowerCase().equals("game")) {
                return observeGame(Arrays.copyOfRange(tokens, 2, tokens.length));
            }

            return switch (command) {
                case "logout" -> logout(params);
                case "help" -> help();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
        LogoutRequest request = new LogoutRequest();
        LogoutResult result = server.logout(request);

        repl.setState(State.LOGGEDOUT);
        visitorName = null;
        repl.getPreLoginClient().clearVisitorName();
        return "You have been logged out.\n\n" + repl.getPreLoginHelp();
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
        try {
            ListGamesResult result = server.listGames(null);
            List<GameData> games = result.games();

            if (games.isEmpty()) {
                return "No games available.";
            }

            StringBuilder list = new StringBuilder();
            int index = 1;
            for (GameData game : games) {
                list.append(String.format("%d. Game: %s (ID: %d) - White: %s, Black: %s\n",
                        index++, game.gameName(), game.gameID(),
                        game.whiteUsername() != null ? game.whiteUsername() : "empty",
                        game.blackUsername() != null ? game.blackUsername() : "empty"));
            }
            return list.toString();
        } catch (Exception e) {
            return "Error listing games: " + e.getMessage();
        }
    }

    public String playGame(String... params) {
        if (params.length >= 2) {
            try {
                int gameID = Integer.parseInt(params[0]);
                ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());

                JoinGameRequest request = new JoinGameRequest(color, gameID);
                JoinGameResult result = server.joinGame(request);

                return String.format("You joined game with ID %d as %s.", gameID, color.name());
            } catch (IllegalArgumentException e) {
                throw new ResponseException(400, "Invalid team color. Use WHITE or BLACK.");
            }
        }
        throw new ResponseException(400, "Expected: <teamColor> <gameID>");
    }

    public String observeGame(String... params) {
        if (params.length >= 1) {
            try {
                int gameID = Integer.parseInt(params[0]);
                try {
                    JoinGameRequest request = new JoinGameRequest(null, gameID);
                    JoinGameResult result = server.joinGame(request);
                    return String.format("You are observing game with ID %d.", gameID);
                } catch (ResponseException e) {
                    return String.format("Unable to observe game %d. Server response: %s", gameID, e.getMessage());
                }

            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid game ID. Must be a number.");
            }
        }
        throw new ResponseException(400, "Expected: <gameID>");
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

    public State getState() {
        return state;
    }

    public void setVisitorName(String name) {
        this.visitorName = name;
    }
}
