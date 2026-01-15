package com.example.demo.controllers;

import com.example.demo.dao.OrderDao;
import com.example.demo.dao.OrderItemDao;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItemWithProduct;
import com.example.demo.utils.CartSession;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import com.example.demo.utils.ViewState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class OrderHistoryController extends BaseController {
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, String> dateColumn;
    @FXML
    private TableColumn<Order, String> totalColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, Void> actionsColumn;
    @FXML
    private VBox orderDetailContainer;
    @FXML
    private Label orderDetailTitle;
    @FXML
    private VBox orderItemsContainer;
    @FXML
    private Label orderTotalLabel;
    @FXML
    private Label cartBadgeLabel;
    @FXML
    private Button cancelOrderButton;
    
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;
    private Order selectedOrder;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return ordersTable.getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        orderDao = new OrderDao();
        orderItemDao = new OrderItemDao();
        
        setupTable();
        loadOrders();
        updateCartBadge();
        
        Integer selectedOrderId = ViewState.getSelectedOrderId();
        if (selectedOrderId != null) {
            showOrderDetails(selectedOrderId);
            ViewState.clearSelectedOrderId();
        }
    }
    
    private void setupTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        dateColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getOrder_date() != null ? order.getOrder_date().toString() : ""
            );
        });
        totalColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty("$" + order.getTotal_amount());
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        actionsColumn.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button viewButton = new Button("View Details");
            private final Button cancelButton = new Button("Cancel");
            private final HBox buttonBox = new HBox(8);
            
            {
                viewButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                cancelButton.setStyle("-fx-background-color:rgb(177, 8, 8); -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                
                viewButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order.getOrder_id());
                });
                
                cancelButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleCancelOrderFromTable(order);
                });
                
                buttonBox.getChildren().addAll(viewButton, cancelButton);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    String status = order.getStatus();
                    // Show cancel button only for Pending or Processing orders
                    if ("Pending".equals(status) || "Processing".equals(status)) {
                        cancelButton.setVisible(true);
                    } else {
                        cancelButton.setVisible(false);
                    }
                    setGraphic(buttonBox);
                }
            }
        });
    }
    
    private void loadOrders() {
        try {
            if (UserSession.getLoggedInUser() == null) return;
            
            int userId = UserSession.getLoggedInUser().getUser_id();
            List<Order> orders = orderDao.getOrdersByUserId(userId);
            ordersTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load orders.", getStage());
        }
    }
    
    private void showOrderDetails(int orderId) {
        try {
            orderDetailContainer.setVisible(true);
            orderDetailTitle.setText("Order #" + orderId);
            orderItemsContainer.getChildren().clear();
            
            List<OrderItemWithProduct> orderItems = orderItemDao.getOrderItemsByOrderIdWithProduct(orderId);
            selectedOrder = orderDao.getOrderById(orderId);
            
            if (selectedOrder != null) {
                orderTotalLabel.setText("$" + selectedOrder.getTotal_amount());
                
                // Show cancel button only if order is Pending or Processing
                String status = selectedOrder.getStatus();
                if (cancelOrderButton != null) {
                    if ("Pending".equals(status) || "Processing".equals(status)) {
                        cancelOrderButton.setVisible(true);
                    } else {
                        cancelOrderButton.setVisible(false);
                    }
                }
            }
            
            for (OrderItemWithProduct item : orderItems) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/components/order-item-card.fxml"));
                    HBox itemCard = loader.load();
                
                Label productNameLabel = (Label) itemCard.lookup("#productNameLabel");
                Label quantityLabel = (Label) itemCard.lookup("#quantityLabel");
                Label unitPriceLabel = (Label) itemCard.lookup("#unitPriceLabel");
                Label subtotalLabel = (Label) itemCard.lookup("#subtotalLabel");
                Button actionButton = (Button) itemCard.lookup("#actionButton");
                Button removeButton = (Button) itemCard.lookup("#removeButton");
                
                if (productNameLabel != null) productNameLabel.setText(item.getProduct_name());
                if (quantityLabel != null) quantityLabel.setText("Qty: " + item.getQuantity());
                if (unitPriceLabel != null) unitPriceLabel.setText("Unit: $" + item.getUnit_price());
                if (subtotalLabel != null) subtotalLabel.setText("Subtotal: $" + item.getSubtotal());
                if (actionButton != null) actionButton.setVisible(false);
                if (removeButton != null) removeButton.setVisible(false);
                
                orderItemsContainer.getChildren().add(itemCard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load order details.", getStage());
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
    private void navigateToProfile() {
        navigateTo("/com/example/demo/views/customer/profile.fxml", "Profile - Smart E-Commerce");
    }
    
    @FXML
    private void handleCancelOrder() {
        if (selectedOrder == null) {
            showError("No order selected. Please select an order first.", getStage());
            return;
        }
        
        cancelOrder(selectedOrder);
    }
    
    private void handleCancelOrderFromTable(Order order) {
        cancelOrder(order);
    }
    
    private void cancelOrder(Order order) {
        String currentStatus = order.getStatus();
        if (!"Pending".equals(currentStatus) && !"Processing".equals(currentStatus)) {
            showError("Only orders with 'Pending' or 'Processing' status can be cancelled.", getStage());
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Order");
        confirmAlert.setHeaderText("Are you sure you want to cancel this order?");
        confirmAlert.setContentText("Order #" + order.getOrder_id() + " will be cancelled. This action cannot be undone.");
        confirmAlert.initOwner(getStage());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String result = orderDao.updateOrderStatus(order.getOrder_id(), "Cancelled");
                    if ("SUCCESS".equals(result)) {
                        showSuccess("Order #" + order.getOrder_id() + " has been cancelled successfully.", getStage());
                        order.setStatus("Cancelled");
                        loadOrders();
                        if (orderDetailContainer != null) {
                            orderDetailContainer.setVisible(false);
                        }
                    } else {
                        showError("Failed to cancel order: " + result, getStage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("An error occurred while cancelling the order. Please try again.", getStage());
                }
            }
        });
    }
}
