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
        registerHandler = new RegisterHandler();
        loginHandler = new LoginHandler();
        logoutHandler = new LogoutHandler();
        authService = new AuthService(new AuthDAO());
        userService = new UserService(new UserDAO(), authService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public String register(String json) {
        try {
            RegisterRequest request = registerHandler.generateRegisterRequest(json);
            RegisterResult result = userService.register(request);
            return registerHandler.processRegisterResult(result);
        } catch (AlreadyExistsException e) {
            return "error";
        }
    }

    public String login(String json) {
        try {
            LoginRequest request = loginHandler.generateLoginRequest(json);
            LoginResult result = userService.login(request);
            return loginHandler.processLoginResult(result);
        } catch (DataAccessException e) {
            return "error";
        }
    }

    public String logout(String json) {
        try {
            LogoutRequest request = logoutHandler.generateLogoutRequest(json);
            LogoutResult result = authService.logout(request);
            return logoutHandler.processLogoutResult(result);
        } catch (DataAccessException e) {
            return "error";
        }
    }
}
