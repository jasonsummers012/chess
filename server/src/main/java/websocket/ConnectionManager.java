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

        Connection existingConnection = connections.get(authToken);
        if (existingConnection != null) {
            connections.remove(authToken);
        }

        connections.put(authToken, new Connection(authToken, session));
    }

    public void remove(int gameID, String authToken) {

        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            Connection removed = connections.remove(authToken);

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
            int gameID = gameEntry.getKey();
            var connections = gameEntry.getValue();

            boolean removed = connections.entrySet().removeIf(entry -> {
                boolean matches = entry.getValue().session.equals(session);
                return matches;
            });

            if (connections.isEmpty()) {
                gameConnections.remove(gameID);
                System.out.println("ConnectionManager.removeSession: Removed empty game connections for game " + gameID);
            }
        }
        System.out.println("ConnectionManager.removeSession: Session removal complete");
    }
}