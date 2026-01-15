package com.example.demo.controllers;

import com.example.demo.dao.InventoryDao;
import com.example.demo.dao.OrderDao;
import com.example.demo.dao.OrderItemDao;
import com.example.demo.model.Inventory;
import com.example.demo.model.Order;
import com.example.demo.utils.CartSession;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.LoadingState;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class ShoppingCartController extends BaseController {
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label emptyCartLabel;
    @FXML
    private Label cartBadgeLabel;
    
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private InventoryDao inventoryDao;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return cartItemsContainer.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        orderDao = new OrderDao();
        orderItemDao = new OrderItemDao();
        inventoryDao = new InventoryDao();
        
        loadCartItems();
        updateCartBadge();
    }
    
    private void loadCartItems() {
        try {
            cartItemsContainer.getChildren().clear();
            Map<Integer, CartSession.CartItem> cartItems = CartSession.getCartItems();
            
            if (cartItems.isEmpty()) {
                emptyCartLabel.setVisible(true);
                subtotalLabel.setText("$0.00");
                totalLabel.setText("$0.00");
                return;
            }
            
            emptyCartLabel.setVisible(false);
            
            for (CartSession.CartItem item : cartItems.values()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/components/order-item-card.fxml"));
                    HBox cartItemCard = loader.load();
                
                Label productNameLabel = (Label) cartItemCard.lookup("#productNameLabel");
                Label quantityLabel = (Label) cartItemCard.lookup("#quantityLabel");
                Label unitPriceLabel = (Label) cartItemCard.lookup("#unitPriceLabel");
                Label subtotalLabel = (Label) cartItemCard.lookup("#subtotalLabel");
                Button actionButton = (Button) cartItemCard.lookup("#actionButton");
                Button removeButton = (Button) cartItemCard.lookup("#removeButton");
                
                if (productNameLabel != null) {
                    productNameLabel.setText(item.getProduct().getName());
                }
                if (quantityLabel != null) {
                    quantityLabel.setText("Qty: " + item.getQuantity());
                }
                if (unitPriceLabel != null) {
                    unitPriceLabel.setText("Unit: $" + item.getUnitPrice());
                }
                if (subtotalLabel != null) {
                    subtotalLabel.setText("Subtotal: $" + item.getSubtotal());
                }
                if (actionButton != null) {
                    actionButton.setText("Update");
                    actionButton.setVisible(false);
                }
                HBox quantityControls = new HBox(8);
                Button decreaseBtn = new Button("-");
                decreaseBtn.setStyle("-fx-background-color: #feeafa; -fx-background-radius: 4px; -fx-padding: 4px 8px; -fx-cursor: hand;");
                Label qtyDisplay = new Label(String.valueOf(item.getQuantity()));
                qtyDisplay.setStyle("-fx-font-size: 14px; -fx-min-width: 30px; -fx-alignment: center;");
                Button increaseBtn = new Button("+");
                increaseBtn.setStyle("-fx-background-color: #feeafa; -fx-background-radius: 4px; -fx-padding: 4px 8px; -fx-cursor: hand;");
                
                int productId = item.getProduct().getProduct_id();
                
                decreaseBtn.setOnAction(e -> {
                    int currentQty = item.getQuantity();
                    if (currentQty > 1) {
                        CartSession.updateQuantity(productId, currentQty - 1);
                        loadCartItems();
                        updateCartBadge();
                    }
                });
                
                increaseBtn.setOnAction(e -> {
                    int currentQty = item.getQuantity();
                    // Check inventory before increasing
                    Inventory inventory = inventoryDao.getInventoryByProductId(productId);
                    if (inventory != null && inventory.getStock_quantity() > currentQty) {
                        CartSession.updateQuantity(productId, currentQty + 1);
                        loadCartItems();
                        updateCartBadge();
                    } else {
                        showError("Cannot increase quantity. Only " + 
                            (inventory != null ? inventory.getStock_quantity() : 0) + 
                            " item(s) available in stock.", getStage());
                    }
                });
                
                quantityControls.getChildren().addAll(decreaseBtn, qtyDisplay, increaseBtn);
                
                if (quantityLabel != null) {
                    quantityLabel.setGraphic(quantityControls);
                    quantityLabel.setText("");
                }
                
                if (actionButton != null) {
                    actionButton.setText("Delete");
                    actionButton.setVisible(true);
                    actionButton.setOnAction(e -> handleRemoveItem(productId));
                }
                if (removeButton != null) {
                    removeButton.setVisible(false);
                }
                
                    cartItemsContainer.getChildren().add(cartItemCard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            updateTotals();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load cart items.", getStage());
        }
    }
    
    private void updateTotals() {
        BigDecimal total = CartSession.getTotalAmount();
        subtotalLabel.setText("$" + total);
        totalLabel.setText("$" + total);
    }
    
    private void handleRemoveItem(int productId) {
        CartSession.removeFromCart(productId);
        loadCartItems();
        updateCartBadge();
    }
    
    @FXML
    private void handleCheckout() {
        if (CartSession.getCartItems().isEmpty()) {
            showError("Your cart is empty. Please add items before checkout.", getStage());
            return;
        }
        
        if (UserSession.getLoggedInUser() == null) {
            showError("Please login to complete your purchase.", getStage());
            return;
        }
        
        // Validate inventory before checkout
        Map<Integer, CartSession.CartItem> cartItems = CartSession.getCartItems();
        StringBuilder inventoryErrors = new StringBuilder();
        
        for (CartSession.CartItem item : cartItems.values()) {
            Inventory inventory = inventoryDao.getInventoryByProductId(item.getProduct().getProduct_id());
            if (inventory == null) {
                inventoryErrors.append("• ").append(item.getProduct().getName())
                    .append(": No inventory information available.\n");
            } else if (inventory.getStock_quantity() < item.getQuantity()) {
                inventoryErrors.append("• ").append(item.getProduct().getName())
                    .append(": Only ").append(inventory.getStock_quantity())
                    .append(" available, but you ordered ").append(item.getQuantity()).append(".\n");
            }
        }
        
        if (inventoryErrors.length() > 0) {
            showError("Not enough in stock:\n\n" + inventoryErrors.toString() + 
                "\nPlease adjust your cart quantities and try again.", getStage());
            return;
        }
        
        Button checkoutButton = (Button) subtotalLabel.getScene().lookup(".primary-button");
        if (checkoutButton == null) {
            checkoutButton = (Button) subtotalLabel.getParent().lookup(".button");
        }
        
        try {
            LoadingState.setLoading(checkoutButton, true);
            
            int userId = UserSession.getLoggedInUser().getUser_id();
            BigDecimal totalAmount = CartSession.getTotalAmount();
            
            Order order = new Order(userId, totalAmount, "Pending");
            int orderId = orderDao.createOrder(order);
            
            if (orderId > 0) {
                // Update inventory and create order items
                for (CartSession.CartItem item : cartItems.values()) {
                    Inventory inventory = inventoryDao.getInventoryByProductId(item.getProduct().getProduct_id());
                    if (inventory != null) {
                        int newStock = inventory.getStock_quantity() - item.getQuantity();
                        inventoryDao.updateStockQuantity(item.getProduct().getProduct_id(), newStock);
                    }
                    
                    com.example.demo.model.OrderItem orderItem = new com.example.demo.model.OrderItem(
                        orderId,
                        item.getProduct().getProduct_id(),
                        item.getQuantity(),
                        item.getUnitPrice()
                    );
                    orderItemDao.createOrderItem(orderItem);
                }
                
                CartSession.clearCart();
                showSuccess("Order placed successfully! Your order ID is: " + orderId, getStage());
                navigateToOrders();
            } else {
                showError("Failed to create order. Please check your connection and try again.", getStage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An unexpected error occurred during checkout. Please try again later.", getStage());
        } finally {
            LoadingState.setLoading(checkoutButton, false);
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
    private void navigateToOrders() {
        navigateTo("/com/example/demo/views/customer/order-history.fxml", "Order History - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToProfile() {
        navigateTo("/com/example/demo/views/customer/profile.fxml", "Profile - Smart E-Commerce");
    }
}
