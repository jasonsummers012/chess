package server;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.RegisterHandler;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import service.UserService;
import spark.*;

public class Server {
    RegisterHandler registerHandler;
    UserService userService;

    public Server() {
        registerHandler = new RegisterHandler();
        userService = new UserService(new UserDAO());
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
        } catch (DataAccessException e) {
            return "error";
        }
    }
}
