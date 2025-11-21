package com.example.financeapp.controllers;

import com.example.financeapp.models.Transaction;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.navigation.SceneManager;

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

    @FXML
    private void initialize() {
        categoryCombo.getItems().addAll(
                "Food", "Transportation", "Salary", "Entertainment",
                "Shopping", "Bills", "Health", "Other"
        );

        incomeRadio.setSelected(true); // default
    }

    // ====================== SAVE TRANSACTION ======================
    @FXML
    private void onSave() {

        // ---------- VALIDATION ----------
        if (titleField.getText().isEmpty() ||
                amountField.getText().isEmpty() ||
                categoryCombo.getValue() == null ||
                datePicker.getValue() == null) {

            showAlert("Please fill out all fields.");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (Exception e) {
            showAlert("Amount must be a number.");
            return;
        }

        boolean isIncome = incomeRadio.isSelected();
        if (!isIncome) amount = -Math.abs(amount); // expenses stored as negative


        // ---------- CREATE TRANSACTION ----------
        Transaction transaction = new Transaction(
                titleField.getText(),
                amount,
                categoryCombo.getValue(),
                datePicker.getValue(),
                notesField.getText()
        );

        // ---------- SAVE ----------
        TransactionManager.addTransaction(transaction);

        // ---------- GO BACK TO TRANSACTIONS LIST ----------
        SceneManager.switchTo("TransactionsList");
    }


    // ====================== CANCEL BUTTON ======================
    @FXML
    private void onCancel() {
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