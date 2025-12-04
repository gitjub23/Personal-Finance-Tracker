package com.example.financeapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    // Default DB file
    private static String url = "jdbc:sqlite:financeapp.db";

    static {
        try {
            // Ensure the SQLite JDBC driver loads
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches the app to a dedicated test database.
     * Called in unit tests (e.g. @BeforeAll).
     */
    public static void useTestDatabase() {
        url = "jdbc:sqlite:test_financeapp.db";
        System.out.println("[Database] Switched to TEST database: test_financeapp.db");
    }

    /**
     * Allows selecting a different DB file dynamically.
     * Useful for migrations or multiple profiles.
     */
    public static void useDatabaseFile(String filename) {
        url = "jdbc:sqlite:" + filename;
        System.out.println("[Database] Switched database file to: " + filename);
    }

    /**
     * Returns a connection to whichever DB file is currently active.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}