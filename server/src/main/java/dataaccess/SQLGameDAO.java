package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static dataaccess.DatabaseManager.createGameTable;

public class SQLGameDAO implements GameDAO {
    private final Connection conn;
    private final Gson gson;

    public SQLGameDAO(Connection conn) throws DataAccessException {
        this.conn = conn;
        this.gson = new Gson();
        createGameTable();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameTable (gameID, whiteUsername, blackUsername, gameName, ChessGame) VALUES (?, ?, ?, ?, ?)";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, Integer.toString(game.gameID()));
            preparedStatement.setString(4, game.gameName());
            preparedStatement.setString(5, gson.toJson(game.game()));

        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException{
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameName = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, gameName);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String gameID = resultSet.getString("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String name = resultSet.getString("gameName");
                    String game = resultSet.getString("game");
                    return new GameData(Integer.parseInt(gameID), whiteUsername, blackUsername, name, gson.fromJson(game, ChessGame.class));
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to access game", ex);
        }
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameName = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, Integer.toString(gameID));

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String ID = resultSet.getString("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String name = resultSet.getString("gameName");
                    String game = resultSet.getString("game");
                    return new GameData(Integer.parseInt(ID), whiteUsername, blackUsername, name, gson.fromJson(game, ChessGame.class));
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to access game", ex);
        }
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
