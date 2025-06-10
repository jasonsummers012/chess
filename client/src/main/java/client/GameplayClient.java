package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import exception.ResponseException;
import ui.BoardDrawer;
import ui.Repl;
import ui.State;
import websocket.ClientNotificationHandler;
import websocket.NotificationHandler;
import websocket.WebSocketServerFacade;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class GameplayClient {
    private final Repl repl;
    private final WebSocketServerFacade webSocketFacade;
    private final String authToken;
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    private ChessGame currentGame;
    private ChessBoard board;
    private NotificationHandler notificationHandler;

    public GameplayClient (
            String serverUrl,
            Repl repl,
            String authToken,
            int gameID,
            ChessGame.TeamColor playerColor,
            ChessGame initialGame
    ) throws ResponseException {
        this.repl = repl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.currentGame = initialGame;
        this.board = initialGame.getBoard();
        this.notificationHandler = new ClientNotificationHandler();

        this.webSocketFacade = new WebSocketServerFacade(
                serverUrl,
                authToken,
                this::handleServerMessage);

        repl.setState(State.INGAME);
        redrawChessBoard();
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            if (tokens.length == 0) {
                return help();
            }

            return switch (tokens[0]) {
                case "redraw" -> redrawChessBoard();
                case "leave" -> leave();
                case "make" -> makeMove();
                case "resign" -> resign();
                case "highlight" -> highlightLegalMoves();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private void handleServerMessage(ServerMessage message) {
        notificationHandler.notify(message);
    }

    private String redrawChessBoard() {
        displayGameBoard();
        return "";
    }

    private String leave() throws ResponseException {
        webSocketFacade.leave(authToken, gameID);
        repl.setState(State.LOGGEDIN);
        return "You left the game";
    }

    private String makeMove(ChessMove move) throws ResponseException {
        webSocketFacade.makeMove(authToken, gameID, move);
        return "You made the move: " + move;
    }

    private String resign() throws ResponseException {
        webSocketFacade.resign(authToken, gameID);
        return "You resigned the game";
    }

    private String help() {
        return SET_TEXT_COLOR_GREEN + """
            redraw chess board             Draw current board
            leave                          Remove from game
            make move <from> <to>          Move piece
            resign                         Forfeit game
            highlight legal moves <piece>  Show all possible moves for piece
            help                           Show possible commands
            """ + RESET_TEXT_COLOR;
    }

    private void displayGameBoard() {
        try {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);
            out.println();

            if (playerColor == ChessGame.TeamColor.BLACK) {
                BoardDrawer.drawBoardBlackPerspective(out, board);
            } else {
                BoardDrawer.drawBoardWhitePerspective(out, board);
            }

            out.println();

        } catch (Exception e) {
            System.err.println("Error displaying board: " + e.getMessage());
        }
    }
}
