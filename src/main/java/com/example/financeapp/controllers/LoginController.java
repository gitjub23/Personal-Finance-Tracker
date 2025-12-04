package com.example.financeapp.controllers;

import com.example.financeapp.auth.OAuthService;
import com.example.financeapp.models.User;
import com.example.financeapp.models.UserManager;
import com.example.financeapp.navigation.SceneManager;
import com.example.financeapp.session.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;      // <-- bind to "Remember me" checkbox in FXML

    private final UserManager userManager = new UserManager();
    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);

    // ==========================
    // Initialization
    // ==========================
    @FXML
    private void initialize() {
        // Prefill email if user chose "Remember me" previously
        String rememberedEmail = prefs.get("rememberedEmail", "");
        if (!rememberedEmail.isEmpty()) {
            emailField.setText(rememberedEmail);
            if (rememberMeCheckBox != null) {
                rememberMeCheckBox.setSelected(true);
            }
        }
    }

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
            // Save current user in session
            Session.setCurrentUser(user);

            // Handle "Remember me"
            if (rememberMeCheckBox != null && rememberMeCheckBox.isSelected()) {
                prefs.put("rememberedEmail", emailOrUsername);
            } else {
                prefs.remove("rememberedEmail");
            }

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

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    // ==========================
    // Forgot Password
    // ==========================
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField emailInput = new TextField();
        emailInput.setPromptText("Email");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailInput, 1, 0);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String email = emailInput.getText().trim();
                String newPass = newPasswordField.getText();
                String confirmPass = confirmPasswordField.getText();

                if (email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    showError("All fields are required.");
                    return null;
                }

                if (!newPass.equals(confirmPass)) {
                    showError("Passwords do not match.");
                    return null;
                }

                // You should implement this method in UserManager:
                // boolean resetPassword(String email, String newRawPassword)
                boolean success = userManager.resetPassword(email, newPass);

                if (success) {
                    showInfo("Password updated successfully. You can now sign in.");
                } else {
                    showError("No account found with that email.");
                }
            }
            return null;
        });

        dialog.showAndWait();
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
                Platform.runLater(() -> {
                    String email = OAuthService.extractEmail(tokenJson);
                    String name  = OAuthService.extractName(tokenJson);

                    if (email == null || email.isBlank()) {
                        showError("Could not read your Google account email.");
                        return;
                    }

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

                    goToDashboard();  // safe now, we're on FX thread
                });
            }

            @Override
            public void onError(String message) {
                Platform.runLater(() ->
                        showError("Google Login Error: " + message)
                );
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

                    goToDashboard();
                });
            }

            @Override
            public void onError(String message) {
                Platform.runLater(() ->
                        showError("Apple Login Error: " + message)
                );
            }
        });
    }
}