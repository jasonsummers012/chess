package service;

import chess.ChessGame;
import dataaccess.AlreadyExistsException;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.request.*;
import handler.result.*;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameDAO = new GameDAO();
        gameService = new GameService(gameDAO);
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
}
