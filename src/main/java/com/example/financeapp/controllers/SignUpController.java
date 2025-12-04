package com.example.financeapp.controllers;

import com.example.financeapp.auth.OAuthService;
import com.example.financeapp.models.User;
import com.example.financeapp.models.UserManager;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private final UserManager userManager = new UserManager();

    // ==========================
    // Create Account (Normal method)
    // ==========================
    @FXML
    public void handleCreateAccount() {

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Basic validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill out all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (!agreeTerms.isSelected()) {
            showError("You must agree to the terms.");
            return;
        }

        // Register user using UserManager
        // We’ll treat fullName as username, goal is empty for now.
        boolean success = userManager.register(fullName, email, password, "");

        if (success) {
            showInfo("Account created successfully! You can now log in.");
            // After manual account creation → go to login screen
            SceneManager.switchTo("LoginView");
        } else {
            showError("Could not create account. Email or username may already be in use.");
        }
    }

    // ==========================
    // Navigation
    // ==========================
    @FXML
    public void goToLogin() {
        SceneManager.switchTo("LoginView");
    }

    // ==========================
    // Google Sign-Up
    // ==========================
    @FXML
    private void handleGoogleSignup() {
        OAuthService.loginWithGoogle(new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String tokenJson) {
                Platform.runLater(() -> {
                    String email = OAuthService.extractEmail(tokenJson);
                    String name  = OAuthService.extractName(tokenJson);

                    if (email == null || email.isBlank()) {
                        showError("Could not read your Google account email.");
                        return;
                    }

                    // Create or load a local user for this Google account
                    User user = userManager.getOrCreateOAuthUser(
                            email,
                            (name == null || name.isBlank()) ? email : name
                    );

                    if (user == null) {
                        showError("Could not create a local user for your Google account.");
                        return;
                    }

                    Session.setCurrentUser(user);
                    Session.setOAuthLogin(tokenJson);

                    SceneManager.switchTo("Dashboard");
                });
            }

            @Override
            public void onError(String message) {
                Platform.runLater(() ->
                        showError("Google Sign-Up ERROR: " + message)
                );
            }
        });
    }

    // ==========================
    // Apple Sign-Up
    // ==========================
    @FXML
    private void handleAppleSignup() {
        OAuthService.loginWithApple(new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String tokenJson) {
                Platform.runLater(() -> {
                    String email = OAuthService.extractEmail(tokenJson);
                    String name  = OAuthService.extractName(tokenJson);

                    if (email == null || email.isBlank()) {
                        showError("Could not read your Apple account email.");
                        return;
                    }

                    User user = userManager.getOrCreateOAuthUser(
                            email,
                            (name == null || name.isBlank()) ? email : name
                    );

                    if (user == null) {
                        showError("Could not create a local user for your Apple account.");
                        return;
                    }

                    Session.setCurrentUser(user);
                    Session.setOAuthLogin(tokenJson);

                    SceneManager.switchTo("Dashboard");
                });
            }

            @Override
            public void onError(String message) {
                Platform.runLater(() ->
                        showError("Apple Sign-Up ERROR: " + message)
                );
            }
        });
    }

    // ==========================
    // Helpers
    // ==========================
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Sign-Up Failed");
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Sign-Up");
        alert.setContentText(msg);
        alert.show();
    }
}