package dementiev_a.data.manager;

import dementiev_a.io.IO;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresManager implements DatabaseManager {
    @Getter(lazy = true)
    private static final PostgresManager instance = new PostgresManager();

    private Connection connection;

    private PostgresManager() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/memorable_dates", "postgres", "qwerty123"
            );
            connection.createStatement()
                    .execute(
                            "CREATE TABLE IF NOT EXISTS events (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "name VARCHAR(64), " +
                                    "description VARCHAR(1024), " +
                                    "date DATE" +
                                    ");" +
                                 "CREATE TABLE IF NOT EXISTS celebrations (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "name VARCHAR(64), " +
                                    "description VARCHAR(1024), " +
                                    "date DATE, " +
                                    "place VARCHAR(64)" +
                                    ");" +
                                 "CREATE TABLE IF NOT EXISTS events_celebrations (" +
                                    "celebrationId INTEGER PRIMARY KEY, " +
                                    "eventId INTEGER" +
                                    ");"
                    );
        } catch (SQLException e) {
            IO.printError("Error while connecting to database and preparing tables");
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
