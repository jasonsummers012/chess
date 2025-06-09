package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.Action;
import webSocketMessages.Notification;

import java.io.IOException;
import java.util.Timer;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Action action = new Gson().fromJson(message, Action.class);
        switch (action.type()) {
            case JOIN -> enter(action.playerName(), session);
            case LEAVE -> exit(action.playerName());
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
