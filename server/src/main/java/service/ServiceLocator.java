package service;

public class ServiceLocator {
    private static GameService gameService;
    private static AuthService authService;

    public static void provideGameService(GameService service) {
        gameService = service;
    }

    public static GameService getGameService() {
        return gameService;
    }

    public static void provideAuthService(AuthService service) {
        authService = service;
    }

    public static AuthService getAuthService() {
        return authService;
    }
}
