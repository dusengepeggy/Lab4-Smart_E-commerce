package com.example.demo.controllers;

import com.example.demo.dao.CategoryDao;
import com.example.demo.model.Category;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.LoadingState;
import com.example.demo.utils.NavigationUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CategoryManagementController extends BaseController {
    @FXML
    private TableView<Category> categoriesTable;
    @FXML
    private TableColumn<Category, Integer> categoryIdCol;
    @FXML
    private TableColumn<Category, String> categoryNameCol;
    @FXML
    private TableColumn<Category, String> descriptionCol;
    @FXML
    private TableColumn<Category, Void> actionsCol;
    @FXML
    private VBox categoryFormContainer;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Label categoryErrorLabel;
    
    private CategoryDao categoryDao;
    private Category editingCategory;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return categoriesTable.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        categoryDao = new CategoryDao();
        
        setupTable();
        loadCategories();
    }
    
    private void setupTable() {
        categoryIdCol.setCellValueFactory(new PropertyValueFactory<>("category_id"));
        categoryNameCol.setCellValueFactory(new PropertyValueFactory<>("category_name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        actionsCol.setCellFactory(param -> new TableCell<Category, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            
            {
                editButton.setStyle("-fx-background-color: #8e9aaf; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                
                editButton.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    handleEditCategory(category.getCategory_id());
                });
                
                deleteButton.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    handleDeleteCategory(category.getCategory_id());
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(8);
                    buttons.getChildren().addAll(editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadCategories() {
        try {
            List<Category> categories = categoryDao.getAllCategories();
            categoriesTable.setItems(FXCollections.observableArrayList(categories));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load categories.", getStage());
        }
    }
    
    @FXML
    private void handleAddCategory() {
        editingCategory = null;
        formTitleLabel.setText("Add Category");
        categoryFormContainer.setVisible(true);
        clearForm();
    }
    
    @FXML
    private void handleEditCategory(int categoryId) {
        Category category = categoryDao.getCategoryById(categoryId);
        if (category != null) {
            editingCategory = category;
            formTitleLabel.setText("Edit Category");
            categoryFormContainer.setVisible(true);
            
            nameField.setText(category.getCategory_name());
            descriptionField.setText(category.getDescription());
        }
    }
    
    @FXML
    private void handleDeleteCategory(int categoryId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText("Are you sure you want to delete this category?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = categoryDao.deleteCategory(categoryId);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Category deleted successfully.", getStage());
                    loadCategories();
                } else {
                    showError(result, getStage());
                }
            }
        });
    }
    
    @FXML
    private void handleSaveCategory() {
        ErrorHandler.clearError(categoryErrorLabel);
        
        if (nameField.getText().trim().isEmpty()) {
            ErrorHandler.handleValidationError("Category name is required.", categoryErrorLabel);
            return;
        }
        
        Button saveButton = (Button) nameField.getScene().lookup(".primary-button");
        
        try {
            if (saveButton != null) {
                LoadingState.setLoading(saveButton, true);
            }
            LoadingState.setLoading(nameField, true);
            LoadingState.setLoading(descriptionField, true);
            
            if (editingCategory == null) {
                Category newCategory = new Category(
                    nameField.getText().trim(),
                    descriptionField.getText().trim()
                );
                String result = categoryDao.createCategory(newCategory);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Category created successfully.", getStage());
                    categoryFormContainer.setVisible(false);
                    loadCategories();
                } else {
                    ErrorHandler.handleValidationError(result, categoryErrorLabel);
                }
            } else {
                editingCategory.setCategory_name(nameField.getText().trim());
                editingCategory.setDescription(descriptionField.getText().trim());
                
                String result = categoryDao.updateCategory(editingCategory);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Category updated successfully.", getStage());
                    categoryFormContainer.setVisible(false);
                    loadCategories();
                } else {
                    ErrorHandler.handleValidationError(result, categoryErrorLabel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred.", categoryErrorLabel);
        } finally {
            if (saveButton != null) {
                LoadingState.setLoading(saveButton, false);
            }
            LoadingState.setLoading(nameField, false);
            LoadingState.setLoading(descriptionField, false);
        }
    }
    
    @FXML
    private void handleCancelForm() {
        categoryFormContainer.setVisible(false);
        clearForm();
        editingCategory = null;
    }
    
    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        clearError(categoryErrorLabel);
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
    private void navigateToInventory() {
        navigateTo("/com/example/demo/views/admin/inventory-management.fxml", "Inventory Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToOrders() {
        navigateTo("/com/example/demo/views/admin/order-management.fxml", "Order Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToUsers() {
        navigateTo("/com/example/demo/views/admin/user-management.fxml", "User Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToReviews() {
        navigateTo("/com/example/demo/views/admin/review-management.fxml", "Review Management - Smart E-Commerce");
    }
}
