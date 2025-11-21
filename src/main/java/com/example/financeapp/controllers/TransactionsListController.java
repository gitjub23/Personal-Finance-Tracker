package com.example.financeapp.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TransactionsListController {

    @FXML private VBox transactionsContainer;
    @FXML private Button addButton;

    @FXML
    private void initialize() {
        // Example demo transactions
        addTransaction("Salary", "Income", "Oct 10", "+$5000.00", true);
        addTransaction("Grocery Shopping", "Food", "Oct 9", "-$150.00", false);
        addTransaction("Uber Ride", "Transportation", "Oct 7", "-$25.00", false);
        addTransaction("Netflix Subscription", "Entertainment", "Oct 8", "-$15.00", false);
    }

    private void addTransaction(String title, String category, String date, String amount, boolean isIncome) {
        VBox card = new VBox();
        card.getStyleClass().add("transaction-card");

        // Title + amount
        HBox topRow = new HBox();
        topRow.setSpacing(10);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("transaction-title");

        Label amountLabel = new Label(amount);
        amountLabel.getStyleClass().add(isIncome ? "amount-income" : "amount-expense");

        HBox.setMargin(amountLabel, new Insets(0, 0, 0, 20));
        topRow.getChildren().addAll(titleLabel, amountLabel);

        // Category + date + edit/delete icons
        HBox bottomRow = new HBox();
        bottomRow.setSpacing(10);

        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("transaction-category");

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

        // Spacer
        HBox spacer = new HBox();
        spacer.setMinWidth(10);
        spacer.setMaxWidth(Double.MAX_VALUE);
        bottomRow.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Edit and delete icons
        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/financeapp/icons/edit.png")));
        editIcon.setFitHeight(18); editIcon.setPreserveRatio(true);

        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/financeapp/icons/delete.png")));
        deleteIcon.setFitHeight(18); deleteIcon.setPreserveRatio(true);

        bottomRow.getChildren().addAll(categoryLabel, dateLabel, spacer, editIcon, deleteIcon);

        card.getChildren().addAll(topRow, bottomRow);
        transactionsContainer.getChildren().add(card);
    }
}