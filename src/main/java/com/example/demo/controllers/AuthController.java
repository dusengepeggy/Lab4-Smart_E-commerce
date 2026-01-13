package com.example.demo.controllers;

import com.example.demo.dao.UserDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.demo.model.User;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import com.example.demo.utils.ValidationUtils;

public class AuthController {
    @FXML
    private TabPane authTabPane;
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginErrorLabel;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerEmailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerConfirmPasswordField;
    @FXML
    private Label registerErrorLabel;
    
    private UserDao userDao;
    
    @FXML
    public void initialize() {
        userDao = new UserDao();
        loginErrorLabel.setVisible(false);
        registerErrorLabel.setVisible(false);
    }
    
    @FXML
    private void handleLogin() {
        loginErrorLabel.setVisible(false);
        loginErrorLabel.setText("");
        
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();
        
        String validationError = ValidationUtils.validateLoginFields(email, password);
        if (validationError != null) {
            showLoginError(validationError);
            return;
        }
        
        String result = userDao.login(email, password);
        
        if ("SUCCESS".equals(result)) {
            User loggedInUser = UserSession.getLoggedInUser();
            if (loggedInUser != null) {
                navigateToMainPage();
            } else {
                showLoginError("Login failed. Please try again.");
            }
        } else {
            showLoginError(result);
        }
    }
    
    @FXML
    private void handleRegister() {
        registerErrorLabel.setVisible(false);
        registerErrorLabel.setText("");
        
        String username = registerUsernameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        String role = "Customer";
        
        String validationError = ValidationUtils.validateRegistrationFields(username, email, password, confirmPassword);
        if (validationError != null) {
            showRegisterError(validationError);
            return;
        }
        
        User newUser = new User(username, email, password, role);
        String result = userDao.registerUser(newUser);
        
        if ("SUCCESS".equals(result)) {
            showRegisterSuccess("Registration successful! Please login.");
            clearRegisterFields();
            authTabPane.getSelectionModel().select(0);
        } else {
            showRegisterError(result);
        }
    }
    
    private void showLoginError(String message) {
        loginErrorLabel.setText(message);
        loginErrorLabel.setVisible(true);
    }
    
    private void showRegisterError(String message) {
        registerErrorLabel.setText(message);
        registerErrorLabel.setVisible(true);
    }
    
    private void showRegisterSuccess(String message) {
        registerErrorLabel.setText(message);
        registerErrorLabel.setStyle("-fx-text-fill: #27ae60;");
        registerErrorLabel.setVisible(true);
    }
    
    private void clearRegisterFields() {
        registerUsernameField.clear();
        registerEmailField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
    }
    
    private void navigateToMainPage() {
        try {
            Stage stage = (Stage) authTabPane.getScene().getWindow();
            NavigationUtils.navigateToMainPage(stage);
        } catch (Exception e) {
            e.printStackTrace();
            showLoginError("Failed to load main page. Please try again.");
        }
    }
}
