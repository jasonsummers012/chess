package websocket.messages;

import com.google.gson.Gson;

public record Action(Type type, String playerName) {
    public enum Type {
        JOIN,
        LEAVE
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
