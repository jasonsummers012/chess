package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> gameConnections = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        ConcurrentHashMap<String, Connection> connections = gameConnections.computeIfAbsent(
                gameID,
                k -> new ConcurrentHashMap<>()
        );
        connections.put(authToken, new Connection(authToken, session));
    }

    public void remove(int gameID, String authToken) {
        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            connections.remove(authToken);
            if (connections.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeAuthToken, String message) throws IOException {
        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections == null) {
            return;
        }

        var failedConnections = new ArrayList<String>();

        for (var entry : connections.entrySet()) {
            String authToken = entry.getKey();
            Connection connection = entry.getValue();

            if (authToken.equals(excludeAuthToken)) {
                continue;
            }

            try {
                connection.send(message);
            } catch (IOException e) {
                failedConnections.add(authToken);
            }
        }

        for (String authToken : failedConnections) {
            connections.remove(authToken);
        }
    }

    public void broadcastToAll(int gameID, String message) throws IOException {
        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections == null) {
            return;
        }

        var failedConnections = new ArrayList<String>();

        for (var entry : connections.entrySet()) {
            String authToken = entry.getKey();
            Connection connection = entry.getValue();

            try {
                connection.send(message);
            } catch (IOException e) {
                failedConnections.add(authToken);
            }
        }

        for (String authToken : failedConnections) {
            connections.remove(authToken);
        }

        if (connections.isEmpty()) {
            gameConnections.remove(gameID);
        }
    }

    public void removeSession(Session session) {
        for (var gameEntry : gameConnections.entrySet()) {
            var connections = gameEntry.getValue();
            connections.entrySet().removeIf(entry -> entry.getValue().session.equals(session));
            if (connections.isEmpty()) {
                gameConnections.remove(gameEntry.getKey());
            }
        }
    }
}