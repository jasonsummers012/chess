package server;

import dataaccess.*;
import handler.*;
import service.*;
import spark.*;
import websocket.WebSocketHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

import static spark.Spark.exception;

public class Server {
    RegisterHandler registerHandler;
    LoginHandler loginHandler;
    LogoutHandler logoutHandler;
    CreateGameHandler createGameHandler;
    ListGamesHandler listGamesHandler;
    JoinGameHandler joinGameHandler;
    ClearHandler clearHandler;
    UserService userService;
    AuthService authService;
    GameService gameService;
    AuthDAO authDAO;
    UserDAO userDAO;
    GameDAO gameDAO;

    public Server() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createAuthTable();
            DatabaseManager.createUserTable();
            DatabaseManager.createGameTable();

            authDAO = new SQLAuthDAO();
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database connection failed. Error: " + e.getMessage(), e);
        }
        authService = new AuthService(authDAO);
        userService = new UserService(userDAO, authService);
        gameService = new GameService(gameDAO, authDAO);
        registerHandler = new RegisterHandler(userService);
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler(authService);
        createGameHandler = new CreateGameHandler(gameService, authService);
        listGamesHandler = new ListGamesHandler(gameService, authService);
        joinGameHandler = new JoinGameHandler(gameService, authService);
        clearHandler = new ClearHandler(userService, gameService, authService);

        exception(BadRequestException.class, (exception, request, response) -> {
            response.status(400);
            response.type("application/json");
            response.body("{\"message\":\"Error: bad request\"}");
        });

        exception(UnauthorizedException.class, (exception, request, response) -> {
            response.status(401);
            response.type("application/json");
            response.body("{\"message\":\"Error: unauthorized\"}");
        });

        exception(AlreadyTakenException.class, (exception, request, response) -> {
            response.status(403);
            response.type("application/json");
            response.body("{\"message\":\"Error: already taken\"}");
        });

        exception(DataAccessException.class, (exception, request, response) -> {
            response.status(500);
            response.type("application/json");
            response.body("{\"message\":\"Error: " + exception.getMessage() + "\"}");
        });
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.post("/game", createGameHandler);
        Spark.get("/game", listGamesHandler);
        Spark.put("/game", joinGameHandler);
        Spark.delete("/db", clearHandler);

        ServiceLocator.provideGameService(gameService);
        ServiceLocator.provideAuthService(authService);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
