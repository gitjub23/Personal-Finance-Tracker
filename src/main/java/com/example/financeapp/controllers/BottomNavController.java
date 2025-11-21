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
        homeButton.setOnMouseClicked(e -> navigate("Dashboard"));
        transactionsButton.setOnMouseClicked(e -> navigate("TransactionsList"));
        reportsButton.setOnMouseClicked(e -> navigate("ReportsView"));
        settingsButton.setOnMouseClicked(e -> navigate("SettingsView"));
    }

    // Highlight the active nav item
    public void setActive(String active) {
        homeButton.getStyleClass().remove("nav-active");
        transactionsButton.getStyleClass().remove("nav-active");
        reportsButton.getStyleClass().remove("nav-active");
        settingsButton.getStyleClass().remove("nav-active");

        switch (active) {
            case "Dashboard" -> homeButton.getStyleClass().add("nav-active");
            case "TransactionsList" -> transactionsButton.getStyleClass().add("nav-active");
            case "ReportsView" -> reportsButton.getStyleClass().add("nav-active");
            case "SettingsView" -> settingsButton.getStyleClass().add("nav-active");
        }
    }

    private void navigate(String target) {
        System.out.println("Navigate to: " + target);
        SceneManager.switchTo(target);
    }
}