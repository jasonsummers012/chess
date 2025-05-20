package dataaccess;

import java.util.UUID;

public class AuthDAO {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
