package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.GameService;
import service.ServiceLocator;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            AuthService authService = ServiceLocator.getAuthService();
            GameService gameService = ServiceLocator.getGameService();

            String username = authService.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData game = gameService.getGame(gameID);

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(command, session, username, game);
                case MAKE_MOVE -> handleMove(command, session, username, game);
                case LEAVE -> handleLeave(command, session, username, game);
                case RESIGN -> handleResign(command, session, username, game);
            }
        } catch (DataAccessException | IOException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void handleConnect(UserGameCommand command, Session session, String username, GameData game) throws IOException {
        connections.add(command.getGameID(), command.getAuthToken(), session);

        String color = "observer";
        if (username.equals(game.whiteUsername())) {
            color = "WHITE";
        } else if (username.equals(game.blackUsername())) {
            color = "BLACK";
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(username + "joined as " + color);
        connections.broadcast(command.getGameID(), command.getAuthToken(), new Gson().toJson(notification));
    }

    private void handleMove(UserGameCommand command, Session session, String username, GameData game) throws IOException, InvalidMoveException {
        ChessMove move = command.getMove();
        ChessGame chessGame = game.game();

        ChessGame.TeamColor playerColor = null;
        if (username.equals(game.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            sendError(session, "Error: Only players can make moves.");
            return;
        }

        if (!chessGame.getTeamTurn().equals(playerColor)) {
            sendError(session, "Error: Not your turn.");
            return;
        }

        if (!chessGame.isValidMove(move)) {
            sendError(session, "Error: Invalid move.");
            return;
        }

        chessGame.makeMove(move);

        GameService gameService = ServiceLocator.getGameService();
        gameService.updateGame(game.gameID(), chessGame);
    }

    private void sendError(Session session, String message) throws IOException {
        ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        errorMessage.setErrorMessage(message);
        session.getRemote().sendString(new Gson().toJson(errorMessage));

    }
}
