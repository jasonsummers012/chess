package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @AfterEach
    void reset() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    void createGameSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(32, "Tony", "Steve", "Avengers", chessGame);
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame("Avengers"));
        assertEquals("Steve", gameDAO.getGame("Avengers").blackUsername());
    }

    @Test
    void getGameSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(1, "Apollo", "Phoenix", "Ace Attorney", chessGame);
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame("Ace Attorney"));
        assertEquals(game, gameDAO.getGame("Ace Attorney"));
    }

    @Test
    void getGameDoesntExist() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(1, "Apollo", "Phoenix", "Ace Attorney", chessGame);
        gameDAO.createGame(game);

        assertNull(gameDAO.getGame("Avengers"));
    }

    @Test
    void getAllGamesSuccessful() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(1, "Birch", "May", "Hoenn", chessGame2);
        GameData game2 = new GameData(2, "Oak", "Blue", "Kanto", chessGame1);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        List<GameData> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        assertNotNull(gameDAO.getAllGames());
        assertEquals(games, gameDAO.getAllGames());
    }

    @Test
    void getAllGamesEmpty() throws DataAccessException {
        assertTrue(gameDAO.getAllGames().isEmpty());
    }

    @Test
    void colorOccupiedSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(35, "Mario", null, "Mario Bros", chessGame);
        gameDAO.createGame(game);

        assertFalse(gameDAO.checkColorOccupied(game, ChessGame.TeamColor.BLACK));
    }

    @Test
    void colorOccupiedDoesntExist() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(1, "Mario", null, "Mario Bros", chessGame);
        assertFalse(gameDAO.checkColorOccupied(game, ChessGame.TeamColor.WHITE));
    }

    @Test
    void joinSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(1, "Mario", null, "Mario Bros", chessGame);
        gameDAO.createGame(game);
        GameData newGame = gameDAO.join(game, ChessGame.TeamColor.BLACK, "Luigi");

        assertEquals("Luigi", newGame.blackUsername());
        assertEquals(new GameData(1, "Mario", "Luigi", "Mario Bros", chessGame), newGame);
    }

    @Test
    void joinOccupiedGame() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(35, "Mario", "Luigi", "Mario Bros", chessGame);
        gameDAO.createGame(game);

        assertThrows(DataAccessException.class, () ->
                gameDAO.join(game, ChessGame.TeamColor.BLACK, "Wario"));
    }

    @Test
    void clearSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(35, "Mario", "Luigi", "Mario Bros", chessGame);
        gameDAO.createGame(game);
        gameDAO.clear();

        assertNull(gameDAO.getGame("Mario Bros"));
    }
}
