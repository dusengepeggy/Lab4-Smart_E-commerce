package com.example.demo.controllers;

import com.example.demo.dao.InventoryDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.dao.ReviewDao;
import com.example.demo.model.Inventory;
import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.model.ReviewWithDetails;
import com.example.demo.utils.CartSession;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.LoadingState;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import com.example.demo.utils.ViewState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProductDetailController extends BaseController {
    @FXML
    private Label productNameLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label reviewCountLabel;
    @FXML
    private TextField quantityField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    @FXML
    private Label cartBadgeLabel;
    @FXML
    private VBox reviewsContainer;
    
    private ProductDao productDao;
    private ReviewDao reviewDao;
    private InventoryDao inventoryDao;
    private Product currentProduct;
    private int productId;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return productNameLabel.getParent().getParent().getParent().getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        productDao = new ProductDao();
        reviewDao = new ReviewDao();
        inventoryDao = new InventoryDao();
        
        Integer selectedId = ViewState.getSelectedProductId();
        if (selectedId != null) {
            productId = selectedId;
            ViewState.clearSelectedProductId();
        } else {
            productId = 0;
        }
        
        if (productId > 0) {
            loadProductDetails();
            loadReviews();
        }
        updateCartBadge();
    }
    
    private void loadProductDetails() {
        try {
            currentProduct = productDao.getProductById(productId);
            if (currentProduct == null) {
                showError("Product not found.", getStage());
                return;
            }
            
            productNameLabel.setText(currentProduct.getName());
            descriptionLabel.setText(currentProduct.getDescription() != null ? currentProduct.getDescription() : "No description available.");
            priceLabel.setText("$" + currentProduct.getPrice());
            
            Double avgRating = reviewDao.getAverageRatingByProductId(productId);
            int reviewCount = reviewDao.getReviewCountByProductId(productId);
            
            if (avgRating != null) {
                ratingLabel.setText(String.format("★ %.1f", avgRating));
            } else {
                ratingLabel.setText("★ 0.0");
            }
            reviewCountLabel.setText("(" + reviewCount + " reviews)");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load product details.", getStage());
        }
    }
    
    private void loadReviews() {
        try {
            reviewsContainer.getChildren().clear();
            List<ReviewWithDetails> reviews = reviewDao.getReviewsByProductIdWithDetails(productId);
            
            if (reviews.isEmpty()) {
                Label noReviewsLabel = new Label("No reviews yet. Be the first to review!");
                noReviewsLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");
                reviewsContainer.getChildren().add(noReviewsLabel);
                return;
            }
            
            for (ReviewWithDetails review : reviews) {
                try {
                    VBox reviewCard = new VBox(8);
                    reviewCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 4px; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 12px;");
                    
                    HBox header = new HBox(8);
                    Label usernameLabel = new Label(review.getUsername());
                    usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #000000;");
                    Label ratingLabel = new Label("★ " + review.getRating());
                    ratingLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");
                    Label dateLabel = new Label(review.getReview_date().toString());
                    dateLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
                    
                    header.getChildren().addAll(usernameLabel, ratingLabel, dateLabel);
                    
                    if (review.getComment() != null && !review.getComment().isEmpty()) {
                        Label commentLabel = new Label(review.getComment());
                        commentLabel.setWrapText(true);
                        commentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #000000;");
                        reviewCard.getChildren().addAll(header, commentLabel);
                    } else {
                        reviewCard.getChildren().add(header);
                    }
                    
                    reviewsContainer.getChildren().add(reviewCard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void decreaseQuantity() {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity > 1) {
                quantityField.setText(String.valueOf(quantity - 1));
            }
        } catch (NumberFormatException e) {
            quantityField.setText("1");
        }
    }
    
    @FXML
    private void increaseQuantity() {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(quantity + 1));
        } catch (NumberFormatException e) {
            quantityField.setText("1");
        }
    }
    
    @FXML
    private void handleAddToCart() {
        ErrorHandler.clearError(errorLabel);
        ErrorHandler.clearError(successLabel);
        
        if (currentProduct == null) {
            ErrorHandler.handleValidationError("Product not loaded. Please refresh the page.", errorLabel);
            return;
        }
        
        Button addToCartButton = (Button) quantityField.getScene().lookup(".primary-button");
        
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                ErrorHandler.handleValidationError("Quantity must be greater than 0.", errorLabel);
                return;
            }
            
            // Check inventory availability
            Inventory inventory = inventoryDao.getInventoryByProductId(productId);
            if (inventory == null) {
                ErrorHandler.handleValidationError("This product is currently unavailable. Please check back later.", errorLabel);
                return;
            }
            
            if (inventory.getStock_quantity() < quantity) {
                ErrorHandler.handleValidationError("Only " + inventory.getStock_quantity() + 
                    " item(s) available in stock. Please adjust your quantity.", errorLabel);
                return;
            }
            
            if (addToCartButton != null) {
                LoadingState.setLoading(addToCartButton, true);
            }
            
            CartSession.addToCart(currentProduct, quantity);
            ErrorHandler.showSuccessMessage("Product added to cart successfully!", successLabel);
            updateCartBadge();
        } catch (NumberFormatException e) {
            ErrorHandler.handleValidationError("Please enter a valid number for quantity.", errorLabel);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.handleValidationError("Failed to add product to cart. Please try again.", errorLabel);
        } finally {
            if (addToCartButton != null) {
                LoadingState.setLoading(addToCartButton, false);
            }
        }
    }
    
    @FXML
    private void handleBuyNow() {
        handleAddToCart();
        navigateToCart();
    }
    
    @FXML
    private void handleWriteReview() {
        if (UserSession.getLoggedInUser() == null) {
            showError("Please login to write a review.", getStage());
            return;
        }
        
        if (currentProduct == null) {
            showError("Product not loaded. Please refresh the page.", getStage());
            return;
        }
        
        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle("Write a Review");
        dialog.setHeaderText("Review for: " + currentProduct.getName());
        
        VBox dialogContent = new VBox(12);
        dialogContent.setStyle("-fx-padding: 20px;");
        
        Label ratingLabel = new Label("Rating (1-5):");
        ComboBox<Integer> ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setValue(5);
        ratingComboBox.setPrefWidth(200);
        
        Label commentLabel = new Label("Comment:");
        TextArea commentArea = new TextArea();
        commentArea.setPrefRowCount(5);
        commentArea.setWrapText(true);
        commentArea.setPrefWidth(400);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        dialogContent.getChildren().addAll(ratingLabel, ratingComboBox, commentLabel, commentArea, errorLabel);
        
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setOnAction(e -> {
            if (ratingComboBox.getValue() == null) {
                errorLabel.setText("Please select a rating.");
                errorLabel.setVisible(true);
                e.consume();
                return;
            }
        });
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (ratingComboBox.getValue() == null) {
                    return null;
                }
                return new Review(
                    UserSession.getLoggedInUser().getUser_id(),
                    productId,
                    ratingComboBox.getValue(),
                    commentArea.getText().trim()
                );
            }
            return null;
        });
        
        java.util.Optional<Review> result = dialog.showAndWait();
        result.ifPresent(review -> {
            try {
                String reviewResult = reviewDao.createReview(review);
                if ("SUCCESS".equals(reviewResult)) {
                    showSuccess("Review submitted successfully!", getStage());
                    loadReviews();
                    loadProductDetails();
                } else {
                    showError("Failed to submit review: " + reviewResult, getStage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("An error occurred while submitting your review. Please try again.", getStage());
            }
        });
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
    
    @FXML
    private void navigateToProfile() {
        navigateTo("/com/example/demo/views/customer/profile.fxml", "Profile - Smart E-Commerce");
    }
}
