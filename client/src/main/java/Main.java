import chess.*;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "https://localhost:8080";
        Repl repl = new Repl(serverUrl);
        repl.run();
    }
}