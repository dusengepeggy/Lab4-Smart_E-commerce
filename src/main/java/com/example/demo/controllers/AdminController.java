package com.example.demo.controllers;

import com.example.demo.dao.*;
import com.example.demo.model.OrderWithUser;
import com.example.demo.model.User;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class AdminController extends BaseController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label pendingOrdersLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label totalReviewsLabel;
    @FXML
    private TableView<OrderWithUser> recentOrdersTable;
    @FXML
    private TableColumn<OrderWithUser, Integer> orderIdCol;
    @FXML
    private TableColumn<OrderWithUser, String> customerCol;
    @FXML
    private TableColumn<OrderWithUser, String> dateCol;
    @FXML
    private TableColumn<OrderWithUser, String> amountCol;
    @FXML
    private TableColumn<OrderWithUser, String> statusCol;
    
    private ProductDao productDao;
    private OrderDao orderDao;
    private UserDao userDao;
    private InventoryDao inventoryDao;
    private ReviewDao reviewDao;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return welcomeLabel.getParent().getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        productDao = new ProductDao();
        orderDao = new OrderDao();
        userDao = new UserDao();
        inventoryDao = new InventoryDao();
        reviewDao = new ReviewDao();
        
        loadDashboardData();
        setupRecentOrdersTable();
    }
    
    private void loadDashboardData() {
        try {
            if (UserSession.getLoggedInUser() == null) return;
            
            welcomeLabel.setText("Welcome, " + UserSession.getLoggedInUser().getUsername() + "!");
            
            int totalProducts = productDao.getAllProducts().size();
            totalProductsLabel.setText(String.valueOf(totalProducts));
            
            List<OrderWithUser> allOrders = orderDao.getAllOrdersWithUser();
            totalOrdersLabel.setText(String.valueOf(allOrders.size()));
            
            long pendingCount = allOrders.stream()
                .filter(o -> "Pending".equalsIgnoreCase(o.getStatus()))
                .count();
            pendingOrdersLabel.setText(String.valueOf(pendingCount));
            
            BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> !"Pending".equalsIgnoreCase(o.getStatus()))
                .map(OrderWithUser::getTotal_amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalRevenueLabel.setText("$" + totalRevenue);
            
            int totalUsers = userDao.getAllUsers().size();
            totalUsersLabel.setText(String.valueOf(totalUsers));
            
            List<com.example.demo.model.InventoryWithProduct> inventory = inventoryDao.getAllInventory();
            long lowStockCount = inventory.stream()
                .filter(i -> i.getStock_quantity() < 10)
                .count();
            lowStockLabel.setText(String.valueOf(lowStockCount));
            
            int totalReviews = 0;
            List<com.example.demo.model.Product> products = productDao.getAllProducts();
            for (com.example.demo.model.Product product : products) {
                totalReviews += reviewDao.getReviewCountByProductId(product.getProduct_id());
            }
            totalReviewsLabel.setText(String.valueOf(totalReviews));
            
            List<OrderWithUser> recentOrders = allOrders.stream()
                .limit(10)
                .toList();
            recentOrdersTable.setItems(FXCollections.observableArrayList(recentOrders));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load dashboard data.", getStage());
        }
    }
    
    private void setupRecentOrdersTable() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("username"));
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
    
    @FXML
    private void navigateToReviews() {
        navigateTo("/com/example/demo/views/admin/review-management.fxml", "Review Management - Smart E-Commerce");
    }
}
