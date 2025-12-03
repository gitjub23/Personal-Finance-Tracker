package com.example.financeapp.controllers;

import com.example.financeapp.models.Transaction;
import com.example.financeapp.models.TransactionManager;
import com.example.financeapp.models.User;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import com.example.financeapp.session.TransactionEditContext;
import com.example.financeapp.util.CurrencyUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class TransactionsListController {

    @FXML private VBox transactionsContainer;
    @FXML private Button addButton;

    private final TransactionManager transactionManager = new TransactionManager();
    private User currentUser;

    @FXML
    private void initialize() {

        currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            // No user -> back to login
            SceneManager.switchTo("LoginView");
            return;
        }

        // Add button → open Add Transaction form (create mode)
        addButton.setOnAction(e -> {
            TransactionEditContext.clear(); // ensure not editing
            SceneManager.switchTo("AddTransaction");
        });

        loadTransactions();
    }

    // ===================== LOAD REAL TRANSACTIONS =====================
    private void loadTransactions() {
        transactionsContainer.getChildren().clear();

        List<Transaction> txs = transactionManager.getTransactionsForUser(currentUser.getId());

        if (txs.isEmpty()) {
            Label empty = new Label("No transactions yet.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            transactionsContainer.getChildren().add(empty);
            return;
        }

        for (Transaction t : txs) {
            addTransactionCard(t);
        }
    }

    // ===================== CREATE CARD UI =====================
    private void addTransactionCard(Transaction t) {

        boolean isIncome = t.isIncome();

        VBox card = new VBox();
        card.getStyleClass().add("transaction-card");
        card.setPadding(new Insets(10));
        card.setSpacing(5);

        // ---- TOP ROW (title + amount) ----
        HBox topRow = new HBox(10);

        String titleText = t.getTitle();
        if (titleText == null || titleText.isBlank()) {
            titleText = t.getCategory(); // fallback if title not set (old rows)
        }

        Label titleLabel = new Label(titleText);
        titleLabel.getStyleClass().add("transaction-title");

        // Currency-aware amount formatting
        String symbol = CurrencyUtil.getSymbol(currentUser.getCurrencyCode());
        double amountAbs = Math.abs(t.getAmount());
        String amountText = String.format("%s%s%.2f",
                isIncome ? "+" : "-",
                symbol,
                amountAbs
        );

        Label amountLabel = new Label(amountText);
        amountLabel.getStyleClass().add(isIncome ? "amount-income" : "amount-expense");

        HBox.setMargin(amountLabel, new Insets(0, 0, 0, 20));
        topRow.getChildren().addAll(titleLabel, amountLabel);

        // ---- BOTTOM ROW: category + date + actions ----
        HBox bottomRow = new HBox(10);

        Label categoryLabel = new Label(t.getCategory());
        categoryLabel.getStyleClass().add("transaction-category");

        Label dateLabel = new Label(t.getDate().toString());
        dateLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Edit icon
        ImageView editIcon = new ImageView(new Image(
                getClass().getResourceAsStream("/com/example/financeapp/icons/edit.png")
        ));
        editIcon.setFitHeight(18);
        editIcon.setPreserveRatio(true);
        editIcon.setOnMouseClicked(e -> {
            TransactionEditContext.startEditing(t);
            SceneManager.switchTo("AddTransaction");
        });

        // Delete icon
        ImageView deleteIcon = new ImageView(new Image(
                getClass().getResourceAsStream("/com/example/financeapp/icons/delete.png")
        ));
        deleteIcon.setFitHeight(18);
        deleteIcon.setPreserveRatio(true);
        deleteIcon.setOnMouseClicked(e -> handleDelete(t));

        bottomRow.getChildren().addAll(categoryLabel, dateLabel, spacer, editIcon, deleteIcon);

        // Combine rows
        card.getChildren().addAll(topRow, bottomRow);

        transactionsContainer.getChildren().add(card);
    }

    private void handleDelete(Transaction t) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete transaction?");
        confirm.setContentText("This action cannot be undone.");
        confirm.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = transactionManager.deleteTransaction(t.getId(), t.getUserId());
                if (!ok) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Delete failed");
                    error.setContentText("Could not delete this transaction.");
                    error.showAndWait();
                } else {
                    loadTransactions();
                }
            }
        });
    }
}