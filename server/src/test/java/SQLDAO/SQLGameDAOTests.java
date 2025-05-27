package SQLDAO;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLGameDAOTests {
    private Connection conn;
    private SQLGameDAO gameDAO;

    @BeforeEach
    void setup() throws SQLException {
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/testdb",
                "root",
                "SAirplane12#"
        );

        try (var statement = conn.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS gameTable (" +
                    "gameName VARCHAR(255) PRIMARY KEY," +
                    "whiteUsername VARCHAR(255)," +
                    "blackUsername VARCHAR(255)," +
                    "gameID INT NOT NULL," +
                    "game VARCHAR(12000) NOT NULL)"
            );
        }
        gameDAO = new SQLGameDAO();
    }

    @AfterEach
    void clearTable() throws SQLException {
        try (var statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM gameTable");
        }
    }

    @AfterAll
    void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    void createGameSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(32, "Tony", "Steve", "Avengers", chessGame);
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame("Avengers"));
        assertEquals(32, gameDAO.getGame("Avengers").gameID());
        assertEquals("Steve", gameDAO.getGame("Avengers").blackUsername());
    }

    @Test
    void createGameAlreadyExists() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game1 = new GameData(32, "Tony", "Steve", "Avengers", chessGame);
        GameData game2 = new GameData(33, "Thor", "Bruce", "Avengers", chessGame);
        gameDAO.createGame(game1);

        assertThrows(DataAccessException.class, () ->
                gameDAO.createGame(game2));
    }

    @Test
    void getGameSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "Apollo", "Phoenix", "Ace Attorney", chessGame);
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame("Ace Attorney"));
        assertNotNull(gameDAO.getGameByID(12));
        assertEquals(game, gameDAO.getGame("Ace Attorney"));
        assertEquals(chessGame, gameDAO.getGameByID(12).game());
    }

    @Test
    void getGameDoesntExist() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "Apollo", "Phoenix", "Ace Attorney", chessGame);
        gameDAO.createGame(game);

        assertThrows(DataAccessException.class, () ->
                gameDAO.getGame("Avengers"));
        assertThrows(DataAccessException.class, () ->
                gameDAO.getGameByID(23));
    }

    @Test
    void getAllGamesSuccessful() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(3, "Birch", "May", "Hoenn", chessGame2);
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

        assertTrue(gameDAO.checkColorOccupied(game, ChessGame.TeamColor.WHITE));
        assertFalse(gameDAO.checkColorOccupied(game, ChessGame.TeamColor.BLACK));
    }

    @Test
    void colorOccupiedDoesntExist() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(35, "Mario", null, "Mario Bros", chessGame);
        assertThrows(DataAccessException.class, () ->
                gameDAO.checkColorOccupied(game, ChessGame.TeamColor.WHITE));
    }

    @Test
    void joinSuccessful() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(35, "Mario", null, "Mario Bros", chessGame);
        gameDAO.createGame(game);
        GameData newGame = gameDAO.join(game, ChessGame.TeamColor.BLACK, "Luigi");

        assertEquals("Luigi", newGame.blackUsername());
        assertEquals(new GameData(35, "Mario", "Luigi", "Mario Bros", chessGame), newGame);
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

        assertThrows(DataAccessException.class, () ->
                gameDAO.getGame("Mario Bros"));
    }
}
