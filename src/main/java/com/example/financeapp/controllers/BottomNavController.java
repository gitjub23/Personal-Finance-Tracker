package com.example.financeapp.controllers;

import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class BottomNavController {

    @FXML private VBox homeButton;
    @FXML private VBox transactionsButton;
    @FXML private VBox reportsButton;
    @FXML private VBox settingsButton;

    @FXML
    private void initialize() {
        homeButton.setOnMouseClicked(e -> navigate("dashboard"));
        transactionsButton.setOnMouseClicked(e -> navigate("transactions"));
        reportsButton.setOnMouseClicked(e -> navigate("reports"));
        settingsButton.setOnMouseClicked(e -> navigate("settings"));
    }

    public void setActive(String active) {
        homeButton.getStyleClass().remove("nav-active");
        transactionsButton.getStyleClass().remove("nav-active");
        reportsButton.getStyleClass().remove("nav-active");
        settingsButton.getStyleClass().remove("nav-active");

        switch (active) {
            case "home" -> homeButton.getStyleClass().add("nav-active");
            case "transactions" -> transactionsButton.getStyleClass().add("nav-active");
            case "reports" -> reportsButton.getStyleClass().add("nav-active");
            case "settings" -> settingsButton.getStyleClass().add("nav-active");
        }
    }

    private void navigate(String target) {
        System.out.println("Navigate to: " + target);
        switch (target) {
            case "dashboard" -> SceneManager.switchTo("Dashboard");
            case "transactions" -> SceneManager.switchTo("TransactionsList");
            case "reports" -> SceneManager.switchTo("ReportsView"); // when you make it
            case "settings" -> SceneManager.switchTo("SettingsView"); // when ready
        }
    }

    @FXML
    private void goToTransactions() {
        SceneManager.switchTo("TransactionsList");
    }
}