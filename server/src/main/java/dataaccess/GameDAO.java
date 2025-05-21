package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public GameData getGame(String gameName) {
        for (GameData game : games.values()) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    public GameData getGameByID(int gameID) {
        for (GameData game : games.values()) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public List<GameData> getAllGames() {
        List<GameData> allGames = new ArrayList<>();
        allGames.addAll(games.values());
        return allGames;
    }

    public boolean checkColorOccupied(GameData game, String color) {
        if (color.equals("WHITE")) {
            return games.get(game.gameID()).whiteUsername() != null;
        } else {
            return games.get(game.gameID()).blackUsername() != null;
        }
    }

    public GameData join(GameData game, String color, String username) {
        GameData newGameData;
        if (color.equals("WHITE")) {
            newGameData = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            games.put(game.gameID(), newGameData);
        } else {
            newGameData = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            games.put(game.gameID(), newGameData);
        }
        return newGameData;
    }

    public void clear() {
        games.clear();
    }
}
