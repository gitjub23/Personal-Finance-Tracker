package com.example.financeapp.models;

import com.example.financeapp.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetManager {

    public BudgetManager() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    monthly_limit REAL NOT NULL,
                    UNIQUE(user_id, category),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Legacy "upsert" method kept for compatibility.
     * If a budget exists for (userId, category), update it; otherwise insert it.
     */
    public boolean setBudget(int userId, String category, double monthlyLimit) {
        // Try update first; if nothing updated, insert.
        List<Budget> budgets = getBudgetsForUser(userId);
        Budget existing = budgets.stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setMonthlyLimit(monthlyLimit);
            return updateBudget(existing);
        } else {
            return addBudget(userId, category, monthlyLimit);
        }
    }

    /**
     * Insert a new budget for the given user/category.
     * Will fail (return false) if a budget for that (user, category) already exists
     * due to the UNIQUE(user_id, category) constraint.
     */
    public boolean addBudget(int userId, String category, double monthlyLimit) {
        String insert = "INSERT INTO budgets(user_id, category, monthly_limit) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement psIns = conn.prepareStatement(insert)) {

            psIns.setInt(1, userId);
            psIns.setString(2, category);
            psIns.setDouble(3, monthlyLimit);

            return psIns.executeUpdate() > 0;

        } catch (SQLException e) {
            // Most likely a UNIQUE constraint violation if duplicate category
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing budget row (by id).
     * Updates the category and monthly_limit for safety.
     */
    public boolean updateBudget(Budget budget) {
        String update = "UPDATE budgets SET category = ?, monthly_limit = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement psUpd = conn.prepareStatement(update)) {

            psUpd.setString(1, budget.getCategory());
            psUpd.setDouble(2, budget.getMonthlyLimit());
            psUpd.setInt(3, budget.getId());

            return psUpd.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a budget by its id.
     */
    public boolean deleteBudget(int budgetId) {
        String delete = "DELETE FROM budgets WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement psDel = conn.prepareStatement(delete)) {

            psDel.setInt(1, budgetId);
            return psDel.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Budget> getBudgetsForUser(int userId) {
        String sql = "SELECT * FROM budgets WHERE user_id = ?";
        List<Budget> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Budget b = new Budget();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setCategory(rs.getString("category"));
                b.setMonthlyLimit(rs.getDouble("monthly_limit"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}