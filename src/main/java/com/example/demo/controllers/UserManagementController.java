package com.example.demo.controllers;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.LoadingState;
import com.example.demo.utils.NavigationUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagementController extends BaseController {
    @FXML
    private ComboBox<String> roleFilterComboBox;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIdCol;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> roleCol;
    @FXML
    private TableColumn<User, Void> actionsCol;
    
    private UserDao userDao;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return usersTable.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        userDao = new UserDao();
        
        setupRoleFilter();
        setupTable();
        loadUsers();
    }
    
    private void setupRoleFilter() {
        roleFilterComboBox.getItems().addAll("All", "Admin", "Customer");
        roleFilterComboBox.getSelectionModel().select("All");
    }
    
    private void setupTable() {
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        actionsCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editRoleButton = new Button("Change Role");
            private final Button deleteButton = new Button("Delete");
            
            {
                editRoleButton.setStyle("-fx-background-color: #8e9aaf; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                
                editRoleButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleChangeRole(user);
                });
                
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user.getUser_id());
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(8);
                    buttons.getChildren().addAll(editRoleButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadUsers() {
        try {
            List<User> users = userDao.getAllUsers();
            usersTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load users.", getStage());
        }
    }
    
    @FXML
    private void handleFilter() {
        String selectedRole = roleFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedRole == null || "All".equals(selectedRole)) {
            loadUsers();
            return;
        }
        
        List<User> allUsers = userDao.getAllUsers();
        List<User> filtered = allUsers.stream()
            .filter(u -> selectedRole.equalsIgnoreCase(u.getRole()))
            .collect(Collectors.toList());
        usersTable.setItems(FXCollections.observableArrayList(filtered));
    }
    
    private void handleChangeRole(User user) {
        String currentRole = user.getRole();
        String newRole = "Admin".equals(currentRole) ? "Customer" : "Admin";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Change User Role");
        alert.setHeaderText("Change role for " + user.getUsername() + "?");
        alert.setContentText("Current role: " + currentRole + "\nNew role: " + newRole);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = userDao.updateRole(user.getUser_id(), newRole);
                if ("SUCCESS".equals(result)) {
                    showSuccess("User role updated successfully.", getStage());
                    loadUsers();
                } else {
                    showError(result, getStage());
                }
            }
        });
    }
    
    private void handleDeleteUser(int userId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = userDao.deleteUser(userId);
                if ("SUCCESS".equals(result)) {
                    showSuccess("User deleted successfully.", getStage());
                    loadUsers();
                } else {
                    showError(result, getStage());
                }
            }
        });
    }
    
    @FXML
    private void navigateToDashboard() {
        try {
            NavigationUtils.navigateToAdminDashboard(getStage());
        } catch (IOException e) {
            showError("Failed to load dashboard.", getStage());
        }
    }
    
    @FXML
    private void navigateToProducts() {
        navigateTo("/com/example/demo/views/admin/product-management.fxml", "Product Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToCategories() {
        navigateTo("/com/example/demo/views/admin/category-management.fxml", "Category Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToInventory() {
        navigateTo("/com/example/demo/views/admin/inventory-management.fxml", "Inventory Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToOrders() {
        navigateTo("/com/example/demo/views/admin/order-management.fxml", "Order Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToReviews() {
        navigateTo("/com/example/demo/views/admin/review-management.fxml", "Review Management - Smart E-Commerce");
    }
}
