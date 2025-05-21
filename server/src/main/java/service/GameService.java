package service;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import handler.request.CreateGameRequest;
import handler.request.JoinGameRequest;
import handler.request.ListGamesRequest;
import handler.result.CreateGameResult;
import handler.result.JoinGameResult;
import handler.result.ListGamesResult;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private int nextID;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        nextID = 0;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws AlreadyTakenException {
        String gameName = request.gameName();
        if (gameDAO.getGame(gameName) != null) {
            throw new AlreadyTakenException("Error: Game name already exists");
        }

        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(nextID, null, null, gameName, chessGame);
        gameDAO.createGame(game);
        nextID ++;

        int gameID = game.gameID();
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(ListGamesRequest request) {
        List<GameData> games = gameDAO.getAllGames();
        return new ListGamesResult(games);
    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws AlreadyTakenException, DataAccessException{
        int gameID = request.gameID();
        String color = request.playerColor();
        String username = authDAO.getUsername(authToken);

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new DataAccessException("Error: Invalid color");
        }

        GameData game = gameDAO.getGameByID(gameID);
        if (gameDAO.checkColorOccupied(game, color)) {
            throw new AlreadyTakenException("Error: color already taken");
        }

        gameDAO.join(game, color, username);
        return new JoinGameResult();
    }

    public void clear() {
        gameDAO.clear();
    }
}
