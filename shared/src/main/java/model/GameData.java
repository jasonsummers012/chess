package model;

import chess.ChessGame;

public record GameData (
    int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game
) {
    public GameData withWhiteUsername(String newWhiteUsername) {
        return new GameData(gameID, newWhiteUsername, blackUsername, gameName, game);
    }

    public GameData withBlackUsername(String newBlackUsername) {
        return new GameData(gameID, whiteUsername, newBlackUsername, gameName, game);
    }
}
