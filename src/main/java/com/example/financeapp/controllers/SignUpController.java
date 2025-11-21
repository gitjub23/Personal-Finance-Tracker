package com.example.financeapp.controllers;

import com.example.financeapp.auth.OAuthService;
import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private CheckBox agreeTerms;

    // ==========================
    // Create Account (Normal method)
    // ==========================
    @FXML
    public void handleCreateAccount() {

        System.out.println("SIGNUP CLICKED");

        if (fullNameField.getText().isEmpty()
                || emailField.getText().isEmpty()
                || passwordField.getText().isEmpty()) {
            System.out.println("Please fill out all fields.");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (!agreeTerms.isSelected()) {
            System.out.println("You must agree to the terms.");
            return;
        }

        System.out.println("Account created successfully!");

        // After manual account creation → go to login screen
        SceneManager.switchTo("LoginView");
    }

    // ==========================
    // Navigation
    // ==========================
    @FXML
    public void goToLogin() {
        System.out.println("Switching to LoginView");
        SceneManager.switchTo("LoginView");
    }

    // ==========================
    // Google Sign-Up
    // ==========================
    @FXML
    private void handleGoogleSignup() {
        System.out.println("Google Sign-Up clicked");

        OAuthService.loginWithGoogle(new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String tokenJson) {
                System.out.println("Google Sign-Up successful!");
                SceneManager.switchTo("Dashboard");   // redirect after success
            }

            @Override
            public void onError(String message) {
                System.out.println("Google Sign-Up ERROR: " + message);
            }
        });
    }

    // ==========================
    // Apple Sign-Up
    // ==========================
    @FXML
    private void handleAppleSignup() {
        System.out.println("Apple Sign-Up clicked");

        OAuthService.loginWithApple(new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String tokenJson) {
                System.out.println("Apple Sign-Up successful!");
                SceneManager.switchTo("Dashboard");   // redirect after success
            }

            @Override
            public void onError(String message) {
                System.out.println("Apple Sign-Up ERROR: " + message);
            }
        });
    }
}