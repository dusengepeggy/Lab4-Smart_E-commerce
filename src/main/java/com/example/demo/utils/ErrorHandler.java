package com.example.demo.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

public class ErrorHandler {
    
    public static void showError(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }
    
    public static void showSuccess(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("Success");
        alert.setHeaderText("Operation completed successfully");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }
    
    public static void showWarning(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(stage);
        alert.setTitle("Warning");
        alert.setHeaderText("Please note");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }
    
    public static void handleDatabaseError(SQLException e, Stage stage) {
        String message = "An unexpected database error occurred.";
        
        if (e.getSQLState() != null) {
            if (e.getSQLState().startsWith("08")) {
                message = "The server is currently unreachable. Please check your internet connection.";
            } else if (e.getSQLState().equals("23505")) {
                message = "This record already exists. Please check your input.";
            } else if (e.getSQLState().equals("23503")) {
                message = "Invalid reference. The related record does not exist.";
            } else if (e.getSQLState().equals("23514")) {
                message = "Invalid data. Please check your input values.";
            }
        }
        
        e.printStackTrace();
        showError(message, stage);
    }
    
    public static void handleValidationError(String message, Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setStyle("-fx-text-fill: #f44336;");
        }
    }
    
    public static void clearError(Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }
    
    public static void showSuccessMessage(String message, Label successLabel) {
        if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setVisible(true);
            successLabel.setStyle("-fx-text-fill: #4caf50;");
        }
    }
}
