package com.example.demo.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtils {
    
    public static void navigateToScene(Stage stage, String fxmlPath, int width, int height, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.centerOnScreen();
    }
    
    public static void navigateToMainPage(Stage stage) throws IOException {
        navigateToScene(stage, "/com/example/demo/hello-view.fxml", 800, 600, 
            "Smart E-Commerce - Welcome " + UserSession.getLoggedInUser().getUsername());
    }
}
