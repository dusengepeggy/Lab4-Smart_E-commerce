package com.example.demo.controllers;

import com.example.demo.dao.UserDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.demo.model.User;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.LoadingState;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import com.example.demo.utils.ValidationUtils;

public class AuthController extends BaseController {
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
        try {
            userDao = new UserDao();
            loginErrorLabel.setVisible(false);
            registerErrorLabel.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogin() {
        ErrorHandler.clearError(loginErrorLabel);
        
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();
        
        String validationError = ValidationUtils.validateLoginFields(email, password);
        if (validationError != null) {
            ErrorHandler.handleValidationError(validationError, loginErrorLabel);
            return;
        }
        
        Button loginButton = (Button) loginEmailField.getScene().lookup("#loginButton");
        if (loginButton == null) {
            loginButton = (Button) loginEmailField.getParent().lookup(".button");
        }
        
        try {
            LoadingState.setLoading(loginButton, true);
            LoadingState.setLoading(loginEmailField, true);
            LoadingState.setLoading(loginPasswordField, true);
            
            String result = userDao.login(email, password);
            
            if ("SUCCESS".equals(result)) {
                User loggedInUser = UserSession.getLoggedInUser();
                if (loggedInUser != null) {
                    navigateToMainPage();
                } else {
                    ErrorHandler.handleValidationError("Login failed. Please try again.", loginErrorLabel);
                }
            } else {
                ErrorHandler.handleValidationError(result, loginErrorLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred. Please try again.", loginErrorLabel);
        } finally {
            LoadingState.setLoading(loginButton, false);
            LoadingState.setLoading(loginEmailField, false);
            LoadingState.setLoading(loginPasswordField, false);
        }
    }
    
    @FXML
    private void handleRegister() {
        ErrorHandler.clearError(registerErrorLabel);
        
        String username = registerUsernameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        String role = "Customer";
        
        String validationError = ValidationUtils.validateRegistrationFields(username, email, password, confirmPassword);
        if (validationError != null) {
            ErrorHandler.handleValidationError(validationError, registerErrorLabel);
            return;
        }
        
        Button registerButton = (Button) registerUsernameField.getScene().lookup("#registerButton");
        if (registerButton == null) {
            registerButton = (Button) registerUsernameField.getParent().lookup(".button");
        }
        
        try {
            LoadingState.setLoading(registerButton, true);
            LoadingState.setLoading(registerUsernameField, true);
            LoadingState.setLoading(registerEmailField, true);
            LoadingState.setLoading(registerPasswordField, true);
            LoadingState.setLoading(registerConfirmPasswordField, true);
            
            User newUser = new User(username, email, password, role);
            String result = userDao.registerUser(newUser);
            
            if ("SUCCESS".equals(result)) {
                ErrorHandler.showSuccessMessage("Registration successful! Please login.", registerErrorLabel);
                clearRegisterFields();
                authTabPane.getSelectionModel().select(0);
            } else {
                ErrorHandler.handleValidationError(result, registerErrorLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred. Please try again.", registerErrorLabel);
        } finally {
            LoadingState.setLoading(registerButton, false);
            LoadingState.setLoading(registerUsernameField, false);
            LoadingState.setLoading(registerEmailField, false);
            LoadingState.setLoading(registerPasswordField, false);
            LoadingState.setLoading(registerConfirmPasswordField, false);
        }
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
            ErrorHandler.handleValidationError("Failed to load main page. Please try again.", loginErrorLabel);
        }
    }
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return authTabPane;
    }
}
