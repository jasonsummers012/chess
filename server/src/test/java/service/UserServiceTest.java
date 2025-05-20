package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {
    private UserDAO testDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        testDAO = new UserDAO();
        userService = new UserService(testDAO);
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
