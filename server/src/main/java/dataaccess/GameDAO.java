package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(String gameName);
    GameData getGameByID(int gameID);
    List<GameData> getAllGames();
    boolean checkColorOccupied(GameData game, ChessGame.TeamColor color);
    GameData join(GameData game, ChessGame.TeamColor color, String username);
    void clear();
}
