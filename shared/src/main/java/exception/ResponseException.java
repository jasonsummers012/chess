package exception;

public class ResponseException extends RuntimeException {
    public ResponseException(int i, String message) {
        super(message);
    }
}
