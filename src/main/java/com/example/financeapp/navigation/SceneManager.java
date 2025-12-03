package com.example.financeapp.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SceneManager {

    private static Stage stage;
    private static String currentViewName;   // <- active view

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchTo(String fxmlName) {
        try {
            currentViewName = fxmlName;   // remember active view

            String fxmlPath = "/com/example/financeapp/views/" + fxmlName + ".fxml";
            URL location = SceneManager.class.getResource(fxmlPath);

            if (location == null) {
                System.out.println("❌ FXML NOT FOUND at: " + fxmlPath);
                return;
            }

            System.out.println("📄 Loading FXML: " + location);

            FXMLLoader loader = new FXMLLoader(location);
            Scene scene = new Scene(loader.load());

            // Optional global stylesheet
            URL css = SceneManager.class.getResource("/com/example/financeapp/styles/app.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("❌ Failed to load scene: " + fxmlName);
            e.printStackTrace();
        }
    }

    // Used by BottomNavController to know which tab to highlight
    public static String getCurrentViewName() {
        return currentViewName;
    }

    // Kept for compatibility (SettingsController.logout calls this)
    public static void clearCache() {
        // no-op now, but method exists so you don't get compile errors
        System.out.println("SceneManager.clearCache(): no caching currently in use.");
    }
}