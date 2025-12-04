package com.example.financeapp.models;

import com.example.financeapp.database.Database;

import java.sql.*;

/**
 * Handles all database operations related to users:
 *  - create users table (if not exists)
 *  - register new user
 *  - login existing user
 *  - update goal / currency
 *  - load user by id
 */
public class UserManager {

    public UserManager() {
        createTableIfNotExists();
    }

    // ==========================
    // TABLE SETUP
    // ==========================

    private void createTableIfNotExists() {
        String createSql = """
             CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL COLLATE NOCASE UNIQUE,
                email TEXT NOT NULL COLLATE NOCASE UNIQUE,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                goal TEXT,
                currency_code TEXT NOT NULL DEFAULT 'USD'
             );
         """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create table if it doesn't exist at all
            stmt.execute(createSql);

            // Ensure currency_code column exists (for older DBs)
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN currency_code TEXT NOT NULL DEFAULT 'USD';");
                System.out.println("Added currency_code column to users table.");
            } catch (SQLException e) {
                // If column already exists, SQLite will throw an error; we can safely ignore it
                if (!e.getMessage().toLowerCase().contains("duplicate column")
                        && !e.getMessage().toLowerCase().contains("already exists")) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // REGISTER
    // ==========================

    /**
     * Registers a new user.
     *
     * @param username display/username (we used full name in SignUpController)
     * @param email    user email (must be unique)
     * @param password raw password (will be hashed)
     * @param goal     optional financial goal text
     * @return true if user created, false otherwise
     */
    public boolean register(String username, String email, String password, String goal) {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(password, salt);
        String defaultCurrency = "USD";

        String sql = """
        INSERT INTO users(username, email, password_hash, salt, goal, currency_code)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, salt);
            ps.setString(5, goal);
            ps.setString(6, defaultCurrency);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            // UNIQUE constraint is a normal "user already exists" case; no need for full stacktrace.
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Register error: username or email already exists (" + e.getMessage() + ")");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // ==========================
    // LOGIN
    // ==========================

    /**
     * Attempts to login by username or email.
     *
     * @param usernameOrEmail username OR email
     * @param password        raw password
     * @return User object if successful, null otherwise
     */
    public User login(String usernameOrEmail, String password) {
        String sql = "SELECT * FROM users " +
                "WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String salt = rs.getString("salt");
                String hash = rs.getString("password_hash");

                if (PasswordUtils.verifyPassword(password, salt, hash)) {
                    return mapRowToUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    // ==========================
    // PROFILE UPDATES
    // ==========================

    public boolean updateGoal(int userId, String goal) {
        String sql = "UPDATE users SET goal = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, goal);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCurrency(int userId, String currencyCode) {
        String sql = "UPDATE users SET currency_code = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currencyCode);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    // LOAD BY ID
    // ==========================

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==========================
    // LOAD BY EMAIL (for Forgot Password)
    // ==========================

    // ==========================
    // LOAD BY EMAIL OR USERNAME (for Forgot Password)
    // ==========================
    public User getUserByEmailOrUsername(String input) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?) OR LOWER(username) = LOWER(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, input);
            ps.setString(2, input);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Resets password for the user with the given email or username.
     * Returns true if a user was found and updated, false otherwise.
     */
    public boolean resetPassword(String emailOrUsername, String newPassword) {
        User user = getUserByEmailOrUsername(emailOrUsername.trim());
        if (user == null) {
            System.out.println("resetPassword: no user found for input = " + emailOrUsername);
            return false;
        }
        return updatePassword(user.getId(), newPassword);
    }

    // ==========================
    // HELPER
    // ==========================

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setGoal(rs.getString("goal"));
        user.setCurrencyCode(rs.getString("currency_code"));
        return user;
    }

    // ==========================
    // UPDATE EMAIL
    // ==========================
    public boolean updateEmail(int userId, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newEmail);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Will fail here if email is already taken because of UNIQUE constraint
            e.printStackTrace();
            return false;
        }
    }

    // ==========================
    // UPDATE PASSWORD
    // ==========================
    public boolean updatePassword(int userId, String newPassword) {
        String newSalt = PasswordUtils.generateSalt();
        String newHash = PasswordUtils.hashPassword(newPassword, newSalt);

        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setString(2, newSalt);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================
    // UPDATE USERNAME
    // ==========================
    public boolean updateUsername(int userId, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Will fail if username already exists (UNIQUE constraint)
            e.printStackTrace();
            return false;
        }
    }

    // ==========================
    // DELETE USER (and their data)
    // ==========================
    public boolean deleteUser(int userId) {
        String deleteTransactions = "DELETE FROM transactions WHERE user_id = ?";
        String deleteBudgets = "DELETE FROM budgets WHERE user_id = ?";
        String deleteUser = "DELETE FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(deleteTransactions);
                 PreparedStatement ps2 = conn.prepareStatement(deleteBudgets);
                 PreparedStatement ps3 = conn.prepareStatement(deleteUser)) {

                ps1.setInt(1, userId);
                ps1.executeUpdate();

                ps2.setInt(1, userId);
                ps2.executeUpdate();

                ps3.setInt(1, userId);
                int rows = ps3.executeUpdate();

                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // In UserManager

    public User getOrCreateOAuthUser(String email, String displayName) {
        // 1) Try existing user
        User existing = getUserByEmailOrUsername(email);
        if (existing != null) {
            return existing;
        }

        // 2) Otherwise auto-register one
        String randomPassword = java.util.UUID.randomUUID().toString(); // never used directly
        boolean ok = register(displayName, email, randomPassword, null);

        if (!ok) {
            return null;
        }

        // 3) Load and return the newly created user
        return getUserByEmailOrUsername(email);
    }
}