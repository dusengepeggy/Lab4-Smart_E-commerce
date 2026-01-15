package com.example.demo.controllers;

import com.example.demo.dao.OrderDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.model.Order;
import com.example.demo.model.ProductWithCategory;
import com.example.demo.utils.CartSession;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import com.example.demo.utils.ViewState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CustomerController extends BaseController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label cartItemsLabel;
    @FXML
    private Label pendingOrdersLabel;
    @FXML
    private Label cartBadgeLabel;
    @FXML
    private Button cartButton;
    @FXML
    private VBox recentOrdersContainer;
    @FXML
    private FlowPane featuredProductsContainer;
    
    private OrderDao orderDao;
    private ProductDao productDao;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return welcomeLabel.getParent().getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        orderDao = new OrderDao();
        productDao = new ProductDao();
        
        loadDashboardData();
        updateCartBadge();
    }
    
    private void loadDashboardData() {
        try {
            if (UserSession.getLoggedInUser() == null) return;
            
            int userId = UserSession.getLoggedInUser().getUser_id();
            welcomeLabel.setText("Welcome, " + UserSession.getLoggedInUser().getUsername() + "!");
            
            List<Order> orders = orderDao.getOrdersByUserId(userId);
            totalOrdersLabel.setText(String.valueOf(orders.size()));
            
            long pendingCount = orders.stream()
                .filter(o -> "Pending".equalsIgnoreCase(o.getStatus()))
                .count();
            pendingOrdersLabel.setText(String.valueOf(pendingCount));
            
            cartItemsLabel.setText(String.valueOf(CartSession.getCartItemCount()));
            
            loadRecentOrders(orders);
            loadFeaturedProducts();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load dashboard data. Please try again.", getStage());
        }
    }
    
    private void loadRecentOrders(List<Order> orders) {
        try {
            recentOrdersContainer.getChildren().clear();
            int count = Math.min(5, orders.size());
            
            for (int i = 0; i < count; i++) {
                Order order = orders.get(i);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/components/order-item-card.fxml"));
                    HBox orderCard = loader.load();
                
                Label productNameLabel = (Label) orderCard.lookup("#productNameLabel");
                Label quantityLabel = (Label) orderCard.lookup("#quantityLabel");
                Label unitPriceLabel = (Label) orderCard.lookup("#unitPriceLabel");
                Label subtotalLabel = (Label) orderCard.lookup("#subtotalLabel");
                Button actionButton = (Button) orderCard.lookup("#actionButton");
                Button removeButton = (Button) orderCard.lookup("#removeButton");
                
                if (productNameLabel != null) {
                    productNameLabel.setText("Order #" + order.getOrder_id());
                }
                if (quantityLabel != null) {
                    quantityLabel.setText("Date: " + order.getOrder_date());
                }
                if (unitPriceLabel != null) {
                    unitPriceLabel.setText("Status: " + order.getStatus());
                }
                if (subtotalLabel != null) {
                    subtotalLabel.setText("Total: $" + order.getTotal_amount());
                }
                if (actionButton != null) {
                    actionButton.setText("View");
                    int orderId = order.getOrder_id();
                    actionButton.setOnAction(e -> navigateToOrderDetail(orderId));
                }
                if (removeButton != null) {
                    removeButton.setVisible(false);
                }
                
                    recentOrdersContainer.getChildren().add(orderCard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadFeaturedProducts() {
        try {
            featuredProductsContainer.getChildren().clear();
            List<ProductWithCategory> products = productDao.getAllProductsWithCategory();
            int count = Math.min(6, products.size());
            
            for (int i = 0; i < count; i++) {
                ProductWithCategory product = products.get(i);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/components/product-card.fxml"));
                    VBox productCard = loader.load();
                
                Label productNameLabel = (Label) productCard.lookup("#productNameLabel");
                Label categoryLabel = (Label) productCard.lookup("#categoryLabel");
                Label priceLabel = (Label) productCard.lookup("#priceLabel");
                Label ratingLabel = (Label) productCard.lookup("#ratingLabel");
                Label reviewCountLabel = (Label) productCard.lookup("#reviewCountLabel");
                Button actionButton = (Button) productCard.lookup("#actionButton");
                
                if (productNameLabel != null) productNameLabel.setText(product.getName());
                if (categoryLabel != null) categoryLabel.setText(product.getCategory_name());
                if (priceLabel != null) priceLabel.setText("$" + product.getPrice());
                if (ratingLabel != null) ratingLabel.setText("★ 0.0");
                if (reviewCountLabel != null) reviewCountLabel.setText("(0 reviews)");
                if (actionButton != null) {
                    actionButton.setText("View Details");
                    int productId = product.getProduct_id();
                    actionButton.setOnAction(e -> navigateToProductDetail(productId));
                }
                
                    featuredProductsContainer.getChildren().add(productCard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Stage stage = getStage();
            NavigationUtils.navigateToCustomerDashboard(stage);
        } catch (IOException e) {
            e.printStackTrace();
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
    
    private void navigateToProductDetail(int productId) {
        ViewState.setSelectedProductId(productId);
        navigateTo("/com/example/demo/views/customer/product-detail.fxml", "Product Details - Smart E-Commerce");
    }
    
    private void navigateToOrderDetail(int orderId) {
        ViewState.setSelectedOrderId(orderId);
        navigateTo("/com/example/demo/views/customer/order-history.fxml", "Order Details - Smart E-Commerce");
    }
}
