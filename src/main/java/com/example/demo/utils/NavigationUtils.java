package com.example.demo.utils;

import com.example.demo.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtils {
    
    private static final String CUSTOMER_DASHBOARD = "/com/example/demo/views/customer/customer-dashboard.fxml";
    private static final String ADMIN_DASHBOARD = "/com/example/demo/views/admin/admin-dashboard.fxml";
    
    public static void navigateToScene(Stage stage, String fxmlPath, int width, int height, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        
        // Ensure resizable and maximized before setting scene
        stage.setResizable(true);
        stage.setMaximized(true);
        
        stage.setScene(scene);
        stage.setTitle(title);
        
        // Explicitly set resizable and maximized after scene is set
        stage.setResizable(true);
        stage.setMaximized(true);
        
        // Use Platform.runLater to ensure properties persist after scene initialization
        javafx.application.Platform.runLater(() -> {
            if (stage != null) {
                stage.setResizable(true);
                stage.setMaximized(true);
            }
        });
    }
    
    public static void navigateToMainPage(Stage stage) throws IOException {
        User loggedInUser = UserSession.getLoggedInUser();
        if (loggedInUser == null) {
            navigateToAuth(stage);
            return;
        }
        
        String role = loggedInUser.getRole();
        String fxmlPath;
        String title;
        
        if ("Admin".equalsIgnoreCase(role)) {
            fxmlPath = ADMIN_DASHBOARD;
            title = "Smart E-Commerce - Admin Dashboard";
        } else {
            fxmlPath = CUSTOMER_DASHBOARD;
            title = "Smart E-Commerce - Welcome " + loggedInUser.getUsername();
        }
        
        navigateToScene(stage, fxmlPath, 1200, 800, title);
    }
    
    public static void navigateToAuth(Stage stage) throws IOException {
        navigateToScene(stage, "/com/example/demo/auth-view.fxml", 600, 700, 
            "Smart E-Commerce - Authentication");
    }
    
    public static void navigateToCustomerDashboard(Stage stage) throws IOException {
        User loggedInUser = UserSession.getLoggedInUser();
        String title = "Smart E-Commerce - Welcome " + (loggedInUser != null ? loggedInUser.getUsername() : "Customer");
        navigateToScene(stage, CUSTOMER_DASHBOARD, 1200, 800, title);
    }
    
    public static void navigateToAdminDashboard(Stage stage) throws IOException {
        navigateToScene(stage, ADMIN_DASHBOARD, 1200, 800, "Smart E-Commerce - Admin Dashboard");
    }

    
    public static boolean validateSession() {
        User loggedInUser = UserSession.getLoggedInUser();
        return loggedInUser != null;
    }
    
    public static void logout(Stage stage) throws IOException {
        UserSession.cleanUserSession();
        navigateToAuth(stage);
    }
}
