package com.example.demo.controllers;

import com.example.demo.dao.OrderDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ProfileController extends BaseController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label profileErrorLabel;
    @FXML
    private Label profileSuccessLabel;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Label passwordSuccessLabel;
    @FXML
    private Label totalOrdersStatLabel;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label pendingOrdersStatLabel;
    @FXML
    private Label cartBadgeLabel;
    
    private UserDao userDao;
    private OrderDao orderDao;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return usernameField.getParent().getParent().getParent().getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        userDao = new UserDao();
        orderDao = new OrderDao();
        
        loadUserData();
        loadOrderStatistics();
        updateCartBadge();
    }
    
    private void loadUserData() {
        try {
            User user = UserSession.getLoggedInUser();
            if (user != null) {
                usernameField.setText(user.getUsername());
                emailField.setText(user.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadOrderStatistics() {
        try {
            if (UserSession.getLoggedInUser() == null) return;
            
            int userId = UserSession.getLoggedInUser().getUser_id();
            List<Order> orders = orderDao.getOrdersByUserId(userId);
            
            totalOrdersStatLabel.setText(String.valueOf(orders.size()));
            
            BigDecimal totalSpent = orders.stream()
                .map(Order::getTotal_amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalSpentLabel.setText("$" + totalSpent);
            
            long pendingCount = orders.stream()
                .filter(o -> "Pending".equalsIgnoreCase(o.getStatus()))
                .count();
            pendingOrdersStatLabel.setText(String.valueOf(pendingCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUpdateProfile() {
        ErrorHandler.clearError(profileErrorLabel);
        ErrorHandler.clearError(profileSuccessLabel);
        
        User user = UserSession.getLoggedInUser();
        if (user == null) {
            showError("User session expired. Please login again.", getStage());
            return;
        }
        
        String email = emailField.getText().trim();
        
        String validationError = ValidationUtils.validateLoginFields(email, "dummy");
        if (validationError != null && !validationError.contains("password")) {
            ErrorHandler.handleValidationError(validationError, profileErrorLabel);
            return;
        }
        
        Button updateButton = (Button) emailField.getScene().lookup(".primary-button");
        
        try {
            if (updateButton != null) {
                LoadingState.setLoading(updateButton, true);
            }
            LoadingState.setLoading(emailField, true);
            
            user.setEmail(email);
            String result = userDao.updateUser(user);
            
            if ("SUCCESS".equals(result)) {
                UserSession.setLoggedInUser(user);
                ErrorHandler.showSuccessMessage("Profile updated successfully!", profileSuccessLabel);
            } else {
                ErrorHandler.handleValidationError(result, profileErrorLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred.", profileErrorLabel);
        } finally {
            if (updateButton != null) {
                LoadingState.setLoading(updateButton, false);
            }
            LoadingState.setLoading(emailField, false);
        }
    }
    
    @FXML
    private void handleChangePassword() {
        ErrorHandler.clearError(passwordErrorLabel);
        ErrorHandler.clearError(passwordSuccessLabel);
        
        User user = UserSession.getLoggedInUser();
        if (user == null) {
            showError("User session expired. Please login again.", getStage());
            return;
        }
        
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            ErrorHandler.handleValidationError("Please fill in all password fields.", passwordErrorLabel);
            return;
        }
        
        String validationError = ValidationUtils.validateRegistrationFields("dummy", "dummy@test.com", newPassword, confirmPassword);
        if (validationError != null && validationError.contains("Password")) {
            ErrorHandler.handleValidationError(validationError, passwordErrorLabel);
            return;
        }
        
        Button changePasswordButton = (Button) currentPasswordField.getScene().lookup(".primary-button");
        
        try {
            if (changePasswordButton != null) {
                LoadingState.setLoading(changePasswordButton, true);
            }
            LoadingState.setLoading(currentPasswordField, true);
            LoadingState.setLoading(newPasswordField, true);
            LoadingState.setLoading(confirmPasswordField, true);
            
            String result = userDao.updatePassword(user.getUser_id(), currentPassword, newPassword);
            
            if ("SUCCESS".equals(result)) {
                ErrorHandler.showSuccessMessage("Password changed successfully!", passwordSuccessLabel);
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                ErrorHandler.handleValidationError(result, passwordErrorLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred.", passwordErrorLabel);
        } finally {
            if (changePasswordButton != null) {
                LoadingState.setLoading(changePasswordButton, false);
            }
            LoadingState.setLoading(currentPasswordField, false);
            LoadingState.setLoading(newPasswordField, false);
            LoadingState.setLoading(confirmPasswordField, false);
        }
    }
    
    private void updateCartBadge() {
        int count = CartSession.getCartItemCount();
        cartBadgeLabel.setText("(" + count + ")");
        cartBadgeLabel.setVisible(true);
    }
    
    @FXML
    private void navigateToDashboard() {
        try {
            NavigationUtils.navigateToCustomerDashboard(getStage());
        } catch (IOException e) {
            showError("Failed to load dashboard.", getStage());
        }
    }
    
    @FXML
    private void navigateToProducts() {
        navigateTo("/com/example/demo/views/customer/product-catalog.fxml", "Products - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToCart() {
        navigateTo("/com/example/demo/views/customer/shopping-cart.fxml", "Shopping Cart - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToOrders() {
        navigateTo("/com/example/demo/views/customer/order-history.fxml", "Order History - Smart E-Commerce");
    }
}
