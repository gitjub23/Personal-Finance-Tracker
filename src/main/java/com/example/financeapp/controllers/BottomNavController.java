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

        // Highlight the tab based on current view
        String current = SceneManager.getCurrentViewName();
        System.out.println("[BottomNav] initialize, current view = " + current);
        setActive(current);
    }

    // Highlight the active nav item
    public void setActive(String active) {
        // Remove nav-active from all
        homeButton.getStyleClass().remove("nav-active");
        transactionsButton.getStyleClass().remove("nav-active");
        reportsButton.getStyleClass().remove("nav-active");
        settingsButton.getStyleClass().remove("nav-active");

        // Also clear inline styles (in case we set any)
        clearInlineStyles();

        if (active == null) return;

        VBox target = null;

        switch (active) {
            case "Dashboard" -> target = homeButton;
            case "TransactionsList" -> target = transactionsButton;
            case "ReportsView" -> target = reportsButton;
            case "SettingsView", "BudgetsView" -> target = settingsButton;
        }

        if (target != null) {
            System.out.println("[BottomNav] setActive -> " + active);
            target.getStyleClass().add("nav-active");

            // EXTRA visible: inline style to ensure background shows even if CSS fails
            target.setStyle(
                    "-fx-background-color: rgba(66,133,244,0.25); " +
                            "-fx-background-radius: 16;"
            );
        }
    }

    private void clearInlineStyles() {
        homeButton.setStyle("");
        transactionsButton.setStyle("");
        reportsButton.setStyle("");
        settingsButton.setStyle("");
    }

    private void navigate(String target) {
        System.out.println("Navigate to: " + target);
        SceneManager.switchTo(target);
        // New scene's BottomNav will initialize and call setActive() again
    }
}