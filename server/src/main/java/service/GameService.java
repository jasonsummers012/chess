package service;

import chess.ChessGame;
import dataaccess.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        String gameName = request.gameName();
        if (gameDAO.getGame(gameName) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, gameName, chessGame);
        int generatedID = gameDAO.createGame(game);

        return new CreateGameResult(generatedID);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        return new ListGamesResult(games);
    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken)
            throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        if (request.gameID() < 1) {
            throw new BadRequestException("Error: bad request");
        }

        int gameID = request.gameID();
        ChessGame.TeamColor color = request.playerColor();

        if (color == null && !request.observer()) {
            throw new BadRequestException("Error: bad request");
        }

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

        if (request.observer()) {
            return new JoinGameResult();
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

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGameByID(gameID);
    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException {
        gameDAO.updateGame(gameID, newGame);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
