package com.example.financeapp.controllers;

import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        System.out.println("Logging in...");
        // TODO: Validate credentials
        // SceneManager.switchTo("Dashboard");  <-- later
    }

    @FXML
    public void handleGoToSignUp() {
        System.out.println("Going to SignUp...");
        SceneManager.switchTo("SignUpView");
    }
}