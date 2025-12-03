package com.example.financeapp.controllers;

import com.example.financeapp.auth.OAuthService;
import com.example.financeapp.models.User;
import com.example.financeapp.models.UserManager;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final UserManager userManager = new UserManager();

    // ==========================
    // Email/Password Login
    // ==========================
    @FXML
    public void handleLogin() {
        String emailOrUsername = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            showError("Please fill in both fields.");
            return;
        }

        // Try logging in via the DB
        User user = userManager.login(emailOrUsername, password);

        if (user != null) {
            // Save current user in session (you’ll create Session class)
            Session.setCurrentUser(user);
            goToDashboard();
        } else {
            showError("Invalid credentials. Please try again.");
        }
    }

    private void goToDashboard() {
        SceneManager.switchTo("Dashboard");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Failed");
        alert.setContentText(msg);
        alert.show();
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

                // Mark user as “logged in”
                Session.setOAuthLogin(tokenJson);

                goToDashboard();
            }

            @Override
            public void onError(String message) {
                showError("Google Login Error: " + message);
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
                Session.setOAuthLogin(tokenJson);
                goToDashboard();
            }

            @Override
            public void onError(String message) {
                showError("Apple Login Error: " + message);
            }
        });
    }
}