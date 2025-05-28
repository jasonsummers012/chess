package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @AfterEach
    void clearTable() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void createUserSuccessful() throws DataAccessException {
        UserData user = new UserData("Godot", "coffee", "godot@gmail.com");
        userDAO.createUser(user);

        assertNotNull(userDAO.getUser("Godot"));
        assertEquals("Godot", userDAO.getUser("Godot").username());
        assertEquals("coffee", userDAO.getUser("Godot").password());
    }

    @Test
    void createDuplicateUser() throws DataAccessException {
        UserData user1 = new UserData("Buddy", "456", "Buddy@yahoo.com");
        UserData user2 = new UserData("Buddy", "333", "Buddy@yahoo.com");
        userDAO.createUser(user1);

        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(user2));
    }

    @Test
    void getUserSuccessful() throws DataAccessException {
        UserData user = new UserData("Edgeworth", "objection!", "miles@hotmail.com");
        userDAO.createUser(user);

        assertNotNull(userDAO.getUser("Edgeworth"));
        assertEquals(user, userDAO.getUser("Edgeworth"));
    }

    @Test
    void getUserDoesntExist() throws DataAccessException {
        userDAO.clear();
        UserData user = new UserData("Wright", "objection!", "miles@hotmail.com");
        userDAO.createUser(user);

        assertNull(userDAO.getUser("Edgeworth"));
    }

    @Test
    void clearSuccessful() throws DataAccessException {
        UserData user1 = new UserData("Simon", "silence!", "blackquill@gmail.com");
        UserData user2 = new UserData("Athena", "widget", "cykes@gmail.com");
        userDAO.createUser(user1);
        userDAO.createUser(user2);
        userDAO.clear();

        assertNull(userDAO.getUser("Simon"));
        assertNull(userDAO.getUser("Athena"));
    }
}
