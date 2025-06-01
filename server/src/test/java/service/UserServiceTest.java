package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private AuthService authService;

    @BeforeEach
    public void setup() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);
    }

    @AfterEach
    public void reset() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void testRegisterNewUser() throws DataAccessException {
        RegisterRequest testRequest = new RegisterRequest("Jeremy", "12345", "jeremy@emila.com");
        RegisterResult testResult = userService.register(testRequest);

        assertNotNull(testResult);
        assertEquals("Jeremy", testResult.username());
    }

    @Test
    public void testRegisterRepeatUser() throws AlreadyTakenException, DataAccessException {
        RegisterRequest testRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");
        userService.register(testRequest);

        RegisterRequest repeatRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");

        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(repeatRequest);
        });
    }

    @Test
    public void testLoginSuccessful() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Shaw", "12345", "Shaw@gmail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Shaw", "12345");
        LoginResult loginResult = userService.login(loginRequest);

        assertNotNull(loginResult);
        assertEquals("Shaw", loginResult.username());
        assertEquals("12345", loginRequest.password());
    }

    @Test
    public void testLoginIncorrectPassword() throws UnauthorizedException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Shaw", "12345", "Shaw@gmail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Shaw", "00000");

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    public void testClear() throws DataAccessException {
        UserData user = new UserData("Buttered Beans", "pass123", "beans@hotmail.com");
        userDAO.createUser(user);
        userService.clear();

        assertNull(userDAO.getUser("Buttered Beans"));
    }
}
