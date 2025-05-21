package service;

import chess.ChessGame;
import dataaccess.*;
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
        nextID = 1;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws BadRequestException, AlreadyTakenException {
        if (request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        String gameName = request.gameName();
        if (gameDAO.getGame(gameName) != null) {
            throw new AlreadyTakenException("Error: already taken");
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

    public JoinGameResult joinGame(JoinGameRequest request, String authToken)
            throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        if (request.playerColor() == null || request.gameID() == 0) {
            throw new BadRequestException("Error: bad request");
        }

        int gameID = request.gameID();
        ChessGame.TeamColor color = request.playerColor();
        String username;
        try {
            username = authDAO.getUsername(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData game = gameDAO.getGameByID(gameID);
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (color != ChessGame.TeamColor.WHITE && color != ChessGame.TeamColor.BLACK) {
            throw new BadRequestException("Error: bad request");
        }

        if (gameDAO.checkColorOccupied(game, color)) {
            throw new AlreadyTakenException("Error: already taken");
        }

        gameDAO.join(game, color, username);
        return new JoinGameResult();
    }

    public void clear() {
        gameDAO.clear();
    }
}
