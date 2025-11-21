package com.example.financeapp.controllers;

import com.example.financeapp.models.Transaction;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TransactionsListController {

    @FXML private VBox transactionsContainer;
    @FXML private Button addButton;

    @FXML
    private void initialize() {

        // Add button → open Add Transaction form
        addButton.setOnAction(e -> SceneManager.switchTo("AddTransaction"));

        loadTransactions();
    }


    // ===================== LOAD REAL TRANSACTIONS =====================
    private void loadTransactions() {
        transactionsContainer.getChildren().clear();

        if (TransactionManager.getTransactions().isEmpty()) {
            Label empty = new Label("No transactions yet.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            transactionsContainer.getChildren().add(empty);
            return;
        }

        for (Transaction t : TransactionManager.getTransactions()) {
            addTransactionCard(t);
        }
    }


    // ===================== CREATE CARD UI =====================
    private void addTransactionCard(Transaction t) {

        boolean isIncome = t.getAmount() > 0;

        VBox card = new VBox();
        card.getStyleClass().add("transaction-card");
        card.setPadding(new Insets(10));
        card.setSpacing(5);


        // ---- TOP ROW (title + amount) ----
        HBox topRow = new HBox(10);

        Label titleLabel = new Label(t.getCategory());
        titleLabel.getStyleClass().add("transaction-title");

        Label amountLabel = new Label(String.format("%+.2f", t.getAmount()));
        amountLabel.getStyleClass().add(isIncome ? "amount-income" : "amount-expense");

        HBox.setMargin(amountLabel, new Insets(0, 0, 0, 20));
        topRow.getChildren().addAll(titleLabel, amountLabel);


        // ---- BOTTOM ROW: category + date + actions ----
        HBox bottomRow = new HBox(10);

        Label categoryLabel = new Label(t.getCategory());
        categoryLabel.getStyleClass().add("transaction-category");

        Label dateLabel = new Label(t.getDate().toString());
        dateLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

        // Spacer expands to push icons to the right
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);


        // Edit icon
        ImageView editIcon = new ImageView(new Image(
                getClass().getResourceAsStream("/com/example/financeapp/icons/edit.png")
        ));
        editIcon.setFitHeight(18);
        editIcon.setPreserveRatio(true);

        // Delete icon
        ImageView deleteIcon = new ImageView(new Image(
                getClass().getResourceAsStream("/com/example/financeapp/icons/delete.png")
        ));
        deleteIcon.setFitHeight(18);
        deleteIcon.setPreserveRatio(true);

        bottomRow.getChildren().addAll(categoryLabel, dateLabel, spacer, editIcon, deleteIcon);


        // Combine rows
        card.getChildren().addAll(topRow, bottomRow);

        transactionsContainer.getChildren().add(card);
    }
}