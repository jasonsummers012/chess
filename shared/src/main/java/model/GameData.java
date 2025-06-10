package model;

import chess.ChessGame;

public record GameData (
    int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, boolean gameOver
) {

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(gameID, whiteUsername, blackUsername, gameName, game, false);
    }

    public GameData withWhiteUsername(String newWhiteUsername) {
        return new GameData(gameID, newWhiteUsername, blackUsername, gameName, game, gameOver);
    }

    public GameData withBlackUsername(String newBlackUsername) {
        return new GameData(gameID, whiteUsername, newBlackUsername, gameName, game, gameOver);
    }

    public GameData withGameOver(boolean newGameOver) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, newGameOver);
    }
}
