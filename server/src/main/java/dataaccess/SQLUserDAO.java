package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class SQLUserDAO implements UserDAO {
    private final Connection conn;

    public SQLUserDAO(Connection conn) {
        this.conn = conn;
        try {
            createDatabase();
            createUserTable();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create user table", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM userTable WHERE username = ?";
        try (var preparedStatement = conn.prepareStatement(statement)){

            preparedStatement.setString(1, username);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    return new UserData(name, password, email);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT into userTable (username, password, email) VALUES (?, ?, ?)";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to create user", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE userTable";
        try (var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear user data", ex);
        }
    }
}
