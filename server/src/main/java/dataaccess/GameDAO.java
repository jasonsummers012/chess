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

    public List<GameData> getAllGames() {
        List<GameData> allGames = new ArrayList<>();
        for (GameData game : games.values()) {
            allGames.add(game);
        }
        return allGames;
    }
}
