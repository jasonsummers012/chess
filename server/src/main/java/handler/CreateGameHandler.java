package handler;

import com.google.gson.Gson;
import handler.request.CreateGameRequest;
import handler.result.CreateGameResult;

public class CreateGameHandler {
    private final Gson gson;

    public CreateGameHandler() {
        gson = new Gson();
    }

    public CreateGameRequest generateCreateGameRequest(String json) {
        return gson.fromJson(json, CreateGameRequest.class);
    }

    public String processCreateGameResult(CreateGameResult result) {
        return gson.toJson(result);
    }
}