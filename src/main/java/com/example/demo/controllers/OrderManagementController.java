package com.example.demo.controllers;

import com.example.demo.dao.OrderDao;
import com.example.demo.dao.OrderItemDao;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItemWithProduct;
import com.example.demo.model.OrderWithUser;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.LoadingState;
import com.example.demo.utils.NavigationUtils;
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
import java.util.stream.Collectors;

public class OrderManagementController extends BaseController {
    @FXML
    private ComboBox<String> statusFilterComboBox;
    @FXML
    private TableView<OrderWithUser> ordersTable;
    @FXML
    private TableColumn<OrderWithUser, Integer> orderIdCol;
    @FXML
    private TableColumn<OrderWithUser, String> customerCol;
    @FXML
    private TableColumn<OrderWithUser, String> emailCol;
    @FXML
    private TableColumn<OrderWithUser, String> dateCol;
    @FXML
    private TableColumn<OrderWithUser, String> amountCol;
    @FXML
    private TableColumn<OrderWithUser, String> statusCol;
    @FXML
    private TableColumn<OrderWithUser, Void> actionsCol;
    @FXML
    private VBox orderDetailContainer;
    @FXML
    private Label orderDetailTitle;
    @FXML
    private VBox orderItemsContainer;
    @FXML
    private Label orderTotalLabel;
    @FXML
    private ComboBox<String> statusComboBox;
    
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
        
        setupStatusFilters();
        setupTable();
        loadOrders();
    }
    
    private void setupStatusFilters() {
        statusFilterComboBox.getItems().addAll("All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled");
        statusFilterComboBox.getSelectionModel().select("All");
        
        statusComboBox.getItems().addAll("Pending", "Processing", "Shipped", "Delivered", "Cancelled");
    }
    
    private void setupTable() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("user_email"));
        dateCol.setCellValueFactory(cellData -> {
            OrderWithUser order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getOrder_date() != null ? order.getOrder_date().toString() : ""
            );
        });
        amountCol.setCellValueFactory(cellData -> {
            OrderWithUser order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty("$" + order.getTotal_amount());
        });
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        actionsCol.setCellFactory(param -> new TableCell<OrderWithUser, Void>() {
            private final Button viewButton = new Button("View Details");
            
            {
                viewButton.setStyle("-fx-background-color: #8e9aaf; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                viewButton.setOnAction(event -> {
                    OrderWithUser order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order.getOrder_id());
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }
    
    private void loadOrders() {
        try {
            List<OrderWithUser> orders = orderDao.getAllOrdersWithUser();
            ordersTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load orders.", getStage());
        }
    }
    
    @FXML
    private void handleFilter() {
        String selectedStatus = statusFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedStatus == null || "All".equals(selectedStatus)) {
            loadOrders();
            return;
        }
        
        List<OrderWithUser> allOrders = orderDao.getAllOrdersWithUser();
        List<OrderWithUser> filtered = allOrders.stream()
            .filter(o -> selectedStatus.equalsIgnoreCase(o.getStatus()))
            .collect(Collectors.toList());
        ordersTable.setItems(FXCollections.observableArrayList(filtered));
    }
    
    private void showOrderDetails(int orderId) {
        orderDetailContainer.setVisible(true);
        orderDetailTitle.setText("Order #" + orderId);
        orderItemsContainer.getChildren().clear();
        
        selectedOrder = orderDao.getOrderById(orderId);
        if (selectedOrder != null) {
            orderTotalLabel.setText("$" + selectedOrder.getTotal_amount());
            statusComboBox.getSelectionModel().select(selectedOrder.getStatus());
        }
        
        List<OrderItemWithProduct> orderItems = orderItemDao.getOrderItemsByOrderIdWithProduct(orderId);
        
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
    }
    
    @FXML
    private void handleUpdateStatus() {
        if (selectedOrder == null) {
            showError("Please select an order first.", getStage());
            return;
        }
        
        String newStatus = statusComboBox.getSelectionModel().getSelectedItem();
        if (newStatus == null) {
            showError("Please select a status.", getStage());
            return;
        }
        
        Button updateButton = (Button) statusComboBox.getScene().lookup(".primary-button");
        
        try {
            if (updateButton != null) {
                LoadingState.setLoading(updateButton, true);
            }
            LoadingState.setLoading(statusComboBox, true);
            
            String result = orderDao.updateOrderStatus(selectedOrder.getOrder_id(), newStatus);
            if ("SUCCESS".equals(result)) {
                showSuccess("Order status updated successfully.", getStage());
                selectedOrder.setStatus(newStatus);
                loadOrders();
            } else {
                showError(result, getStage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update order status.", getStage());
        } finally {
            if (updateButton != null) {
                LoadingState.setLoading(updateButton, false);
            }
            LoadingState.setLoading(statusComboBox, false);
        }
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
    private void navigateToUsers() {
        navigateTo("/com/example/demo/views/admin/user-management.fxml", "User Management - Smart E-Commerce");
    }
    
    @FXML
    private void navigateToReviews() {
        navigateTo("/com/example/demo/views/admin/review-management.fxml", "Review Management - Smart E-Commerce");
    }
}
