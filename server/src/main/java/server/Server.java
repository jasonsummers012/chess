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
    UserService userService;
    AuthService authService;

    public Server() {
        authService = new AuthService(new AuthDAO());
        userService = new UserService(new UserDAO(), authService);
        registerHandler = new RegisterHandler(userService);
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler(authService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);

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
