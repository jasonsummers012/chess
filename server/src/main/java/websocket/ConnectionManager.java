package websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String playerName, Session session) {
        var connection = new Connection(playerName, session);
        connections.put(playerName, connection);
    }

    public void remove(String playerName) {
        connections.remove(playerName);
    }

    public void broadcast(String excludePlayerName, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (!c.playerName.equals(excludePlayerName)) {
                c.send(notification.toString());
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.playerName);
        }
    }
}
