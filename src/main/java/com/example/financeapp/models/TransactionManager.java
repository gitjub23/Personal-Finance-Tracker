package com.example.financeapp.models;

import com.example.financeapp.database.Database;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionManager {

    public TransactionManager() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                title TEXT,
                amount REAL NOT NULL,
                is_income INTEGER NOT NULL,
                category TEXT NOT NULL,
                payment_method TEXT,
                notes TEXT,
                recurring INTEGER DEFAULT 0,
                recurrence_rule TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
            """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            // Migration: add title column if the table existed without it
            try (Statement alter = conn.createStatement()) {
                alter.execute("ALTER TABLE transactions ADD COLUMN title TEXT");
            } catch (SQLException e) {
                // Ignore if column already exists
                if (!e.getMessage().toLowerCase().contains("duplicate column name")) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // CRUD
    // ==========================

    public int addTransaction(Transaction t) {
        String sql = """
            INSERT INTO transactions
            (user_id, date, title, amount, is_income, category, payment_method, notes, recurring, recurrence_rule)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getDate().toString());
            ps.setString(3, t.getTitle());
            ps.setDouble(4, t.getAmount());
            ps.setInt(5, t.isIncome() ? 1 : 0);
            ps.setString(6, t.getCategory());
            ps.setString(7, t.getPaymentMethod());
            ps.setString(8, t.getNotes());
            ps.setInt(9, t.isRecurring() ? 1 : 0);
            ps.setString(10, t.getRecurrenceRule());

            int affected = ps.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    t.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateTransaction(Transaction t) {
        String sql = """
            UPDATE transactions
            SET date = ?, title = ?, amount = ?, is_income = ?, category = ?, payment_method = ?,
                notes = ?, recurring = ?, recurrence_rule = ?
            WHERE id = ? AND user_id = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getDate().toString());
            ps.setString(2, t.getTitle());
            ps.setDouble(3, t.getAmount());
            ps.setInt(4, t.isIncome() ? 1 : 0);
            ps.setString(5, t.getCategory());
            ps.setString(6, t.getPaymentMethod());
            ps.setString(7, t.getNotes());
            ps.setInt(8, t.isRecurring() ? 1 : 0);
            ps.setString(9, t.getRecurrenceRule());
            ps.setInt(10, t.getId());
            ps.setInt(11, t.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTransaction(int id, int userId) {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    // Queries for UI
    // ==========================

    public List<Transaction> getTransactionsForUser(int userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC, id DESC";
        List<Transaction> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Transaction> getRecentTransactions(int userId, int limit) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC, id DESC LIMIT ?";
        List<Transaction> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalIncomeForMonth(int userId, YearMonth month) {
        return getTotalForMonth(userId, month, true);
    }

    public double getTotalExpenseForMonth(int userId, YearMonth month) {
        return getTotalForMonth(userId, month, false);
    }

    private double getTotalForMonth(int userId, YearMonth month, boolean income) {
        String sql = """
                SELECT SUM(amount) AS total
                FROM transactions
                WHERE user_id = ?
                  AND is_income = ?
                  AND date >= ?
                  AND date <  ?
                """;

        LocalDate start = month.atDay(1);
        LocalDate endExclusive = month.plusMonths(1).atDay(1);

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, income ? 1 : 0);
            ps.setString(3, start.toString());
            ps.setString(4, endExclusive.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Map<String, Double> getCategoryTotalsForMonth(int userId, YearMonth month, boolean income) {
        String sql = """
                SELECT category, SUM(amount) AS total
                FROM transactions
                WHERE user_id = ?
                  AND is_income = ?
                  AND date >= ?
                  AND date <  ?
                GROUP BY category
                """;

        LocalDate start = month.atDay(1);
        LocalDate endExclusive = month.plusMonths(1).atDay(1);

        Map<String, Double> result = new HashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, income ? 1 : 0);
            ps.setString(3, start.toString());
            ps.setString(4, endExclusive.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // ==========================
    // Helper
    // ==========================

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setDate(LocalDate.parse(rs.getString("date")));
        t.setTitle(rs.getString("title"));      // 👈 add this
        t.setAmount(rs.getDouble("amount"));
        t.setIncome(rs.getInt("is_income") == 1);
        t.setCategory(rs.getString("category"));
        t.setPaymentMethod(rs.getString("payment_method"));
        t.setNotes(rs.getString("notes"));
        t.setRecurring(rs.getInt("recurring") == 1);
        t.setRecurrenceRule(rs.getString("recurrence_rule"));
        return t;
    }
}