package client;

import exception.ResponseException;
import request.*;
import result.*;
import server.*;
import ui.Repl;
import ui.State;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginClient {
    private final Repl repl;
    private String playerName = null;
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl, Repl repl, ServerFacade server) {
        this.repl = repl;
        this.server = server;
        this.serverUrl = serverUrl;
        this.playerName = null;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
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
            playerName = username;

            repl.getPostLoginClient().setAuthToken(result.authToken());

            return String.format("You registered as %s.\n\n%s", playerName, repl.getPostLoginHelp());
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
            playerName = username;
            return String.format("You signed in as %s.\n\n%s", playerName, repl.getPostLoginHelp());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String quit() {
        return "quit";
    }

    public String help() {
        return SET_TEXT_COLOR_GREEN + """
                register <username> <password> <email>   Register a new account
                login <username> <password>              Log in to your account
                quit                                     Exit
                help                                     Show possible commands
                """ + RESET_TEXT_COLOR;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void clearPlayerName() {
        this.playerName = null;
    }
}

