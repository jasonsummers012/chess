package client;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import request.*;
import result.*;
import ui.BoardDrawer;
import ui.Repl;
import ui.State;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.*;

public class PostLoginClient {
    private final Repl repl;
    private String playerName;
    private final ServerFacade server;
    private final String serverUrl;

    public PostLoginClient(String serverUrl, Repl repl, String playerName, ServerFacade server) {
        this.repl = repl;
        this.server = server;
        this.serverUrl = serverUrl;
        this.playerName = playerName;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (command.equals("create") && tokens.length > 1 && tokens[1].equalsIgnoreCase("game")) {
                return createGame(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("list") && tokens.length > 1 && tokens[1].equalsIgnoreCase("games")) {
                return listGames();
            } else if (command.equals("play") && tokens.length > 1 && tokens[1].equalsIgnoreCase("game")) {
                return playGame(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("observe") && tokens.length > 1 && tokens[1].equalsIgnoreCase("game")) {
                return observeGame(Arrays.copyOfRange(tokens, 2, tokens.length));
            }

            return switch (command) {
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        LogoutRequest request = new LogoutRequest();
        LogoutResult result = server.logout(request);

        repl.setState(State.LOGGEDOUT);
        playerName = null;
        repl.getPreLoginClient().clearPlayerName();
        return "You have been logged out.\n\n" + repl.getPreLoginHelp();
    }

    public String createGame(String... params) {
        if (params.length >= 1) {
            String gameName = params[0];

            CreateGameRequest request = new CreateGameRequest(gameName);
            CreateGameResult result = server.createGame(request);

            return String.format("You created a game with name: %s", gameName + "\n\n" + help());
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String listGames() {
        try {
            ListGamesResult result = server.listGames(null);
            List<GameData> games = result.games();

            if (games.isEmpty()) {
                return "No games available.";
            }

            StringBuilder list = new StringBuilder();
            int index = 1;
            for (GameData game : games) {
                list.append(String.format("%d. Game: %s - White: %s, Black: %s\n",
                        index++, game.gameName(),
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

                GameData currentGame = server.getGame(gameID);
                ChessBoard board = currentGame.game().getBoard();

                displayGameBoard(gameID, color, board);

                return String.format("You joined game with ID %d as %s.", gameID, color.name());
            } catch (IllegalArgumentException e) {
                throw new ResponseException(400, "Invalid team color. Use WHITE or BLACK.");
            }
        }
        throw new ResponseException(400, "Expected: <gameID> <TeamColor>");
    }

    public String observeGame(String... params) {
        if (params.length >= 1) {
            try {
                int gameID = Integer.parseInt(params[0]);
                try {
                    JoinGameRequest request = JoinGameRequest.forObserver(gameID);
                    JoinGameResult result = server.joinGame(request);

                    GameData currentGame = server.getGame(gameID);
                    ChessBoard board = currentGame.game().getBoard();

                    displayGameBoard(gameID, null, board);

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
        return SET_TEXT_COLOR_GREEN + """
            logout                           Log out of your account
            create game <gameName>           Create a new game
            list games                       List all games
            play game <gameID> <TeamColor>   Join a game
            observe game <gameID>            Spectate a game
            help                             Show possible commands
            """ + RESET_TEXT_COLOR;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    private void displayGameBoard(int gameID, ChessGame.TeamColor playerColor, ChessBoard board) {
        try {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);
            out.println();

            String viewInfo = (playerColor != null)
                    ? String.format("Playing as %s in Game %d", playerColor.name(), gameID)
                    : String.format("Observing Game %d", gameID);

            out.println(SET_TEXT_COLOR_BLUE + viewInfo + RESET_TEXT_COLOR);
            out.println();

            if (playerColor == ChessGame.TeamColor.BLACK) {
                BoardDrawer.drawBoardBlackPerspective(out, board, null);
            } else {
                BoardDrawer.drawBoardWhitePerspective(out, board, null);
            }

            out.println();

        } catch (Exception e) {
            System.err.println("Error displaying board: " + e.getMessage());
        }
    }
}
