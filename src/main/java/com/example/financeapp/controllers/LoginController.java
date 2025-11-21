package com.example.financeapp.controllers;

import com.example.financeapp.auth.OAuthService;
import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // ==========================
    // Email/Password Login
    // ==========================
    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String pass = passwordField.getText();

        boolean isValid = !email.isEmpty() && !pass.isEmpty();

        if (isValid) {
            goToDashboard();
        } else {
            System.out.println("Login failed — invalid input.");
        }
    }

    private void goToDashboard() {
        SceneManager.switchTo("Dashboard");
    }

    // ==========================
    // Navigation
    // ==========================
    @FXML
    private void handleGoToSignUp() {
        SceneManager.switchTo("SignUpView");
    }

    // ==========================
    // Google Login
    // ==========================
    @FXML
    private void handleGoogleLogin() {
        OAuthService.loginWithGoogle(new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String tokenJson) {
                goToDashboard();
            }

            @Override
            public void onError(String message) {
                System.out.println("Google Login Error: " + message);
            }
        });
    }

    // ==========================
    // Apple Login
    // ==========================
    @FXML
    private void handleAppleLogin() {
        OAuthService.loginWithApple(new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String tokenJson) {
                goToDashboard();
            }

            @Override
            public void onError(String message) {
                System.out.println("Apple Login Error: " + message);
            }
        });
    }
}