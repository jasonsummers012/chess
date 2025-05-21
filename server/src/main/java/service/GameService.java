package service;

import chess.ChessGame;
import dataaccess.AlreadyExistsException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import handler.request.CreateGameRequest;
import handler.result.CreateGameResult;
import model.GameData;

public class GameService {
    private final GameDAO gameDAO;
    private int nextID;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        nextID = 0;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws AlreadyExistsException {
        String gameName = request.gameName();
        if (gameDAO.getGame(gameName) != null) {
            throw new AlreadyExistsException("Error: Game name already exists");
        }

        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(nextID, null, null, gameName, chessGame);
        gameDAO.createGame(game);
        nextID ++;

        int gameID = game.gameID();
        return new CreateGameResult(gameID);
    }
}
