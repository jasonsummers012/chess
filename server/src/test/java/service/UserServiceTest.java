package service;

import dataaccess.*;
import handler.request.*;
import handler.result.*;
import model.UserData;
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
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);

    }

    @Test
    public void testRegisterNewUser() {
        RegisterRequest testRequest = new RegisterRequest("Jeremy", "12345", "jeremy@emila.com");
        RegisterResult testResult = userService.register(testRequest);

        assertNotNull(testResult);
        assertEquals("Jeremy", testResult.username());
    }

    @Test
    public void testRegisterRepeatUser() throws AlreadyTakenException {
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
    public void testLoginIncorrectPassword() throws UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("Shaw", "12345", "Shaw@gmail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Shaw", "00000");

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    public void testClear() {
        UserData user = new UserData("Buttered Beans", "pass123", "beans@hotmail.com");
        userDAO.createUser(user);
        userService.clear();

        assertNull(userDAO.getUser("Buttered Beans"));
    }
}
