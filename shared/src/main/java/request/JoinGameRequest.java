package request;

import chess.ChessGame;

public record JoinGameRequest(
        ChessGame.TeamColor playerColor, int gameID, boolean observer
) {
    public JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
        this(playerColor, gameID, false);
    }

    public static JoinGameRequest forObserver(int gameID) {
        return new JoinGameRequest(null, gameID, true);
    }
}
