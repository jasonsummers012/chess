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
        var statement = "INSERT INTO gameTable (whiteUsername, blackUsername, gameName, game, gameOver) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, gson.toJson(game.game()));
            preparedStatement.setBoolean(5, game.gameOver());
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
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game, gameOver FROM gameTable WHERE gameName = ?";
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
                    boolean gameOver = resultSet.getBoolean("gameOver");
                    return new GameData(gameID, whiteUsername, blackUsername, name, game, gameOver);
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
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game, gameOver FROM gameTable WHERE gameID = ?";
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
                    boolean gameOver = resultSet.getBoolean("gameOver");
                    return new GameData(iD, whiteUsername, blackUsername, name, game, gameOver);
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
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game, gameOver FROM gameTable";

        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement);
             var resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                ChessGame game = gson.fromJson(resultSet.getString("game"), ChessGame.class);
                boolean gameOver = resultSet.getBoolean("gameOver");

                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game, gameOver));
            }
            return games;

        } catch (SQLException ex) {
            throw new DataAccessException("failed to retrieve games", ex);
        }
    }

    @Override
    public boolean checkColorOccupied(GameData game, ChessGame.TeamColor color) throws DataAccessException {

        GameData freshGame = getGameByID(game.gameID());

        if (freshGame == null) {
            return false;
        }

        boolean result = color.equals(ChessGame.TeamColor.WHITE) ? freshGame.whiteUsername() != null
                : freshGame.blackUsername() != null;

        return result;
    }

    @Override
    public GameData join(GameData game, ChessGame.TeamColor color, String username) throws DataAccessException {
        GameData freshGame = getGameByID(game.gameID());

        if (freshGame == null) {
            throw new DataAccessException("Game not found");
        }

        String updateStatement;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            updateStatement = "UPDATE gameTable SET whiteUsername = ? WHERE gameID = ? AND whiteUsername IS NULL";
        } else {
            updateStatement = "UPDATE gameTable SET blackUsername = ? WHERE gameID = ? AND blackUsername IS NULL";
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            try (var preparedStatement = conn.prepareStatement(updateStatement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, game.gameID());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DataAccessException("failed to join game");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to join game: " + ex.getMessage(), ex);
        }

        try {
            GameData result = getGameByID(game.gameID());
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void updateGame(int gameID, GameData newGame) throws DataAccessException {

        var statement = "UPDATE gameTable SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?, gameOver = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, newGame.whiteUsername());
            preparedStatement.setString(2, newGame.blackUsername());
            preparedStatement.setString(3, newGame.gameName());
            preparedStatement.setString(4, gson.toJson(newGame.game()));
            preparedStatement.setBoolean(5, newGame.gameOver());
            preparedStatement.setInt(6, gameID);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("failed to find game");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to update game", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE gameTable";
        try (Connection conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear game data", ex);
        }
    }
}
