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
import service.*;
import service.ServiceLocator;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();

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
            color = "white";
        } else if (username.equals(game.blackUsername())) {
            color = "black";
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(username + " joined as " + color);
        connections.broadcast(command.getGameID(), command.getAuthToken(), new Gson().toJson(notification));

        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGameMessage.setGame(game.game());
        session.getRemote().sendString(serializeMessage(loadGameMessage));
    }

    private void handleMove(UserGameCommand command, Session session, String username, GameData game)
            throws IOException, InvalidMoveException, DataAccessException {
        ChessMove move = command.getMove();
        ChessGame chessGame = game.game();

        ChessGame.TeamColor playerColor = null;
        ChessGame.TeamColor opponentColor = null;

        if (username.equals(game.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
            opponentColor = ChessGame.TeamColor.BLACK;
        } else if (username.equals(game.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
            opponentColor = ChessGame.TeamColor.WHITE;
        } else {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.setMessage("Error: only players can make moves");
            connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(errorMessage));
            return;
        }

        String opponentUsername = (opponentColor == ChessGame.TeamColor.WHITE)
                ? game.whiteUsername()
                : game.blackUsername();

        if (!chessGame.getTeamTurn().equals(playerColor)) {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.setMessage("Error: only make moves on your turn");
            connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(errorMessage));
            return;
        }

        if (!chessGame.isValidMove(move)) {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.setMessage("Error: invalid move");
            connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(errorMessage));
            return;
        }

        chessGame.makeMove(move);

        GameService gameService = ServiceLocator.getGameService();
        gameService.updateGame(game.gameID(), game);

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(username + " made the move " + move);
        connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(notification));

        if (chessGame.isInCheckmate(opponentColor)) {
            ServerMessage checkmateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkmateNotification.setMessage(username + " has won by checkmate!");
            connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(checkmateNotification));
        } else if (chessGame.isInStalemate(opponentColor)) {
            ServerMessage checkmateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkmateNotification.setMessage("Stalemate!");
            connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(checkmateNotification));
        } else if (chessGame.isInCheck(opponentColor)) {
            ServerMessage checkNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkNotification.setMessage(opponentUsername + " is in check");
            connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(checkNotification));
        }

        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGameMessage.setGame(chessGame);
        connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(loadGameMessage));
    }

    private void handleLeave(UserGameCommand command, Session session, String username, GameData game) throws IOException, DataAccessException {
        connections.remove(command.getGameID(), command.getAuthToken());

        GameService gameService = ServiceLocator.getGameService();
        GameData updatedGame = game;

        if (username.equals(game.whiteUsername())) {
            updatedGame = game.withWhiteUsername(null);
        } else if (username.equals(game.blackUsername())) {
            updatedGame = game.withBlackUsername(null);
        }

        if (updatedGame != game) {
            gameService.updateGame(updatedGame.gameID(), updatedGame);
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(username + " left the game");
        connections.broadcast(command.getGameID(), command.getAuthToken(), serializeMessage(notification));
    }

    private void handleResign(UserGameCommand command, Session session, String username, GameData game) {

    }

    public String serializeMessage(ServerMessage message) {
        return gson.toJson(message);
    }
}
