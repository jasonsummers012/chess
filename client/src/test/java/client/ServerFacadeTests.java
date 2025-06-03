package client;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import server.Server;
import server.ServerFacade;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static int port;
    private static ServerFacade facade;
    private final static HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + port + "/db"))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterAll
    static void stopServer() {

        server.stop();
    }

    @Test
    public void registerSuccessful() {
        RegisterRequest request = new RegisterRequest("Jeremy", "password", "email");
        var authData = facade.register(request);

        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerRepeat() {
        RegisterRequest request = new RegisterRequest("Jeremy", "password", "email");
        RegisterRequest repeatRequest = new RegisterRequest("Jeremy", "password", "email");
        facade.register(request);

        assertThrows(ResponseException.class, () ->
            facade.register(repeatRequest));
    }

    @Test
    public void loginSuccessful() {
        RegisterRequest registerRequest = new RegisterRequest("Jeff", "password", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeff", "password");

        var loginResult = facade.login(loginRequest);
        assertNotNull(loginResult);
    }

    @Test
    public void loginDoesntExist() {
        LoginRequest request = new LoginRequest("Jeff", "password");

        assertThrows(ResponseException.class, () ->
                facade.login(request));
    }

    @Test
    public void logoutSuccessful() {
        RegisterRequest registerRequest = new RegisterRequest("Phoenix", "password", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Phoenix", "password");
        facade.login(loginRequest);

        assertNotNull(facade.getAuthToken());

        LogoutRequest logoutRequest = new LogoutRequest();

        assertNotNull(facade.logout(logoutRequest));
        assertNull(facade.getAuthToken());
    }

    @Test
    public void logoutNotLoggedIn() {
        LogoutRequest request = new LogoutRequest();
        assertThrows(ResponseException.class, () ->
                facade.logout(request));
    }

    @Test
    public void createGameSuccessful() {
        RegisterRequest registerRequest = new RegisterRequest("Edgeworth", "password", "email");
        facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("coolgame");

        assertNotNull(facade.createGame(createGameRequest));
    }

    @Test
    public void createRepeatGame() {
        RegisterRequest registerRequest = new RegisterRequest("Edgeworth", "password", "email");
        facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("coolgame");
        facade.createGame(createGameRequest);

        assertThrows(ResponseException.class, () ->
                facade.createGame(createGameRequest));
    }

    @Test
    public void listGamesSuccessful() {
        RegisterRequest registerRequest = new RegisterRequest("Godot", "password", "email");
        facade.register(registerRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest();

        assertTrue(facade.listGames(listGamesRequest).games().isEmpty());

        CreateGameRequest createGameRequest = new CreateGameRequest("GodotGame");
        facade.createGame(createGameRequest);

        assertFalse(facade.listGames(listGamesRequest).games().isEmpty());
    }

    @Test
    public void listGamesNotLoggedIn() {
        ListGamesRequest request = new ListGamesRequest();

        assertThrows(ResponseException.class, () ->
                facade.listGames(request));
    }

    @Test
    public void joinGameSuccessful() {
        RegisterRequest registerRequest = new RegisterRequest("Brock", "password", "email");
        facade.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("1stGym");
        facade.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);

        assertDoesNotThrow(() -> facade.joinGame(joinGameRequest));
    }

    @Test
    public void joinGameOccupied() {
        RegisterRequest registerRequest = new RegisterRequest("Brock", "password", "email");
        facade.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("1stGym");
        facade.createGame(createGameRequest);

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        facade.joinGame(joinGameRequest1);

        JoinGameRequest joinGameRequest2 = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);

        assertThrows(ResponseException.class, () ->
                facade.joinGame(joinGameRequest2));
    }

    @Test
    public void joinGameObserver() {
        RegisterRequest registerRequest = new RegisterRequest("Brock", "password", "email");
        facade.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("1stGym");
        facade.createGame(createGameRequest);

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        facade.joinGame(joinGameRequest1);

        JoinGameRequest joinAsObserverRequest = new JoinGameRequest(null, 1);

        assertDoesNotThrow(() -> facade.joinGame(joinAsObserverRequest));
    }
}
