package com.example.financeapp.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SceneManager {

    private static Stage stage;
    private static Scene scene;              // 👈 Persist ONE scene
    private static String currentViewName;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchTo(String fxmlName) {
        try {
            currentViewName = fxmlName;

            String fxmlPath = "/com/example/financeapp/views/" + fxmlName + ".fxml";
            URL location = SceneManager.class.getResource(fxmlPath);

            if (location == null) {
                System.out.println("❌ FXML NOT FOUND at: " + fxmlPath);
                return;
            }

            System.out.println("📄 Loading FXML: " + location);
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();   // 👈 Load only the root

            if (scene == null) {
                // FIRST LOAD → create the Scene once
                scene = new Scene(root);

                URL css = SceneManager.class.getResource("/com/example/financeapp/styles/app.css");
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                }

                stage.setScene(scene);
                stage.show();
            } else {
                // NEXT LOADS → reuse the same Scene, swap root
                scene.setRoot(root);
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to load scene: " + fxmlName);
            e.printStackTrace();
        }
    }

    public static String getCurrentViewName() {
        return currentViewName;
    }

    public static void clearCache() {
        // We no longer cache scenes; kept for compatibility
        System.out.println("SceneManager.clearCache(): no cached scenes to clear.");
    }
}