package service;

import chess.ChessGame;
import dataaccess.*;
import handler.request.*;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    public void testCreateNewGame() throws DataAccessException {
        CreateGameRequest testRequest = new CreateGameRequest("Boog");
        gameService.createGame(testRequest);

        assertNotNull(gameDAO.getGame("Boog"));
        assertEquals(1, gameDAO.getGame("Boog").gameID());
    }

    @Test
    public void testCreateRepeatGame() throws AlreadyTakenException {
        CreateGameRequest testRequest = new CreateGameRequest("Boog");
        gameService.createGame(testRequest);

        CreateGameRequest repeatGameRequest = new CreateGameRequest("Boog");

        assertThrows(AlreadyTakenException.class, () -> {
            gameService.createGame(repeatGameRequest);
        });
    }

    @Test
    public void testListGames() {
        CreateGameRequest request1 = new CreateGameRequest("Shrek");
        gameService.createGame(request1);
        GameData game1 = gameDAO.getGame("Shrek");

        CreateGameRequest request2 = new CreateGameRequest("Donkey");
        gameService.createGame(request2);
        GameData game2 = gameDAO.getGame("Donkey");

        List<GameData> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        ListGamesRequest listGamesRequest = new ListGamesRequest();

        assertEquals(gameService.listGames(listGamesRequest).games(), games);
    }

    @Test
    public void testReturnEmptyGameList() {
        ListGamesRequest listGamesRequest = new ListGamesRequest();
        assertTrue(gameService.listGames(listGamesRequest).games().isEmpty());
    }

    @Test
    public void testJoinGame() throws AlreadyTakenException, DataAccessException {
        CreateGameRequest request1 = new CreateGameRequest("Wright");
        gameService.createGame(request1);
        GameData game1 = gameDAO.getGame("Wright");

        CreateGameRequest request2 = new CreateGameRequest("Edgeworth");
        gameService.createGame(request2);
        GameData game2 = gameDAO.getGame("Edgeworth");

        List<GameData> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        AuthData data = new AuthData("Oldbag", "12");
        authDAO.createAuth(data);
        gameService.joinGame(joinGameRequest, "12");

        GameData expectedGame = new GameData(game1.gameID(), "Oldbag", null, "Wright", game1.game());
        assertEquals(expectedGame, gameDAO.getGame("Wright"));
    }

    @Test
    public void testJoinOccupiedGame() throws AlreadyTakenException, DataAccessException {
        CreateGameRequest request1 = new CreateGameRequest("Wright");
        gameService.createGame(request1);

        CreateGameRequest request2 = new CreateGameRequest("Edgeworth");
        gameService.createGame(request2);

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        AuthData data1 = new AuthData("Oldbag", "12");
        authDAO.createAuth(data1);
        gameService.joinGame(joinGameRequest1, "12");

        JoinGameRequest joinGameRequest2 = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        AuthData data2 = new AuthData("Godot", "13");
        authDAO.createAuth(data2);

        assertThrows(AlreadyTakenException.class, () ->{
            gameService.joinGame(joinGameRequest2, "13");
        });
    }

    @Test
    public void testClear() {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(123, "Wario", "Waluigi", "Smash Bros", chessGame);
        gameDAO.createGame(game);
        gameService.clear();

        assertNull(gameDAO.getGame("Smash Bros"));
    }
}
