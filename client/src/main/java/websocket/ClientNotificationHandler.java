package websocket;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class ClientNotificationHandler implements NotificationHandler {
    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame(message.getGame());
            case ERROR -> handleError(message.getErrorMessage());
            case NOTIFICATION -> handleNotification(message.getMessage());
        }
    }

    private void handleLoadGame(ChessGame game) {

    }

    private void handleError(String errorMessage) {

    }

    private void handleNotification(String notification) {

    }
}
