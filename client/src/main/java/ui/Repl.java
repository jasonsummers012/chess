package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private State state = State.LOGGEDOUT;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl, this);
        postLoginClient = new PostLoginClient(serverUrl, this, null);
    }

    public void run() {
        System.out.println(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state == State.LOGGEDOUT) {
                    result = preLoginClient.eval(line);
                } else {
                    result = postLoginClient.eval(line);
                }
                System.out.print(SET_TEXT_COLOR_GREEN + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n");
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        state = newState;
    }
}
