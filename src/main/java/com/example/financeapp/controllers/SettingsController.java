package com.example.financeapp.controllers;

import com.example.financeapp.models.*;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SettingsController {

    // Account
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private Button updateAccountButton;

    // Change password
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changePasswordButton;

    // Existing settings
    @FXML private ComboBox<String> currencyCombo;
    @FXML private TextField goalField;
    @FXML private Button saveButton;
    @FXML private Button logoutButton;
    @FXML private Button deleteAccountButton;

    @FXML private ComboBox<String> budgetCategoryBox;
    @FXML private TextField budgetLimitField;
    @FXML private Button addBudgetButton;
    @FXML private VBox budgetListContainer;

    @FXML private Label infoLabel;

    private final BudgetManager budgetManager = new BudgetManager();
    private final UserManager userManager = new UserManager();
    private User currentUser;

    @FXML
    private void initialize() {

        if (!Session.isLoggedIn() || Session.getCurrentUser() == null) {
            SceneManager.switchTo("LoginView");
            return;
        }

        currentUser = Session.getCurrentUser();

        // Prefill account info
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());

        // Currency choices
        currencyCombo.getItems().addAll("USD", "EUR", "GBP", "HUF", "JPY");
        currencyCombo.setValue(currentUser.getCurrencyCode());

        // Goal
        goalField.setText(currentUser.getGoal());

        // Wire buttons
        saveButton.setOnAction(e -> saveSettings());
        logoutButton.setOnAction(e -> logout());
        deleteAccountButton.setOnAction(e -> handleDeleteAccount());
        updateAccountButton.setOnAction(e -> handleUpdateAccount());
        changePasswordButton.setOnAction(e -> handleChangePassword());

        loadBudgetCategories();
        loadBudgets();

        addBudgetButton.setOnAction(e -> handleAddOrUpdateBudget());
    }

    private void loadBudgetCategories() {
        budgetCategoryBox.getItems().setAll(Categories.getDefaultExpenseCategories());
    }

    private void handleAddOrUpdateBudget() {
        String category = budgetCategoryBox.getValue();
        String limitText = budgetLimitField.getText();

        if (category == null || limitText == null || limitText.isBlank()) {
            showError("Please choose a category and enter a limit.");
            return;
        }

        double limit;
        try {
            limit = Double.parseDouble(limitText);
            if (limit < 0) {
                showError("Budget must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Invalid budget amount.");
            return;
        }

        User user = Session.getCurrentUser();
        if (user == null) {
            showError("No logged-in user.");
            return;
        }

        // See if there is already a budget for this category
        List<Budget> budgets = budgetManager.getBudgetsForUser(user.getId());
        Budget existing = budgets.stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            // Create new
            budgetManager.addBudget(user.getId(), category, limit);
        } else {
            // Update existing
            existing.setMonthlyLimit(limit);
            budgetManager.updateBudget(existing);   // make sure this method exists
        }

        budgetLimitField.clear();
        loadBudgets();
    }

    private void loadBudgets() {
        budgetListContainer.getChildren().clear();

        User user = Session.getCurrentUser();
        if (user == null) return;

        List<Budget> budgets = budgetManager.getBudgetsForUser(user.getId());
        if (budgets.isEmpty()) {
            Label empty = new Label("No budgets set yet.");
            empty.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            budgetListContainer.getChildren().add(empty);
            return;
        }

        for (Budget b : budgets) {
            budgetListContainer.getChildren().add(createBudgetRow(b));
        }
    }

    private javafx.scene.Node createBudgetRow(Budget budget) {
        HBox row = new HBox(10);
        row.setFillHeight(true);

        Label categoryLabel = new Label(budget.getCategory());
        categoryLabel.setMinWidth(100);

        TextField limitField = new TextField(String.format("%.2f", budget.getMonthlyLimit()));
        limitField.setPrefWidth(80);

        Button saveBtn = new Button("Save");
        Button deleteBtn = new Button("Delete");

        saveBtn.setOnAction(e -> handleSaveBudget(budget, limitField));
        deleteBtn.setOnAction(e -> handleDeleteBudget(budget));

        row.getChildren().addAll(categoryLabel, limitField, saveBtn, deleteBtn);
        return row;
    }

    private void handleSaveBudget(Budget budget, TextField limitField) {
        String text = limitField.getText();
        if (text == null || text.isBlank()) {
            showError("Please enter a budget amount.");
            return;
        }

        double limit;
        try {
            limit = Double.parseDouble(text);
            if (limit < 0) {
                showError("Budget must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Invalid budget amount.");
            return;
        }

        budget.setMonthlyLimit(limit);
        budgetManager.updateBudget(budget);   // make sure this exists
        loadBudgets();
    }

    private void handleDeleteBudget(Budget budget) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete budget?");
        confirm.setContentText("This will remove the budget for " + budget.getCategory() + ".");
        confirm.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                budgetManager.deleteBudget(budget.getId());  // ensure this exists
                loadBudgets();
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== Currency + goal =====
    private void saveSettings() {
        String currency = currencyCombo.getValue();
        String goal = goalField.getText().trim();

        boolean ok1 = userManager.updateCurrency(currentUser.getId(), currency);
        boolean ok2 = userManager.updateGoal(currentUser.getId(), goal);

        if (ok1 || ok2) {
            currentUser.setCurrencyCode(currency);
            currentUser.setGoal(goal);
            setInfo("Settings saved successfully!", false);
        } else {
            setInfo("Failed to save settings.", true);
        }
    }

    // ===== Update username + email =====
    private void handleUpdateAccount() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newUsername.isEmpty()) {
            setInfo("Username cannot be empty.", true);
            return;
        }
        if (newUsername.length() < 3) {
            setInfo("Username should be at least 3 characters.", true);
            return;
        }

        if (newEmail.isEmpty()) {
            setInfo("Email cannot be empty.", true);
            return;
        }
        if (!newEmail.contains("@") || !newEmail.contains(".")) {
            setInfo("Please enter a valid email address.", true);
            return;
        }

        boolean okUser = true;
        boolean okEmail = true;

        // Only hit DB if value actually changed (avoids UNIQUE issues if unchanged)
        if (!newUsername.equals(currentUser.getUsername())) {
            okUser = userManager.updateUsername(currentUser.getId(), newUsername);
        }

        if (!newEmail.equals(currentUser.getEmail())) {
            okEmail = userManager.updateEmail(currentUser.getId(), newEmail);
        }

        if (!okUser || !okEmail) {
            String msg = !okUser && !okEmail
                    ? "Could not update username or email (they may already be in use)."
                    : !okUser
                    ? "Could not update username (it may already be in use)."
                    : "Could not update email (it may already be in use).";
            setInfo(msg, true);
            return;
        }

        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        setInfo("Account details updated.", false);
    }

    // ===== Change password =====
    private void handleChangePassword() {
        String currentPw = currentPasswordField.getText();
        String newPw = newPasswordField.getText();
        String confirmPw = confirmPasswordField.getText();

        if (currentPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            setInfo("Please fill in all password fields.", true);
            return;
        }

        if (newPw.length() < 6) {
            setInfo("New password should be at least 6 characters.", true);
            return;
        }

        if (!newPw.equals(confirmPw)) {
            setInfo("New passwords do not match.", true);
            return;
        }

        // Verify current password using login (by email or username)
        if (userManager.login(currentUser.getEmail(), currentPw) == null &&
                userManager.login(currentUser.getUsername(), currentPw) == null) {
            setInfo("Current password is incorrect.", true);
            return;
        }

        boolean ok = userManager.updatePassword(currentUser.getId(), newPw);
        if (!ok) {
            setInfo("Failed to change password. Please try again.", true);
            return;
        }

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        setInfo("Password changed successfully.", false);
    }

    // ===== Delete account =====
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText(
                "This will delete your account and all associated data (transactions, budgets). " +
                        "This action cannot be undone.");

        confirm.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);
        confirm.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                boolean ok = userManager.deleteUser(currentUser.getId());
                if (ok) {
                    Session.logout();
                    SceneManager.clearCache();
                    SceneManager.switchTo("LoginView");
                } else {
                    setInfo("Failed to delete account. Please try again.", true);
                }
            }
        });
    }

    // ===== Logout =====
    private void logout() {
        Session.logout();
        SceneManager.clearCache();
        SceneManager.switchTo("LoginView");
    }

    // ===== Helper =====
    private void setInfo(String msg, boolean error) {
        infoLabel.setText(msg);
        infoLabel.setStyle(error
                ? "-fx-text-fill: red; -fx-font-size: 12px;"
                : "-fx-text-fill: #2e7d32; -fx-font-size: 12px;");
    }
}