package websocket;

import chess.ChessGame;
import client.GameplayClient;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class ClientNotificationHandler implements NotificationHandler {
    private final GameplayClient gameplayClient;

    public ClientNotificationHandler(GameplayClient client) {
        this.gameplayClient = client;
    }
    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame(message.getGame());
            case ERROR -> handleError(message.getErrorMessage());
            case NOTIFICATION -> handleNotification(message.getMessage());
        }
    }

    private void handleLoadGame(ChessGame game) {
        System.out.println();
        gameplayClient.updateGameStatus(game);
    }

    private void handleError(String errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED + errorMessage + RESET_TEXT_COLOR);
    }

    private void handleNotification(String notification) {
        System.out.println(SET_TEXT_COLOR_YELLOW + notification + RESET_TEXT_COLOR);
    }
}
