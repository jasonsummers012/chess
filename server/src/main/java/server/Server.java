package server;

import dataaccess.AuthDAO;
import dataaccess.*;
import handler.*;
import handler.request.*;
import handler.result.*;
import service.*;
import spark.*;

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

    public Server() {
        authService = new AuthService(new AuthDAO());
        userService = new UserService(new UserDAO(), authService);
        gameService = new GameService(new GameDAO(), new AuthDAO());
        registerHandler = new RegisterHandler(userService);
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler(authService);
        createGameHandler = new CreateGameHandler(gameService, authService);
        listGamesHandler = new ListGamesHandler(gameService, authService);
        joinGameHandler = new JoinGameHandler(gameService, authService);
        clearHandler = new ClearHandler(userService, gameService, authService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.post("/game", createGameHandler);
        Spark.get("/game", listGamesHandler);
        Spark.put("/game", joinGameHandler);
        Spark.delete("/db", clearHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
