package ui;

import client.PostLoginClient;
import client.PreLoginClient;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private State state = State.LOGGEDOUT;
    private final ServerFacade server;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
        preLoginClient = new PreLoginClient(serverUrl, this, server);
        postLoginClient = new PostLoginClient(serverUrl, this, null, server);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_GREEN + preLoginClient.help() + RESET_TEXT_COLOR);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state == State.LOGGEDOUT) {
                    result = preLoginClient.eval(line);
                    if (state == State.LOGGEDIN) {
                        postLoginClient.setVisitorName(preLoginClient.getVisitorName());
                    }
                } else {
                    result = postLoginClient.eval(line);
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n");
    }

    public void setState(State newState) {
        state = newState;
    }

    public String getPreLoginHelp() {
        return preLoginClient.help();
    }

    public String getPostLoginHelp() {
        return postLoginClient.help();
    }

    public PreLoginClient getPreLoginClient() {
        return preLoginClient;
    }

    public PostLoginClient getPostLoginClient() {
        return postLoginClient;
    }
}
