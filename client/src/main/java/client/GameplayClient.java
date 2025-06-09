package client;

import chess.ChessGame;
import exception.ResponseException;
import websocket.WebSocketServerFacade;
import ui.Repl;
import ui.State;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class GameplayClient {
    private final Repl repl;
    private final WebSocketServerFacade webSocketFacade;
    private final String authToken;
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    private WebSocketClient webSocket;

    public GameplayClient (
            String serverUrl,
            Repl repl,
            String authToken,
            int gameID,
            ChessGame.TeamColor playerColor
    ) throws ResponseException {
        this.repl = repl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;

        this.webSocketFacade = new WebSocketServerFacade(serverUrl);
        try {
            this.webSocketFacade = webSocketFacade.connect(
                    authToken,
                    gameID,
                    this::handleServerMessage
            );
        } catch (Exception e) {
            throw new ResponseException(500, "Websocket connection failed: " + e.getMessage());
        }

        repl.setState(State.INGAME);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (command.equals("redraw") && tokens.length > 1 && tokens[1].equalsIgnoreCase("chess") && tokens[2].equalsIgnoreCase("board")) {
                return redrawChessBoard(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("make") && tokens.length > 1 && tokens[1].equalsIgnoreCase("move")) {
                return listGames(Arrays.copyOfRange(tokens, 2, tokens.length));
            } else if (command.equals("highlight") && tokens.length > 1 && tokens[1].equalsIgnoreCase("legal") && tokens[2].equalsIgnoreCase("moves")) {
                return highlightLegalMoves(Arrays.copyOfRange(tokens, 2, tokens.length));
            }

            return switch (command) {
                case "leave" -> leave(params);
                case "resign" -> resign(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return SET_TEXT_COLOR_GREEN + """
            redraw chess board             Draw current board
            leave                          Remove from game
            make move <from> <to>          Move piece
            resign                         Forfeit game
            highlight legal moves <piece>  Show all possible moves for piece
            help                           Show possible commands
            """ + RESET_TEXT_COLOR;
    }
}
