package com.example.demo.controllers;

import com.example.demo.dao.InventoryDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.model.Inventory;
import com.example.demo.model.InventoryWithProduct;
import com.example.demo.model.Product;
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

public class InventoryManagementController extends BaseController {
    @FXML
    private TableView<InventoryWithProduct> inventoryTable;
    @FXML
    private TableColumn<InventoryWithProduct, Integer> inventoryIdCol;
    @FXML
    private TableColumn<InventoryWithProduct, String> productNameCol;
    @FXML
    private TableColumn<InventoryWithProduct, Integer> stockCol;
    @FXML
    private TableColumn<InventoryWithProduct, String> locationCol;
    @FXML
    private TableColumn<InventoryWithProduct, String> updatedCol;
    @FXML
    private TableColumn<InventoryWithProduct, Void> actionsCol;
    @FXML
    private VBox inventoryFormContainer;
    @FXML
    private Label formTitleLabel;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField stockField;
    @FXML
    private TextField locationField;
    @FXML
    private Label inventoryErrorLabel;
    
    private InventoryDao inventoryDao;
    private ProductDao productDao;
    private Inventory editingInventory;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return inventoryTable.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        inventoryDao = new InventoryDao();
        productDao = new ProductDao();
        
        loadProducts();
        setupTable();
        loadInventory();
    }
    
    private void loadProducts() {
        try {
            List<Product> products = productDao.getAllProducts();
            productComboBox.setItems(FXCollections.observableArrayList(products));
            productComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            productComboBox.setButtonCell(new javafx.scene.control.ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load products. Please try again.", getStage());
        }
    }
    
    private void setupTable() {
        inventoryIdCol.setCellValueFactory(new PropertyValueFactory<>("inventory_id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock_quantity"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("warehouse_location"));
        updatedCol.setCellValueFactory(cellData -> {
            InventoryWithProduct inv = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                inv.getUpdated_at() != null ? inv.getUpdated_at().toString() : ""
            );
        });
        
        actionsCol.setCellFactory(param -> new TableCell<InventoryWithProduct, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            
            {
                editButton.setStyle("-fx-background-color: #8e9aaf; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                
                editButton.setOnAction(event -> {
                    InventoryWithProduct inv = getTableView().getItems().get(getIndex());
                    handleEditInventory(inv.getInventory_id());
                });
                
                deleteButton.setOnAction(event -> {
                    InventoryWithProduct inv = getTableView().getItems().get(getIndex());
                    handleDeleteInventory(inv.getInventory_id());
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
    
    private void loadInventory() {
        try {
            List<InventoryWithProduct> inventory = inventoryDao.getAllInventory();
            inventoryTable.setItems(FXCollections.observableArrayList(inventory));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load inventory.", getStage());
        }
    }
    
    @FXML
    private void handleAddInventory() {
        editingInventory = null;
        formTitleLabel.setText("Add Inventory");
        inventoryFormContainer.setVisible(true);
        clearForm();
    }
    
    @FXML
    private void handleEditInventory(int inventoryId) {
        List<InventoryWithProduct> allInventory = inventoryDao.getAllInventory();
        InventoryWithProduct invWithProduct = allInventory.stream()
            .filter(inv -> inv.getInventory_id() == inventoryId)
            .findFirst()
            .orElse(null);
        
        if (invWithProduct != null) {
            Inventory inventory = new Inventory(
                invWithProduct.getInventory_id(),
                invWithProduct.getProduct_id(),
                invWithProduct.getStock_quantity(),
                invWithProduct.getWarehouse_location(),
                invWithProduct.getUpdated_at()
            );
            editingInventory = inventory;
            formTitleLabel.setText("Edit Inventory");
            inventoryFormContainer.setVisible(true);
            
            stockField.setText(String.valueOf(inventory.getStock_quantity()));
            locationField.setText(inventory.getWarehouse_location());
            
            Product product = productDao.getProductById(inventory.getProduct_id());
            if (product != null) {
                productComboBox.getSelectionModel().select(product);
            }
        }
    }
    
    @FXML
    private void handleDeleteInventory(int inventoryId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Inventory");
        alert.setHeaderText("Are you sure you want to delete this inventory record?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = inventoryDao.deleteInventory(inventoryId);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Inventory deleted successfully.", getStage());
                    loadInventory();
                } else {
                    showError(result, getStage());
                }
            }
        });
    }
    
    @FXML
    private void handleSaveInventory() {
        ErrorHandler.clearError(inventoryErrorLabel);
        
        if (productComboBox.getSelectionModel().getSelectedItem() == null) {
            ErrorHandler.handleValidationError("Please select a product.", inventoryErrorLabel);
            return;
        }
        
        if (stockField.getText().trim().isEmpty()) {
            ErrorHandler.handleValidationError("Stock quantity is required.", inventoryErrorLabel);
            return;
        }
        
        Button saveButton = (Button) stockField.getScene().lookup(".primary-button");
        
        try {
            int stock = Integer.parseInt(stockField.getText());
            if (stock < 0) {
                ErrorHandler.handleValidationError("Stock quantity cannot be negative.", inventoryErrorLabel);
                return;
            }
            
            Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
            
            if (saveButton != null) {
                LoadingState.setLoading(saveButton, true);
            }
            LoadingState.setLoading(stockField, true);
            LoadingState.setLoading(locationField, true);
            LoadingState.setLoading(productComboBox, true);
            
            if (editingInventory == null) {
                Inventory newInventory = new Inventory(
                    selectedProduct.getProduct_id(),
                    stock,
                    locationField.getText().trim()
                );
                String result = inventoryDao.createInventory(newInventory);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Inventory created successfully.", getStage());
                    inventoryFormContainer.setVisible(false);
                    loadInventory();
                } else {
                    ErrorHandler.handleValidationError(result, inventoryErrorLabel);
                }
            } else {
                editingInventory.setStock_quantity(stock);
                editingInventory.setWarehouse_location(locationField.getText().trim());
                
                String result = inventoryDao.updateInventory(editingInventory);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Inventory updated successfully.", getStage());
                    inventoryFormContainer.setVisible(false);
                    loadInventory();
                } else {
                    ErrorHandler.handleValidationError(result, inventoryErrorLabel);
                }
            }
        } catch (NumberFormatException e) {
            ErrorHandler.handleValidationError("Please enter a valid stock quantity.", inventoryErrorLabel);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("An unexpected error occurred.", inventoryErrorLabel);
        } finally {
            if (saveButton != null) {
                LoadingState.setLoading(saveButton, false);
            }
            LoadingState.setLoading(stockField, false);
            LoadingState.setLoading(locationField, false);
            LoadingState.setLoading(productComboBox, false);
        }
    }
    
    @FXML
    private void handleCancelForm() {
        inventoryFormContainer.setVisible(false);
        clearForm();
        editingInventory = null;
    }
    
    private void clearForm() {
        stockField.clear();
        locationField.clear();
        productComboBox.getSelectionModel().clearSelection();
        clearError(inventoryErrorLabel);
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
