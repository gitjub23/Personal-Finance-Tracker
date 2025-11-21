package com.example.financeapp.models;

import java.time.LocalDate;

public class Transaction {

    private String title;
    private double amount;
    private String category;
    private LocalDate date;
    private String notes;

    public Transaction(String title, double amount, String category, LocalDate date, String notes) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.notes = notes;
    }

    // getters & setters...

    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public String getNotes() { return notes; }

    public void setTitle(String title) { this.title = title; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNotes(String notes) { this.notes = notes; }
}