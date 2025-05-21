package service;

import dataaccess.*;
import handler.request.*;
import handler.result.*;
import model.AuthData;
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
    public void testGenerateToken() {
        RegisterRequest registerRequest = new RegisterRequest("Jeremy", "12345", "jeremy@email.com");
        RegisterResult registerResult = userService.register(registerRequest);

        assertNotNull(registerResult);
        assertEquals(registerResult.authToken(), authDAO.getAuth(registerResult.authToken()).authToken());
    }

    @Test
    public void testValidateToken() {
        RegisterRequest registerRequest = new RegisterRequest("Lebron", "bball", "lebron@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        String authToken = registerResult.authToken();

        assertNotNull(authDAO.getAuth(authToken));
        assertEquals("Lebron", authService.checkValidAuthToken(authToken));
    }

    @Test
    public void testInvalidAuthToken() throws UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("Lebron", "bball", "lebron@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);

        assertThrows(UnauthorizedException.class, () -> {
            authService.checkValidAuthToken("random");
        });
    }

    @Test
    public void testLogoutSuccessful() {
        RegisterRequest registerRequest = new RegisterRequest("Elliot", "010101", "elliot@yahoo.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Elliot", "010101");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        assertNotNull(authDAO.getAuth(authToken));
        assertEquals("Elliot", authDAO.getAuth(authToken).username());

        LogoutResult logoutResult = authService.logout(authToken);

        assertNotNull(logoutResult);
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    public void testLogoutFailure() throws UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("Elliot", "010101", "elliot@yahoo.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Elliot", "010101");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        assertNotNull(authDAO.getAuth(authToken));
        assertEquals("Elliot", authDAO.getAuth(authToken).username());

        assertThrows(UnauthorizedException.class, () -> {
            authService.logout("0");
        });
    }

    @Test
    public void testClear() {
        AuthData data = new AuthData("Mario", "2");
        authDAO.createAuth(data);
        authService.clear();

        assertNull(authDAO.getAuth("2"));
    }
}
