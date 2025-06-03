package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import request.*;
import result.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) {
        var path = "/user";
        RegisterResult result = makeRequest("POST", path, request, RegisterResult.class);
        if (result != null && result.authToken() != null) {
            this.authToken = result.authToken();
        }
        return result;
    }

    public LoginResult login(LoginRequest request) {
        var path = "/session";
        LoginResult result = makeRequest("POST", path, request, LoginResult.class);
        if (result != null && result.authToken() != null) {
            this.authToken = result.authToken();
        }
        return result;
    }

    public LogoutResult logout(LogoutRequest request) {
        var path = "/session";
        LogoutResult result = makeRequest("DELETE", path, request, LogoutResult.class);
        this.authToken = null;
        return result;
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        var path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest request) {
        var path = "/game";
        return this.makeRequest("GET", path, request, ListGamesResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request) {
        var path = "/game";
        return this.makeRequest("PUT", path, request, JoinGameResult.class);
    }

    public JoinGameResult observeGame(JoinGameRequest request) {
        var path = "/game";
        return this.makeRequest("PUT", path, request, JoinGameResult.class);
    }

    public void clear() {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            if (!method.equals("GET")) {
                http.setDoOutput(true);
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            String errorMessage = "HTTP error: " + status;

            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    String serverResponse = new String(respErr.readAllBytes(), StandardCharsets.UTF_8);
                    String parsedMessage = parseErrorMessage(serverResponse);
                    if (parsedMessage != null && !parsedMessage.isEmpty()) {
                        errorMessage = parsedMessage;
                    }
                }
            }

            throw new ResponseException(status, errorMessage);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class <T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private String parseErrorMessage(String response) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response, JsonObject.class);

            if (json.has("message")) {
                return json.get("message").getAsString();
            } else if (json.has("error")) {
                return json.get("error").getAsString();
            } else if (json.has("description")) {
                return json.get("description").getAsString();
            }
        } catch (Exception e) {
            return response;
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
