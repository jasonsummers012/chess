package websocket;

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
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        AuthService authService = ServiceLocator.getAuthService();
        GameService gameService = ServiceLocator.getGameService();

        String username = authService.getUsername(command.getAuthToken());
        GameData game = gameService.getGame(gameID);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(command, session, username);
            case MAKE_MOVE -> handleMove(command, username);
            case LEAVE -> handleLeave(command, username);
            case RESIGN -> handleResign(command, username);
        }
    }

    private void handleConnect(UserGameCommand command, Session session, String username) {
        connections.add(command.getGameID(), username, session);

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(username + "joined the game as " + command.get);
    }
}
