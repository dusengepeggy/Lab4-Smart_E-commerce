package com.example.demo.controllers;

import com.example.demo.dao.ReviewDao;
import com.example.demo.model.ReviewWithDetails;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.NavigationUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ReviewManagementController extends BaseController {
    @FXML
    private TableView<ReviewWithDetails> reviewsTable;
    @FXML
    private TableColumn<ReviewWithDetails, Integer> reviewIdCol;
    @FXML
    private TableColumn<ReviewWithDetails, String> usernameCol;
    @FXML
    private TableColumn<ReviewWithDetails, String> productNameCol;
    @FXML
    private TableColumn<ReviewWithDetails, Integer> ratingCol;
    @FXML
    private TableColumn<ReviewWithDetails, String> commentCol;
    @FXML
    private TableColumn<ReviewWithDetails, String> dateCol;
    @FXML
    private TableColumn<ReviewWithDetails, Void> actionsCol;
    
    private ReviewDao reviewDao;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return reviewsTable.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        reviewDao = new ReviewDao();
        
        setupTable();
        loadReviews();
    }
    
    private void setupTable() {
        reviewIdCol.setCellValueFactory(new PropertyValueFactory<>("review_id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        dateCol.setCellValueFactory(cellData -> {
            ReviewWithDetails review = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                review.getReview_date() != null ? review.getReview_date().toString() : ""
            );
        });
        
        actionsCol.setCellFactory(param -> new TableCell<ReviewWithDetails, Void>() {
            private final Button deleteButton = new Button("Delete");
            
            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    ReviewWithDetails review = getTableView().getItems().get(getIndex());
                    handleDeleteReview(review.getReview_id());
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }
    
    private void loadReviews() {
        try {
            List<ReviewWithDetails> allReviews = new java.util.ArrayList<>();
            com.example.demo.dao.ProductDao productDao = new com.example.demo.dao.ProductDao();
            List<com.example.demo.model.Product> products = productDao.getAllProducts();
            
            for (com.example.demo.model.Product product : products) {
                List<ReviewWithDetails> productReviews = reviewDao.getReviewsByProductIdWithDetails(product.getProduct_id());
                allReviews.addAll(productReviews);
            }
            
            reviewsTable.setItems(FXCollections.observableArrayList(allReviews));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load reviews.", getStage());
        }
    }
    
    private void handleDeleteReview(int reviewId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Review");
        alert.setHeaderText("Are you sure you want to delete this review?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = reviewDao.deleteReview(reviewId);
                if ("SUCCESS".equals(result)) {
                    showSuccess("Review deleted successfully.", getStage());
                    loadReviews();
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
    private void navigateToUsers() {
        navigateTo("/com/example/demo/views/admin/user-management.fxml", "User Management - Smart E-Commerce");
    }
}
