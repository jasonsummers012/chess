package service;

import chess.ChessGame;
import dataaccess.AlreadyExistsException;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import handler.request.*;
import handler.result.*;
import model.GameData;
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
}
