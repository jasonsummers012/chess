package websocket;

import chess.ChessGame;

public interface NotificationHandler {
    void onLoadGame(ChessGame game);
    void onError(String errorMessage);
    void onNotification(String message);
}
