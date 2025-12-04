package com.example.financeapp.controllers;

import com.example.financeapp.models.Categories;
import com.example.financeapp.models.Transaction;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.models.User;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import com.example.financeapp.session.TransactionEditContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class AddTransactionController {

    @FXML private TextField titleField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextArea notesField;

    @FXML private RadioButton incomeRadio;
    @FXML private RadioButton expenseRadio;
    @FXML private ToggleGroup typeGroup;

    private final TransactionManager transactionManager = new TransactionManager();
    private Transaction editingTransaction;

    @FXML
    private void initialize() {
        // Categories
        categoryCombo.getItems().setAll(Categories.getDefaultExpenseCategories());

        // Default date
        if (datePicker.getValue() == null) {
            datePicker.setValue(LocalDate.now());
        }

        // ----- EDIT MODE? -----
        editingTransaction = TransactionEditContext.getEditingTransaction();

        if (editingTransaction != null) {
            // Populate fields from existing transaction
            try {
                // If your Transaction has getTitle():
                String title = (String) Transaction.class
                        .getMethod("getTitle")
                        .invoke(editingTransaction);
                titleField.setText(title);
            } catch (Exception ignore) {
                // no title on model; safe to ignore
            }

            amountField.setText(String.valueOf(Math.abs(editingTransaction.getAmount())));
            if (editingTransaction.isIncome()) {
                incomeRadio.setSelected(true);
            } else {
                expenseRadio.setSelected(true);
            }

            categoryCombo.setValue(editingTransaction.getCategory());
            datePicker.setValue(editingTransaction.getDate());
            notesField.setText(editingTransaction.getNotes());

        } else {
            // Create mode
            incomeRadio.setSelected(true);
        }
    }

    // ====================== SAVE TRANSACTION ======================
    @FXML
    private void onSave() {
        try {
            // Validation
            if (amountField.getText().isBlank()
                    || categoryCombo.getValue() == null
                    || datePicker.getValue() == null) {

                showAlert("Please fill out all required fields.");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Amount must be a number.");
                return;
            }

            boolean isIncome = incomeRadio.isSelected();
            if (!isIncome) {
                amount = -Math.abs(amount); // expenses stored as negative
            }

            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showAlert("No logged-in user. Please log in again.");
                SceneManager.switchTo("LoginView");
                return;
            }

            if (editingTransaction == null) {
                // ===== CREATE NEW =====
                Transaction t = new Transaction();
                t.setUserId(currentUser.getId());
                t.setDate(datePicker.getValue());
                t.setAmount(amount);
                t.setIncome(isIncome);
                t.setCategory(categoryCombo.getValue());
                t.setPaymentMethod(null);
                t.setNotes(notesField.getText());
                t.setRecurring(false);
                t.setRecurrenceRule(null);
                try {
                    Transaction.class.getMethod("setTitle", String.class)
                            .invoke(t, titleField.getText());
                } catch (Exception ignore) {
                    // no title in model
                }

                int id = transactionManager.addTransaction(t);
                if (id <= 0) {
                    showAlert("Failed to save transaction. Please try again.");
                    return;
                }
            } else {
                // ===== UPDATE EXISTING =====
                editingTransaction.setDate(datePicker.getValue());
                editingTransaction.setAmount(amount);
                editingTransaction.setIncome(isIncome);
                editingTransaction.setCategory(categoryCombo.getValue());
                editingTransaction.setNotes(notesField.getText());
                try {
                    Transaction.class.getMethod("setTitle", String.class)
                            .invoke(editingTransaction, titleField.getText());
                } catch (Exception ignore) {
                    // no title in model
                }

                boolean ok = transactionManager.updateTransaction(editingTransaction);
                if (!ok) {
                    showAlert("Failed to update transaction. Please try again.");
                    return;
                }
            }

            // Clear edit context and go back
            TransactionEditContext.clear();
            SceneManager.switchTo("TransactionsList");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Unexpected error while saving:\n" + ex.getMessage());
        }
    }

    // ====================== CANCEL BUTTON ======================
    @FXML
    private void onCancel() {
        TransactionEditContext.clear();
        SceneManager.switchTo("TransactionsList");
    }

    // ====================== ALERT HELPER ======================
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}