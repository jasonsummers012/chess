package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.request.*;
import handler.result.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private AuthService authService;
    private UserService userService;

    @BeforeEach
    public void setup() {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);
    }

    @Test
    public void testGenerateToken() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");
        RegisterResult registerResult = userService.register(registerRequest);

        assertNotNull(registerResult);
        assertEquals(registerResult.authToken(), authDAO.getAuth(registerResult.authToken()).authToken());
    }

    @Test
    public void testLogoutSuccessful() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Elliot", "010101", "elliot@yahoo.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Elliot", "010101");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        assertNotNull(authDAO.getAuth(authToken));
        assertEquals("Elliot", authDAO.getAuth(authToken).username());

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult logoutResult = authService.logout(logoutRequest);

        assertNotNull(logoutResult);
        assertNull(authDAO.getAuth(authToken));
    }
}
