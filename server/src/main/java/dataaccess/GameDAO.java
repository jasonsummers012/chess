package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    GameData getGameByID(int gameID) throws DataAccessException;
    List<GameData> getAllGames() throws DataAccessException;
    boolean checkColorOccupied(GameData game, ChessGame.TeamColor color) throws DataAccessException;
    GameData join(GameData game, ChessGame.TeamColor color, String username) throws DataAccessException;
    void updateGame(int gameID, ChessGame newGame) throws DataAccessException;
    void clear() throws DataAccessException;
}
