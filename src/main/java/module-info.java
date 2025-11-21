module com.example.financeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.financeapp to javafx.fxml;
    opens com.example.financeapp.controllers to javafx.fxml;

    exports com.example.financeapp;
    exports com.example.financeapp.controllers;
}