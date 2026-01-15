package com.example.demo.controllers;

import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseController {
    
    protected void showError(String message, Stage stage) {
        ErrorHandler.showError(message, stage);
    }
    
    protected void showSuccess(String message, Stage stage) {
        ErrorHandler.showSuccess(message, stage);
    }
    
    protected void showWarning(String message, Stage stage) {
        ErrorHandler.showWarning(message, stage);
    }
    
    protected void handleValidationError(String message, Label errorLabel) {
        ErrorHandler.handleValidationError(message, errorLabel);
    }
    
    protected void clearError(Label errorLabel) {
        ErrorHandler.clearError(errorLabel);
    }
    
    protected void showSuccessMessage(String message, Label successLabel) {
        ErrorHandler.showSuccessMessage(message, successLabel);
    }
    
    protected void navigateTo(String fxmlPath, String title) {
        try {
            Stage stage = getStage();
            NavigationUtils.navigateToScene(stage, fxmlPath, 1200, 800, title);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate. Please try again.", getStage());
        }
    }
    
    protected boolean validateSession() {
        return NavigationUtils.validateSession();
    }
    
    protected void logout() {
        try {
            Stage stage = getStage();
            NavigationUtils.logout(stage);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to logout. Please try again.", getStage());
        }
    }
    
    protected Stage getStage() {
        return (Stage) getRootNode().getScene().getWindow();
    }
    
    protected abstract javafx.scene.Node getRootNode();
    
    protected void initialize() {
        if (!validateSession()) {
            try {
                NavigationUtils.navigateToAuth(getStage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
