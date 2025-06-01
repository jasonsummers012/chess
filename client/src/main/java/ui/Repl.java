package ui;

import java.util.Scanner;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl, null);
    }

    public void run() {
        System.out.println(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
        }
    }
}
