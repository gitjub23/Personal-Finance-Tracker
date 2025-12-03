package com.example.financeapp.models;

public class Budget {

    private int id;
    private int userId;
    private String category;
    private double monthlyLimit;

    public Budget() {}

    public Budget(int userId, String category, double monthlyLimit) {
        this.userId = userId;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
}