package server;

import com.google.gson.Gson;
import exception.ResponseException;
import request.*;
import result.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) {
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request) {
        var path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
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

            writeBody(request, http);
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
                    errorMessage = new String(respErr.readAllBytes(), StandardCharsets.UTF_8);
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

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
