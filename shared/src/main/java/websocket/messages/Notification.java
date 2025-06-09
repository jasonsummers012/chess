package websocket.messages;

import com.google.gson.Gson;

public record Notification(Type type, String message) {
    public enum Type {

    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
