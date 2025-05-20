package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.request.LoginRequest;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
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
        RegisterRequest testRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");
        RegisterResult testResult = userService.register(testRequest);
        assertNotNull(testResult);
        assertEquals(testResult.authToken(), authDAO.getAuth(testResult.authToken()).authToken());
    }
}
