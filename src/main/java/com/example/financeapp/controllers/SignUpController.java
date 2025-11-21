package com.example.financeapp.controllers;

import com.example.financeapp.navigation.SceneManager;
import javafx.fxml.FXML;
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
    public void handleCreateAccount() {
        System.out.println("Creating account...");
        // TODO: validation + save logic
    }

    @FXML
    public void goToLogin() {
        System.out.println("Going back to Login...");
        SceneManager.switchTo("LoginView");
    }
}