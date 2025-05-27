package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.DriverManager;

import static dataaccess.DatabaseManager.createUserTable;

public class SQLUserDAO implements UserDAO {
    private final Connection conn;

    public SQLUserDAO(Connection conn) throws DataAccessException {
        this.conn = conn;
        createUserTable();
    }

    @Override
    public UserData getUser(String username) {

    }

    @Override
    public void createUser(UserData user) {
        var statement = "INSERT into userTable ("
    }

    @Override
    public void clear() {

    }
}
