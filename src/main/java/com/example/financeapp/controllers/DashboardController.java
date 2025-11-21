package com.example.financeapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;
    @FXML private Label balanceLabel;

    @FXML private VBox recentTransactionsContainer;
    @FXML private PieChart categoryChart;

    @FXML
    private void initialize() {

        // Example values:
        incomeLabel.setText("$5,000.00");
        expenseLabel.setText("$1,200.00");
        balanceLabel.setText("$3,800.00");

        // Demo chart data:
        categoryChart.getData().addAll(
                new PieChart.Data("Food", 25),
                new PieChart.Data("Transport", 10),
                new PieChart.Data("Shopping", 30),
                new PieChart.Data("Bills", 20),
                new PieChart.Data("Other", 15)
        );

        // Demo recent transactions:
        addDemoTransaction("Uber Ride", "-$25.00", false);
        addDemoTransaction("Salary", "+$5000.00", true);
        addDemoTransaction("Groceries", "-$150.00", false);
    }

    private void addDemoTransaction(String title, String amount, boolean isIncome) {
        Label label = new Label(title + "     " + amount);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; "
                + (isIncome ? "-fx-text-fill: #0a8a31;" : "-fx-text-fill: #d22;"));
        recentTransactionsContainer.getChildren().add(label);
    }
}