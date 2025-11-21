package com.example.financeapp.models;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public static List<Transaction> getTransactions() {
        return transactions;
    }
}