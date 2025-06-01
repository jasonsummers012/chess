package ui;

import exception.ResponseException;
import request.*;
import result.*;
import server.*;

import java.util.Arrays;

public class PreLoginClient {
    private final Repl repl;
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl, Repl repl) {
        this.repl = repl;
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterRequest request = new RegisterRequest(username, password, email);
            RegisterResult result = server.register(request);

            repl.setState(State.LOGGEDIN);
            visitorName = username;
            return String.format("You registered as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = server.login(request);

            repl.setState(State.LOGGEDIN);
            visitorName = username;
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String quit(String... params) {
        return "QUIT";
    }

    public String help() {
        return """
                register <username> <password> <email>   Register a new account
                login <username> <password>              Log in to your account
                quit                                     Exit
                help                                     Show possible commands
                """;
    }

    public String getVisitorName() {
        return visitorName;
    }
}
