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
    void setup() throws SQLException {
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
        authDAO = new SQLAuthDAO();
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

    @Test
    void CreateDuplicateAuth() throws DataAccessException {
        AuthData auth1 = new AuthData("Jeremy", "123");
        AuthData auth2 = new AuthData("Jacob", "123");
        authDAO.createAuth(auth1);

        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(auth2));
    }

    @Test
    void getAuthSuccessful() throws DataAccessException {
        AuthData auth = new AuthData("Shaw", "444");
        authDAO.createAuth(auth);

        assertNotNull(authDAO.getAuth("444"));
        assertEquals(auth, authDAO.getAuth("444"));
    }

    @Test
    void getAuthDoesntExist() throws DataAccessException {
        AuthData auth = new AuthData("Shaw", "444");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth("445"));
    }

    @Test
    void deleteAuthSuccessful() throws DataAccessException {
        AuthData auth = new AuthData("Boog", "234");
        authDAO.createAuth(auth);
        authDAO.deleteAuth(auth.authToken());

        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth("234"));
    }

    @Test
    void deleteAuthDoesntExist() throws DataAccessException {
        AuthData auth = new AuthData("Boog", "234");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () ->
                authDAO.deleteAuth("235"));
    }

    @Test
    void getUsernameSuccessful() throws DataAccessException {
        AuthData auth = new AuthData("Elliot", "222");
        authDAO.createAuth(auth);

        assertNotNull(authDAO.getUsername("222"));
        assertEquals("Elliot", authDAO.getUsername("222"));
    }

    @Test
    void getUsernameDoesntExist() throws DataAccessException {
        AuthData auth = new AuthData("Elliot", "222");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () ->
                authDAO.getUsername("Boog"));
    }

    @Test
    void clearSuccessful() throws DataAccessException {
        AuthData auth1 = new AuthData("Brock", "111");
        AuthData auth2 = new AuthData("Misty", "555");
        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);
        authDAO.clear();

        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth("111"));
        assertThrows(DataAccessException.class, () ->
                authDAO.deleteAuth("555"));
    }
}
