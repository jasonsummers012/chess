package client;

import chess.*;
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
        this.notificationHandler = new ClientNotificationHandler(this);

        this.webSocketFacade = new WebSocketServerFacade(
                serverUrl,
                authToken,
                this::handleServerMessage);

        this.webSocketFacade.connect(authToken, gameID);

        repl.setState(State.INGAME);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            if (tokens.length == 0) {
                return help();
            }

            if (tokens[0].equals("make")) {
                // Check if we have enough tokens for a move command
                if (tokens.length < 4) {
                    return "Error: Invalid move format. Use: make move <from> <to> [promotion]";
                }

                ChessPosition startPosition = stringToPosition(tokens[2]);
                ChessPosition endPosition = stringToPosition(tokens[3]);

                ChessPiece.PieceType promotion = null;
                if (tokens.length > 4) {
                    promotion = stringToPiece(tokens[4]);
                }

                ChessMove move = new ChessMove(startPosition, endPosition, promotion);
                return makeMove(move);
            } else if (tokens[0].equals("highlight") && tokens.length >= 4) {
                ChessPosition position = stringToPosition(tokens[3]);
                return highlightLegalMoves(position);
            }

            return switch (tokens[0]) {
                case "redraw" -> redrawChessBoard();
                case "leave" -> leave();
                case "resign" -> resign();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex) {
            return "Error: Invalid command format. Type 'help' for available commands.";
        }
    }

    private void handleServerMessage(ServerMessage message) {
        notificationHandler.notify(message);
    }

    public void updateGameStatus(ChessGame newGame) {
        if (newGame != null) {
            this.currentGame = newGame;
            this.board = newGame.getBoard();
            redrawChessBoard();
        }
    }

    public String redrawChessBoard() {
        displayGameBoard(null);
        return "";
    }

    public String leave() throws ResponseException {
        webSocketFacade.leave(authToken, gameID);
        webSocketFacade.close();
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

    private String highlightLegalMoves(ChessPosition position) {
        displayGameBoard(position);
        return "";
    }

    private String help() {
        return SET_TEXT_COLOR_GREEN + """
            redraw chess board                 Draw current board
            leave                              Remove from game
            make move <from> <to> [promotion]  Move piece
            resign                             Forfeit game
            highlight legal moves <piece>      Show all possible moves for piece
            help                               Show possible commands
            """ + RESET_TEXT_COLOR;
    }

    private void displayGameBoard(ChessPosition position) {
        try {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);
            out.println();

            if (playerColor == ChessGame.TeamColor.BLACK) {
                BoardDrawer.drawBoardBlackPerspective(out, board, position);
            } else {
                BoardDrawer.drawBoardWhitePerspective(out, board, position);
            }

            out.println();

        } catch (Exception e) {
            System.err.println("Error displaying board: " + e.getMessage());
        }
    }

    private ChessPosition stringToPosition(String input) throws ResponseException {
        if (input == null || input.length() != 2) {
            throw new ResponseException(400, "Error: invalid position format");
        }

        char col = Character.toLowerCase(input.charAt(0));
        char rowChar = input.charAt(1);

        if (col < 'a' || col > 'h') {
            throw new ResponseException(400, "Error: column must be between a and h");
        }

        if (rowChar < '1' || rowChar > '8') {
            throw new ResponseException(400, "Error: row must be between 1 and 8");
        }

        int row = Character.getNumericValue(rowChar);

        return new ChessPosition(row, col - 'a' + 1);
    }

    private ChessPiece.PieceType stringToPiece(String input) {
        if (input.length() > 4) {
            return ChessPiece.PieceType.valueOf(input.toUpperCase());
        }
        return null;
    }

    public void setNotificationHandler(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }
}
