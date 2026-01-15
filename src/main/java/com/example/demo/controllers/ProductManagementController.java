package com.example.demo.controllers;

import com.example.demo.dao.CategoryDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.ProductWithCategory;
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
import java.math.BigDecimal;
import java.util.List;

public class ProductManagementController extends BaseController {
    @FXML
    private TableView<ProductWithCategory> productsTable;
    @FXML
    private TableColumn<ProductWithCategory, Integer> productIdCol;
    @FXML
    private TableColumn<ProductWithCategory, String> productNameCol;
    @FXML
    private TableColumn<ProductWithCategory, String> categoryCol;
    @FXML
    private TableColumn<ProductWithCategory, String> priceCol;
    @FXML
    private TableColumn<ProductWithCategory, String> createdAtCol;
    @FXML
    private TableColumn<ProductWithCategory, Void> actionsCol;
    @FXML
    private VBox productFormContainer;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private Label productErrorLabel;
    
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private Product editingProduct;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return productsTable.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        productDao = new ProductDao();
        categoryDao = new CategoryDao();
        
        loadCategories();
        setupTable();
        loadProducts();
    }
    
    private void loadCategories() {
        try {
            List<Category> categories = categoryDao.getAllCategories();
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
            categoryComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCategory_name());
                    }
                }
            });
            categoryComboBox.setButtonCell(new javafx.scene.control.ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCategory_name());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load categories. Please try again.", getStage());
        }
    }
    
    private void setupTable() {
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category_name"));
        priceCol.setCellValueFactory(cellData -> {
            ProductWithCategory product = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty("$" + product.getPrice());
        });
        createdAtCol.setCellValueFactory(cellData -> {
            ProductWithCategory product = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                product.getCreated_at() != null ? product.getCreated_at().toString() : ""
            );
        });
        
        actionsCol.setCellFactory(param -> new TableCell<ProductWithCategory, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            
            {
                editButton.setStyle("-fx-background-color: #8e9aaf; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                
                HBox buttons = new HBox(8);
                buttons.getChildren().addAll(editButton, deleteButton);
                
                editButton.setOnAction(event -> {
                    ProductWithCategory product = getTableView().getItems().get(getIndex());
                    handleEditProduct(product.getProduct_id());
                });
                
                deleteButton.setOnAction(event -> {
                    ProductWithCategory product = getTableView().getItems().get(getIndex());
                    handleDeleteProduct(product.getProduct_id());
                });
                
                setGraphic(buttons);
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
    
    private void loadProducts() {
        try {
            List<ProductWithCategory> products = productDao.getAllProductsWithCategory();
            productsTable.setItems(FXCollections.observableArrayList(products));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load products.", getStage());
        }
    }
    
    @FXML
    private void handleAddProduct() {
        editingProduct = null;
        formTitleLabel.setText("Add Product");
        productFormContainer.setVisible(true);
        clearForm();
    }
    
    @FXML
    private void handleEditProduct(int productId) {
        try {
            Product product = productDao.getProductById(productId);
            if (product != null) {
                editingProduct = product;
                formTitleLabel.setText("Edit Product");
                productFormContainer.setVisible(true);
                
                nameField.setText(product.getName());
                descriptionField.setText(product.getDescription());
                priceField.setText(product.getPrice().toString());
                
                Category category = categoryDao.getCategoryById(product.getCategory_id());
                if (category != null) {
                    categoryComboBox.getSelectionModel().select(category);
                }
            } else {
                showError("Product not found.", getStage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load product for editing.", getStage());
        }
    }
    
    @FXML
    private void handleDeleteProduct(int productId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String result = productDao.deleteProduct(productId);
                    if ("SUCCESS".equals(result)) {
                        showSuccess("Product deleted successfully.", getStage());
                        loadProducts();
                    } else {
                        showError(result, getStage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Failed to delete product.", getStage());
                }
            }
        });
    }
    
    @FXML
    private void handleSaveProduct() {
        ErrorHandler.clearError(productErrorLabel);
        
        if (nameField.getText().trim().isEmpty()) {
            ErrorHandler.handleValidationError("Product name is required.", productErrorLabel);
            return;
        }
        
        if (categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            ErrorHandler.handleValidationError("Please select a category.", productErrorLabel);
            return;
        }
        
        Button saveButton = (Button) nameField.getScene().lookup(".primary-button");
        
        try {
            BigDecimal price = new BigDecimal(priceField.getText());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                ErrorHandler.handleValidationError("Price must be greater than 0.", productErrorLabel);
                return;
            }
            
            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            
            if (saveButton != null) {
                LoadingState.setLoading(saveButton, true);
            }
            LoadingState.setLoading(nameField, true);
            LoadingState.setLoading(descriptionField, true);
            LoadingState.setLoading(priceField, true);
            LoadingState.setLoading(categoryComboBox, true);
            
            if (editingProduct == null) {
                Product newProduct = new Product(
                    selectedCategory.getCategory_id(),
                    nameField.getText().trim(),
                    descriptionField.getText().trim(),
                    price
                );
                String result = productDao.createProduct(newProduct);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Product created successfully.", getStage());
                    productFormContainer.setVisible(false);
                    loadProducts();
                } else {
                    ErrorHandler.handleValidationError(result, productErrorLabel);
                }
            } else {
                editingProduct.setName(nameField.getText().trim());
                editingProduct.setDescription(descriptionField.getText().trim());
                editingProduct.setPrice(price);
                editingProduct.setCategory_id(selectedCategory.getCategory_id());
                
                String result = productDao.updateProduct(editingProduct);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Product updated successfully.", getStage());
                    productFormContainer.setVisible(false);
                    loadProducts();
                } else {
                    ErrorHandler.handleValidationError(result, productErrorLabel);
                }
            }
        } catch (NumberFormatException e) {
            ErrorHandler.handleValidationError("Please enter a valid price.", productErrorLabel);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred.", productErrorLabel);
        } finally {
            if (saveButton != null) {
                LoadingState.setLoading(saveButton, false);
            }
            LoadingState.setLoading(nameField, false);
            LoadingState.setLoading(descriptionField, false);
            LoadingState.setLoading(priceField, false);
            LoadingState.setLoading(categoryComboBox, false);
        }
    }
    
    @FXML
    private void handleCancelForm() {
        productFormContainer.setVisible(false);
        clearForm();
        editingProduct = null;
    }
    
    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        clearError(productErrorLabel);
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
    private void navigateToUsers() {
        navigateTo("/com/example/demo/views/admin/user-management.fxml", "User Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToReviews() {
        navigateTo("/com/example/demo/views/admin/review-management.fxml", "Review Management - Smart E-Commerce");
    }
}
