package com.example.financeapp.session;

import com.example.financeapp.models.Transaction;

/**
 * Simple holder for the transaction being edited.
 * Similar idea to Session for the current user.
 */
public class TransactionEditContext {

    private static Transaction editingTransaction;

    public static void startEditing(Transaction t) {
        editingTransaction = t;
    }

    public static Transaction getEditingTransaction() {
        return editingTransaction;
    }

    public static boolean isEditing() {
        return editingTransaction != null;
    }

    public static void clear() {
        editingTransaction = null;
    }
}