package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static dataaccess.DatabaseManager.createGameTable;

public class SQLGameDAO implements GameDAO {
    private final Connection conn;

    public SQLGameDAO(Connection conn) throws DataAccessException {
        this.conn = conn;
        createGameTable();
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(String gameName) {

    }

    @Override
    public GameData getGameByID(int gameID) {

    }

    @Override
    public List<GameData> getAllGames() {

    }

    @Override
    public boolean checkColorOccupied(GameData game, ChessGame.TeamColor color) {

    }

    @Override
    public GameData join(GameData game, ChessGame.TeamColor color, String username) {

    }

    @Override
    public void clear() {

    }
}

