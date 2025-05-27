package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dataaccess.DatabaseManager.*;

public class SQLGameDAO implements GameDAO {
    private final Connection conn;
    private final Gson gson;

    public SQLGameDAO(Connection conn) {
        this.conn = conn;
        this.gson = new Gson();
        try {
            createDatabase();
            createGameTable();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create game table", e);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameTable (gameID, whiteUsername, blackUsername, gameName, ChessGame) VALUES (?, ?, ?, ?, ?)";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.blackUsername());
            preparedStatement.setString(4, game.gameName());
            preparedStatement.setString(5, gson.toJson(game.game()));
            preparedStatement.executeUpdate();

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
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setInt(1, gameID);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int ID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String name = resultSet.getString("gameName");
                    ChessGame game = gson.fromJson(resultSet.getString("game"), ChessGame.class);
                    return new GameData(ID, whiteUsername, blackUsername, name, game);
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

        try (var preparedStatement = conn.prepareStatement(statement);
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
    public boolean checkColorOccupied(GameData game, ChessGame.TeamColor color) {
        return color.equals(ChessGame.TeamColor.WHITE) ? game.whiteUsername() != null
                : game.blackUsername() != null;
    }

    @Override
    public GameData join(GameData game, ChessGame.TeamColor color, String username) throws DataAccessException {
        String updateStatement;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            updateStatement = "UPDATE gameTable SET whiteUsername = ? WHERE gameID = ?";
        } else {
            updateStatement = "UPDATE gameTable SET blackUsername = ? WHERE gameID = ?";
        }

        try (var preparedStatement = conn.prepareStatement(updateStatement)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, game.gameID());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to join game", ex);
        }

        String selectStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameID = ?";
        try (var preparedStatement = conn.prepareStatement(selectStatement)) {
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
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to retrieve updated game", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE gameTable";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear auth data", ex);
        }
    }
}
