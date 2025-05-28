package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static dataaccess.DatabaseManager.*;

public class SQLGameDAO implements GameDAO {
    private final Gson gson;

    public SQLGameDAO() {
        this.gson = new Gson();
        try {
            DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create game table", e);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameTable (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, gson.toJson(game.game()));
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new DataAccessException("failed to retrieve game ID");
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException{
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameName = ?";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, gameName);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String name = resultSet.getString("gameName");
                    ChessGame game = gson.fromJson(resultSet.getString("game"), ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, name, game);
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
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setInt(1, gameID);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int iD = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String name = resultSet.getString("gameName");
                    ChessGame game = gson.fromJson(resultSet.getString("game"), ChessGame.class);
                    return new GameData(iD, whiteUsername, blackUsername, name, game);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to access game", ex);
        }
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException{
        List<GameData> games = new ArrayList<>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable";

        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement);
             var resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                ChessGame game = gson.fromJson(resultSet.getString("game"), ChessGame.class);

                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }
            return games;

        } catch (SQLException ex) {
            throw new DataAccessException("failed to retrieve games", ex);
        }
    }

    @Override
    public boolean checkColorOccupied(GameData game, ChessGame.TeamColor color) throws DataAccessException {
        if (getGame(game.gameName()) == null) {
            return false;
        }
        return color.equals(ChessGame.TeamColor.WHITE) ? game.whiteUsername() != null
                : game.blackUsername() != null;
    }

    @Override
    public GameData join(GameData game, ChessGame.TeamColor color, String username) throws DataAccessException {
        if (getGame(game.gameName()) == null) {
            return null;
        }
        String updateStatement;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            updateStatement = "UPDATE gameTable SET whiteUsername = ? WHERE gameID = ? AND whiteUsername IS NULL";
        } else {
            updateStatement = "UPDATE gameTable SET blackUsername = ? WHERE gameID = ? AND blackUsername IS NULL";
        }

        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(updateStatement)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, game.gameID());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("failed to join game");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to join game", ex);
        }

        String selectStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(selectStatement)) {
            preparedStatement.setInt(1, game.gameID());
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String name = resultSet.getString("gameName");
                    ChessGame chessGame = gson.fromJson(resultSet.getString("game"), ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, name, chessGame);
                } else {
                    throw new DataAccessException("failure to join game");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to retrieve updated game", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE gameTable";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear auth data", ex);
        }
    }
}
