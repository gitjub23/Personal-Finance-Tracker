package com.example.financeapp.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(
                    "/com/example/financeapp/views/" + fxmlName + ".fxml"
            ));

            if (loader.getLocation() == null) {
                System.out.println("❌ FXML NOT FOUND: " + fxmlName);
                return;
            }

            System.out.println("Loading FXML from: " + loader.getLocation());
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}