package com.example.financeapp.controllers;

import com.example.financeapp.models.Budget;
import com.example.financeapp.models.BudgetManager;
import com.example.financeapp.models.Transaction;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.models.User;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import com.example.financeapp.util.CurrencyUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    public HBox bottomNav;
    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;
    @FXML private Label balanceLabel;

    @FXML private VBox budgetAlertsContainer;      // NEW: separate alerts section
    @FXML private VBox recentTransactionsContainer;
    @FXML private PieChart categoryChart;

    @FXML private Button addButton;

    private final TransactionManager transactionManager = new TransactionManager();
    private final BudgetManager budgetManager = new BudgetManager();

    // For expanding recent transactions
    private static final int RECENT_PAGE_SIZE = 5;
    private int recentLimit = RECENT_PAGE_SIZE;

    @FXML
    private void initialize() {

        if (!Session.isLoggedIn() || Session.getCurrentUser() == null) {
            SceneManager.switchTo("LoginView");
            return;
        }

        User currentUser = Session.getCurrentUser();
        System.out.println("Dashboard loaded for user: " + currentUser.getUsername()
                + " (" + currentUser.getEmail() + ")");

        if (budgetAlertsContainer != null) {
            budgetAlertsContainer.getChildren().clear();
        }
        recentTransactionsContainer.getChildren().clear();

        loadSummary(currentUser);
        loadCategoryChart(currentUser);
        loadAlerts(currentUser);
        loadRecentTransactions(currentUser);
    }

    // ================= SUMMARY =================
    private void loadSummary(User user) {
        YearMonth currentMonth = YearMonth.now();

        double income = transactionManager.getTotalIncomeForMonth(user.getId(), currentMonth);
        double expenses = transactionManager.getTotalExpenseForMonth(user.getId(), currentMonth); // negative
        double balance = income + expenses;

        String symbol = CurrencyUtil.getSymbol(user.getCurrencyCode());

        incomeLabel.setText(String.format("%s%.2f", symbol, income));

        double expenseAbs = Math.abs(expenses);
        expenseLabel.setText(String.format("-%s%.2f", symbol, expenseAbs));

        balanceLabel.setText(String.format("%s%.2f", symbol, balance));
    }

    // ================= CATEGORY PIE CHART (colors via CSS) =================
    private void loadCategoryChart(User user) {
        categoryChart.getData().clear();

        YearMonth currentMonth = YearMonth.now();

        Map<String, Double> categoryTotals =
                transactionManager.getCategoryTotalsForMonth(user.getId(), currentMonth, false);

        if (categoryTotals.isEmpty()) {
            categoryChart.setTitle("No expenses this month");
            return;
        }

        categoryChart.setTitle("Expenses by Category");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double value = Math.abs(entry.getValue());
            PieChart.Data slice = new PieChart.Data(category, value);
            categoryChart.getData().add(slice);
        }

        // Attach CSS classes like pie-shopping, pie-food, etc.
        Platform.runLater(() -> {
            for (PieChart.Data data : categoryChart.getData()) {
                if (data.getNode() != null) {
                    String cssClass = "pie-" + data.getName()
                            .toLowerCase()
                            .replaceAll("[^a-z0-9]+", "-");
                    data.getNode().getStyleClass().add(cssClass);
                }
            }
        });
    }

    // ================= BUDGET ALERTS (separate container) =================
    private void loadAlerts(User user) {
        if (budgetAlertsContainer == null) return;
        budgetAlertsContainer.getChildren().clear();

        List<Budget> budgets = budgetManager.getBudgetsForUser(user.getId());
        System.out.println("[Alerts] Budgets for user " + user.getId() + ": " + budgets.size());
        if (budgets.isEmpty()) return;

        YearMonth currentMonth = YearMonth.now();

        // Category totals for this month (from TransactionManager)
        Map<String, Double> rawTotals =
                transactionManager.getCategoryTotalsForMonth(user.getId(), currentMonth, false);

        // Normalize category keys to lowercase for case-insensitive match
        Map<String, Double> expenseTotals = new HashMap<>();
        for (Map.Entry<String, Double> e : rawTotals.entrySet()) {
            String key = e.getKey() == null ? "" : e.getKey().toLowerCase();
            double value = Math.abs(e.getValue());
            expenseTotals.put(key, value);
            System.out.println("[Alerts] Category total: '" + e.getKey() + "' = " + value);
        }

        boolean anyAlert = false;

        for (Budget b : budgets) {
            double limit = b.getMonthlyLimit();
            if (limit <= 0) {
                System.out.println("[Alerts] Budget " + b.getCategory() + " has non-positive limit, skipping");
                continue;
            }

            String catKey = b.getCategory() == null ? "" : b.getCategory().toLowerCase();
            double spent = expenseTotals.getOrDefault(catKey, 0.0);
            System.out.println("[Alerts] Budget " + b.getCategory()
                    + " limit=" + limit + " spent=" + spent);

            if (spent <= 0) continue;

            double ratio = spent / limit;
            if (ratio >= 0.8) {
                if (!anyAlert) {
                    Label header = new Label("Budget Alerts");
                    header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #b34700;");
                    budgetAlertsContainer.getChildren().add(header);
                    anyAlert = true;
                }

                String msg;
                if (ratio >= 1.0) {
                    msg = String.format(
                            "You exceeded your %s budget: spent $%.2f of $%.2f.",
                            b.getCategory(), spent, limit
                    );
                } else {
                    msg = String.format(
                            "You used %.0f%% of your %s budget: spent $%.2f of $%.2f.",
                            ratio * 100, b.getCategory(), spent, limit
                    );
                }

                Label alertLabel = new Label(msg);
                alertLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #cc6600;");
                budgetAlertsContainer.getChildren().add(alertLabel);
            }
        }
    }

    // ================= RECENT TRANSACTIONS (with "Show more") =================
    private void loadRecentTransactions(User user) {
        recentTransactionsContainer.getChildren().clear();

        List<Transaction> recent = transactionManager.getRecentTransactions(user.getId(), recentLimit);

        if (recent.isEmpty()) {
            Label empty = new Label("No recent transactions.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            recentTransactionsContainer.getChildren().add(empty);
            return;
        }

        for (Transaction t : recent) {
            addTransactionEntry(t);
        }

        // If we hit the limit, there might be more: show a "Show more…" link
        if (recent.size() == recentLimit) {
            Hyperlink showMore = new Hyperlink("Show more...");
            showMore.setStyle("-fx-font-size: 12px;");
            showMore.setOnAction(e -> {
                recentLimit += RECENT_PAGE_SIZE;
                loadRecentTransactions(user);
            });
            recentTransactionsContainer.getChildren().add(showMore);
        }
    }

    private void addTransactionEntry(Transaction t) {
        boolean isIncome = t.isIncome();

        String titleText = (t.getTitle() == null || t.getTitle().isBlank())
                ? t.getCategory()
                : t.getTitle();

        User user = Session.getCurrentUser();
        String symbol = CurrencyUtil.getSymbol(user != null ? user.getCurrencyCode() : "USD");

        double amountAbs = Math.abs(t.getAmount());
        String amountText = String.format("%s%s%.2f",
                isIncome ? "+" : "-",
                symbol,
                amountAbs);

        Label label = new Label(
                String.format("%s   %s   (%s)", titleText, amountText, t.getDate())
        );

        // Income green, expenses red
        String style = "-fx-font-size: 14px;" +
                (isIncome ? " -fx-text-fill: green;" : " -fx-text-fill: red;");
        label.setStyle(style);

        recentTransactionsContainer.getChildren().add(label);
    }
}