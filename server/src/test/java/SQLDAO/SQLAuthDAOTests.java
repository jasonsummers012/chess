package SQLDAO;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLAuthDAOTests {
    private Connection conn;
    private SQLAuthDAO authDAO;

    @BeforeEach
    void setup() throws SQLException, DataAccessException {
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/testdb",
                "root",
                "SAirplane12#"
        );

        try (var statement = conn.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS authTable (" +
                    "username VARCHAR(255) NOT NULL," +
                    "authToken VARCHAR(255) PRIMARY KEY)"
            );
        }
        authDAO = new SQLAuthDAO(conn);
    }

    @AfterEach
    void clearTable() throws SQLException {
        try (var statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM authTable");
        }
    }

    @AfterAll
    void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    void CreateAuthSuccessful() throws DataAccessException {
        AuthData auth = new AuthData("Jeremy", "123");
        authDAO.createAuth(auth);
        AuthData current = authDAO.getAuth("123");

        assertNotNull(current);
        assertEquals("Jeremy", current.username());
        assertEquals("123", current.authToken());
    }
}
