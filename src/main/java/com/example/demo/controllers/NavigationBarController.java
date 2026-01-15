package com.example.demo.controllers;

import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationBarController {
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Button logoutButton;
    
    @FXML
    public void initialize() {
        updateUserInfo();
    }
    
    public void updateUserInfo() {
        if (UserSession.getLoggedInUser() != null) {
            userNameLabel.setText(UserSession.getLoggedInUser().getUsername());
            userRoleLabel.setText(UserSession.getLoggedInUser().getRole());
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            NavigationUtils.logout(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
