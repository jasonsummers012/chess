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

    @BeforeAll
    void setup() throws SQLException, DataAccessException {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        authDAO = new SQLAuthDAO(conn);
    }

    @AfterEach
    void clearTable() throws SQLException {
        try (var statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM auth");
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
