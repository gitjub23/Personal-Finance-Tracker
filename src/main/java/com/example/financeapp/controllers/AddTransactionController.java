package com.example.financeapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    private void onSave() {
        String title = titleField.getText();
        String amount = amountField.getText();
        String category = categoryCombo.getValue();
        String type = ((RadioButton) typeGroup.getSelectedToggle()).getText();
        String notes = notesField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";

        System.out.println("Transaction Saved:");
        System.out.println(title + " | " + amount + " | " + type + " | " + category + " | " + date);
    }

    @FXML
    private void onCancel() {
        System.out.println("Transaction Cancelled");
    }
}