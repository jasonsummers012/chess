package websocket;

import com.google.gson.Gson;
//import dataaccess.DataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, Action.class);
        switch (command.getCommandType()) {
            case JOIN -> enter(command.playerName(), session);
            case LEAVE -> exit(command.playerName());
        }
    }

    private void enter(String playerName, Session session) throws IOException {
        connections.add(playerName, session);
        var message = String.format("%s has joined the game", playerName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(playerName, notification);
    }

    private void exit(String playerName) throws IOException {
        connections.remove(playerName);
        var message = String.format("%s left the game", playerName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(playerName, notification);
    }
}
