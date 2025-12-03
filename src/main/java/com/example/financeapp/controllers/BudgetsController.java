package com.example.financeapp.controllers;

import com.example.financeapp.models.Budget;
import com.example.financeapp.models.BudgetManager;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.models.User;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.YearMonth;
import java.util.List;

public class BudgetsController {

    @FXML private TableView<Budget> budgetsTable;
    @FXML private TableColumn<Budget, String> categoryColumn;
    @FXML private TableColumn<Budget, Double> limitColumn;

    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField limitField;
    @FXML private Button saveButton;

    @FXML private Label infoLabel;

    private final BudgetManager budgetManager = new BudgetManager();
    private final TransactionManager transactionManager = new TransactionManager();

    private User currentUser;

    @FXML
    private void initialize() {

        if (!Session.isLoggedIn() || Session.getCurrentUser() == null) {
            SceneManager.switchTo("LoginView");
            return;
        }
        currentUser = Session.getCurrentUser();

        categoryColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

        limitColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getMonthlyLimit()));

        categoryCombo.getItems().addAll(
                "Food", "Transportation", "Salary", "Entertainment",
                "Shopping", "Bills", "Health", "Other"
        );

        loadBudgets();

        budgetsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        categoryCombo.setValue(newSel.getCategory());
                        limitField.setText(String.valueOf(newSel.getMonthlyLimit()));
                    }
                }
        );

        saveButton.setOnAction(e -> handleSave());
    }

    private void loadBudgets() {
        List<Budget> list = budgetManager.getBudgetsForUser(currentUser.getId());
        ObservableList<Budget> observable = FXCollections.observableArrayList(list);
        budgetsTable.setItems(observable);
        infoLabel.setText("Loaded " + list.size() + " budgets.");
    }

    private void handleSave() {
        String category = categoryCombo.getValue();
        String limitText = limitField.getText().trim();

        if (category == null || category.isEmpty()) {
            showError("Please select a category.");
            return;
        }

        if (limitText.isEmpty()) {
            showError("Please enter a monthly limit.");
            return;
        }

        double limit;
        try {
            limit = Double.parseDouble(limitText);
        } catch (NumberFormatException e) {
            showError("Monthly limit must be a valid number.");
            return;
        }

        if (limit <= 0) {
            showError("Monthly limit must be greater than 0.");
            return;
        }

        boolean ok = budgetManager.setBudget(currentUser.getId(), category, limit);
        if (!ok) {
            showError("Could not save budget. Please try again.");
            return;
        }

        YearMonth currentMonth = YearMonth.now();
        double spent = transactionManager
                .getCategoryTotalsForMonth(currentUser.getId(), currentMonth, false)
                .getOrDefault(category, 0.0);

        String msg = String.format(
                "Saved budget for %s: $%.2f (spent $%.2f this month).",
                category, limit, spent
        );

        showInfo(msg);
        loadBudgets();
    }

    private void showError(String msg) {
        infoLabel.setText(msg);
        infoLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    }

    private void showInfo(String msg) {
        infoLabel.setText(msg);
        infoLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px;");
    }
}