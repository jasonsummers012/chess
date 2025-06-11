package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = getInititialConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    static public void createAuthTable() throws DataAccessException {
        var statement = "CREATE TABLE IF NOT EXISTS authTable (" +
                "username VARCHAR(255) NOT NULL," +
                "authToken VARCHAR(255) NOT NULL," +
                "PRIMARY KEY (authToken))";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create table", ex);
        }
    }

    static public void createUserTable() throws DataAccessException {
        var statement = "CREATE TABLE IF NOT EXISTS userTable (" +
                "username VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL," +
                "PRIMARY KEY (username))";
        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create table", ex);
        }
    }

    static public void createGameTable() throws DataAccessException {
        var statement = "CREATE TABLE IF NOT EXISTS gameTable (" +
                "gameID INT NOT NULL AUTO_INCREMENT," +
                "whiteUsername VARCHAR(255)," +
                "blackUsername VARCHAR(255)," +
                "gameName VARCHAR(255) NOT NULL UNIQUE," +
                "game VARCHAR(12000) NOT NULL," +
                "gameOver BOOLEAN DEFAULT FALSE, " +
                "PRIMARY KEY (gameID))";

        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create table", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    public static Connection getInititialConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection for creation");
        }
    }
}
