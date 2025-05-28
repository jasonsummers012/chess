package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;

import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @AfterEach
    void reset() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    void createAuthSuccessful() throws DataAccessException {
        AuthData auth = new AuthData("Jeremy", "123");
        authDAO.createAuth(auth);
        AuthData current = authDAO.getAuth("123");

        assertNotNull(current);
        assertEquals("Jeremy", current.username());
        assertEquals("123", current.authToken());
    }

    @Test
    void createDuplicateAuth() throws DataAccessException {
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

        assertNull(authDAO.getAuth("445"));
    }

    @Test
    void deleteAuthSuccessful() throws DataAccessException {
        AuthData auth = new AuthData("Boog", "234");
        authDAO.createAuth(auth);
        authDAO.deleteAuth(auth.authToken());

        assertNull(authDAO.getAuth("234"));
    }

    @Test
    void deleteAuthDoesntExist() throws DataAccessException {
        AuthData auth = new AuthData("Boog", "345");
        authDAO.deleteAuth(auth.authToken());

        assertNull(authDAO.getAuth("345"));
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

        assertNull(authDAO.getUsername("Boog"));
    }

    @Test
    void clearSuccessful() throws DataAccessException {
        AuthData auth1 = new AuthData("Brock", "111");
        AuthData auth2 = new AuthData("Misty", "555");
        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);
        authDAO.clear();

        assertNull(authDAO.getAuth("111"));
        assertNull(authDAO.getAuth("555"));
    }
}
