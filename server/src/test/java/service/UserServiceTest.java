package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private AuthService authService;

    @BeforeEach
    public void setup() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);

    }

    @Test
    public void testRegisterNewUser() throws DataAccessException {
        RegisterRequest testRequest = new RegisterRequest("Jeremy", "12345", "jeremy@emila.com");
        RegisterResult testResult = userService.register(testRequest);
        assertNotNull(testResult);
        assertEquals("Jeremy", testResult.username());
    }

    @Test
    public void testRegisterRepeatUser() throws DataAccessException {
        RegisterRequest testRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");
        RegisterResult testResult = userService.register(testRequest);

        RegisterRequest repeatRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(repeatRequest);
        });
    }
}
