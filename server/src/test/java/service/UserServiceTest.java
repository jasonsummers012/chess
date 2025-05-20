package service;

import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {
    private UserDAO testDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        testDAO = new UserDAO();
        userService = new UserService(testDAO);
    }

}
