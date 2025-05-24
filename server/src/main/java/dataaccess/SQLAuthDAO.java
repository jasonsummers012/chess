package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.DriverManager;

import static dataaccess.DatabaseManager.createAuthTable;

public class SQLAuthDAO implements AuthDAO {
    private final Connection conn;

    public SQLAuthDAO(Connection conn) throws DataAccessException {
        this.conn = conn;
        createAuthTable();
    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public AuthData getAuth(String AuthToken) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public String getUsername(String authToken) {

    }

    @Override
    public void clear();
}

