package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.createAuthTable;

public class SQLAuthDAO implements AuthDAO {
    private final Connection conn;

    public SQLAuthDAO(Connection conn) {
        this.conn = conn;
        try {
            createAuthTable();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create auth table", e);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
         var statement = "INSERT INTO authTable (authToken, username) VALUES (?, ?)";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to create auth data", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM authTable WHERE authToken = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, authToken);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String token = resultSet.getString("authToken");
                    String username = resultSet.getString("username");
                    return new AuthData(token, username);
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("failed to retrieve auth data", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authTable WHERE authToken = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete auth data", ex);
        }
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        var statement = "SELECT username FROM authTable WHERE authToken = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, authToken);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("failed to retrieve auth data", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE authTable";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear auth data", ex);
        }
    }
}

