package service;

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
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    public void testCreateNewGame() throws DataAccessException {
        CreateGameRequest testRequest = new CreateGameRequest("Boog");
        gameService.createGame(testRequest);

        assertNotNull(gameDAO.getGame("Boog"));
        assertEquals(0, gameDAO.getGame("Boog").gameID());
    }

    @Test
    public void testCreateRepeatGame() throws AlreadyExistsException {
        CreateGameRequest testRequest = new CreateGameRequest("Boog");
        gameService.createGame(testRequest);

        CreateGameRequest repeatGameRequest = new CreateGameRequest("Boog");

        assertThrows(AlreadyExistsException.class, () -> {
            gameService.createGame(repeatGameRequest);
        });
    }

    @Test
    public void testListGames() throws DataAccessException {
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
    public void testJoinGame() throws DataAccessException, AlreadyExistsException {
        CreateGameRequest request1 = new CreateGameRequest("Wright");
        gameService.createGame(request1);
        GameData game1 = gameDAO.getGame("Wright");

        CreateGameRequest request2 = new CreateGameRequest("Edgeworth");
        gameService.createGame(request2);
        GameData game2 = gameDAO.getGame("Edgeworth");

        List<GameData> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 0);
        AuthData data = new AuthData("Oldbag", "12");
        authDAO.createAuth(data);
        gameService.joinGame(joinGameRequest, "12");

        GameData expectedGame = new GameData(game1.gameID(), "Oldbag", null, "Wright", game1.game());
        assertEquals(expectedGame, gameDAO.getGame("Wright"));
    }

    @Test
    public void testJoinOccupiedGame() throws AlreadyExistsException, DataAccessException {
        CreateGameRequest request1 = new CreateGameRequest("Wright");
        gameService.createGame(request1);
        GameData game1 = gameDAO.getGame("Wright");

        CreateGameRequest request2 = new CreateGameRequest("Edgeworth");
        gameService.createGame(request2);
        GameData game2 = gameDAO.getGame("Edgeworth");

        List<GameData> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        JoinGameRequest joinGameRequest1 = new JoinGameRequest("WHITE", 0);
        AuthData data1 = new AuthData("Oldbag", "12");
        authDAO.createAuth(data1);
        gameService.joinGame(joinGameRequest1, "12");

        JoinGameRequest joinGameRequest2 = new JoinGameRequest("WHITE", 0);
        AuthData data2 = new AuthData("Godot", "13");
        authDAO.createAuth(data2);

        assertThrows(AlreadyExistsException.class, () ->{
            gameService.joinGame(joinGameRequest2, "13");
        });
    }
}
