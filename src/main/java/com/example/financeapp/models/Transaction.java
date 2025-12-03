package com.example.financeapp.models;

import java.time.LocalDate;

public class Transaction {

    private int id;             // DB primary key
    private int userId;         // owner
    private LocalDate date;
    private double amount;
    private boolean income;     // true = income, false = expense
    private String category;    // Food, Transport, etc.
    private String paymentMethod; // Cash, Card, etc.
    private String notes;
    private boolean recurring;
    private String recurrenceRule; // e.g. "MONTHLY"
    private String title;

    public Transaction(int userId, LocalDate date, String title, double amount, boolean income,
                       String category, String paymentMethod, String notes,
                       boolean recurring, String recurrenceRule) {
        this.userId = userId;
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.income = income;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.recurring = recurring;
        this.recurrenceRule = recurrenceRule;
    }

    public Transaction() {
    }

    // ========= Getters & Setters =========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String text) {
        this.title = text;
    }
}