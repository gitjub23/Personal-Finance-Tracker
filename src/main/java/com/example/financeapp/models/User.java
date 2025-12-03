package com.example.financeapp.models;

public class User {

    private int id;
    private String username;
    private String email;
    private String goal;
    private String currencyCode = "USD";  // Default if not set

    // ==========================
    // Constructors
    // ==========================

    public User() {
        // Required for JDBC + clean initialization
    }

    public User(int id, String username, String email, String goal, String currencyCode) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.goal = goal;
        this.currencyCode = currencyCode != null ? currencyCode : "USD";
    }

    // Constructor used when registering NEW users (ID auto-assigned later)
    public User(String username, String email, String goal, String currencyCode) {
        this.username = username;
        this.email = email;
        this.goal = goal;
        this.currencyCode = currencyCode != null ? currencyCode : "USD";
    }

    // Old 4-parameter constructor kept for compatibility
    public User(int id, String username, String email, String goal) {
        this(id, username, email, goal, "USD");
    }


    // ==========================
    // Getters & Setters
    // ==========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        // Do not allow null values—fallback to USD
        if (currencyCode == null || currencyCode.isBlank()) {
            this.currencyCode = "USD";
        } else {
            this.currencyCode = currencyCode.toUpperCase();
        }
    }


    // ==========================
    // Utility
    // ==========================

    @Override
    public String toString() {
        return "User{id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", goal='" + goal + '\'' +
                ", currency='" + currencyCode + '\'' +
                '}';
    }
}