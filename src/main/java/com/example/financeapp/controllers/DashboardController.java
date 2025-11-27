package com.example.financeapp.controllers;

import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;
    @FXML private Label balanceLabel;

    @FXML private VBox recentTransactionsContainer;
    @FXML private PieChart categoryChart;

    @FXML private Button addButton;   // <-- REQUIRED

    @FXML
    private void initialize() {

        // ================= ADD BUTTON =================
        addButton.setOnAction(e -> SceneManager.switchTo("AddTransaction"));

        // ================= SUMMARY VALUES =================
        incomeLabel.setText("$5,000.00");
        expenseLabel.setText("$1,200.00");
        balanceLabel.setText("$3,800.00");

        // ================= PIE CHART DATA =================
        categoryChart.getData().addAll(
                new PieChart.Data("Food", 25),
                new PieChart.Data("Transport", 10),
                new PieChart.Data("Shopping", 30),
                new PieChart.Data("Bills", 20),
                new PieChart.Data("Other", 15)
        );

        // ================= DEMO TRANSACTIONS =================
        addDemoTransaction("Uber Ride", "-$25.00", false);
        addDemoTransaction("Salary", "+$5000.00", true);
        addDemoTransaction("Groceries", "-$150.00", false);
    }

    private void addDemoTransaction(String title, String amount, boolean isIncome) {
        Label label = new Label(title + "   " + amount);
        label.setStyle("-fx-font-size: 14px; " +
                (isIncome ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));

        recentTransactionsContainer.getChildren().add(label);
    }
}