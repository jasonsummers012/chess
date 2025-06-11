package ui;

import chess.ChessGame;
import client.GameplayClient;
import client.PostLoginClient;
import client.PreLoginClient;
import model.GameData;
import server.ServerFacade;
import websocket.ClientNotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private GameplayClient gameplayClient;
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
                        postLoginClient.setPlayerName(preLoginClient.getPlayerName());
                    }
                } else if (state == State.LOGGEDIN) {
                    result = postLoginClient.eval(line);
                } else {
                    result = gameplayClient.eval(line);
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

    public void enterGameplay(int gameID, ChessGame.TeamColor playerColor, boolean observer) {
        try {
            if (gameplayClient != null) {
                gameplayClient.leave();
            }
            GameData gameData = server.getGame(gameID);
            if (gameData == null) {
                throw new Exception("Game " + gameID + "not found");
            }

            ChessGame initialGame = gameData.game();
            if (initialGame == null) {
                throw new Exception("Game " + gameID + " has no chess game data");
            }

            String authToken = postLoginClient.getAuthToken();

            GameplayClient gameplayClient = new GameplayClient(
                    server.getServerUrl(),
                    this,
                    authToken,
                    gameID,
                    playerColor,
                    initialGame
            );
            gameplayClient.setNotificationHandler(new ClientNotificationHandler(gameplayClient));
            this.gameplayClient = gameplayClient;
        } catch (Exception e) {
            System.err.println("Error: unable to enter game: " + e.getMessage());
        }
    }
}
