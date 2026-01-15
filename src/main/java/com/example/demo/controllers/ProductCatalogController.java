package com.example.demo.controllers;

import com.example.demo.dao.CategoryDao;
import com.example.demo.dao.ProductDao;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.ProductWithCategory;
import com.example.demo.utils.CartSession;
import com.example.demo.utils.ErrorHandler;
import com.example.demo.utils.NavigationUtils;
import com.example.demo.utils.ViewState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductCatalogController extends BaseController {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> categoryListView;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private FlowPane productsContainer;
    @FXML
    private Label resultsLabel;
    @FXML
    private Label cartBadgeLabel;
    @FXML
    private Button cartButton;
    
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private List<ProductWithCategory> allProducts;
    
    @Override
    protected javafx.scene.Node getRootNode() {
        return searchField.getParent().getParent().getParent();
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        productDao = new ProductDao();
        categoryDao = new CategoryDao();
        
        loadCategories();
        loadAllProducts();
        setupSortComboBox();
        updateCartBadge();
    }
    
    private void setupSortComboBox() {
        sortComboBox.getItems().addAll("Name (A-Z)", "Name (Z-A)", "Price (Low to High)", "Price (High to Low)");
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sortProducts(newVal);
            }
        });
    }
    
    private void loadCategories() {
        try {
            List<Category> categories = categoryDao.getAllCategories();
            categoryListView.setItems(FXCollections.observableArrayList(categories.stream().map(Category::getCategory_name).collect(Collectors.toList())));
            categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    Optional<Integer> id = categories.stream()
                            .filter(c -> c.getCategory_name() != null && c.getCategory_name().equals(newVal))
                            .map(Category::getCategory_id)
                            .findFirst();
                    if (id.isPresent()) {
                        filterByCategory(id.get());
                    }
                } else {
                    loadAllProducts();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load categories. Please refresh the page.", getStage());
        }
    }
    
    private void loadAllProducts() {
        try {
            allProducts = productDao.getAllProductsWithCategory();
            displayProducts(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load products. Please check your connection and try again.", getStage());
        }
    }
    
    private void displayProducts(List<ProductWithCategory> products) {
        productsContainer.getChildren().clear();
        resultsLabel.setText(products.size() + " product(s) found");
        
        for (ProductWithCategory product : products) {
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
                
                productsContainer.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
//    private void setupSortComboBox() {
//        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal != null) {
//                sortProducts(newVal);
//            }
//        });
//    }
    
    private void sortProducts(String sortOption) {
        List<ProductWithCategory> sorted = allProducts.stream()
            .sorted((p1, p2) -> {
                switch (sortOption) {
                    case "Name (A-Z)":
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    case "Name (Z-A)":
                        return p2.getName().compareToIgnoreCase(p1.getName());
                    case "Price (Low to High)":
                        return p1.getPrice().compareTo(p2.getPrice());
                    case "Price (High to Low)":
                        return p2.getPrice().compareTo(p1.getPrice());
                    default:
                        return 0;
                }
            })
            .collect(Collectors.toList());
        displayProducts(sorted);
    }
    
    @FXML
    private void handleSearch() {
        try {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                displayProducts(allProducts);
                return;
            }
            
            List<Product> products = productDao.searchProductsByName(searchTerm);
            List<ProductWithCategory> productsWithCategory = products.stream()
                .map(p -> {
                    ProductWithCategory pwc = allProducts.stream()
                        .filter(pc -> pc.getProduct_id() == p.getProduct_id())
                        .findFirst()
                        .orElse(null);
                    return pwc != null ? pwc : new ProductWithCategory(
                        p.getProduct_id(), p.getCategory_id(), "Unknown",
                        p.getName(), p.getDescription(), p.getPrice(), p.getCreated_at()
                    );
                })
                .collect(Collectors.toList());
            
            displayProducts(productsWithCategory);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Search failed. Please check your connection and try again.", getStage());
        }
    }
    
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        categoryListView.getSelectionModel().clearSelection();
        minPriceField.clear();
        maxPriceField.clear();
        loadAllProducts();
    }
    
    @FXML
    private void handlePriceFilter() {
        try {
            BigDecimal minPrice = minPriceField.getText().isEmpty() ? 
                BigDecimal.ZERO : new BigDecimal(minPriceField.getText());
            BigDecimal maxPrice = maxPriceField.getText().isEmpty() ? 
                new BigDecimal("999999") : new BigDecimal(maxPriceField.getText());
            
            List<Product> products = productDao.getProductsByPriceRange(minPrice, maxPrice);
            List<ProductWithCategory> filtered = products.stream()
                .map(p -> allProducts.stream()
                    .filter(pc -> pc.getProduct_id() == p.getProduct_id())
                    .findFirst()
                    .orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toList());
            
            displayProducts(filtered);
        } catch (NumberFormatException e) {
            showError("Please enter valid price values.", getStage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to filter products by price.", getStage());
        }
    }
    
    private void filterByCategory(int categoryId) {
        List<ProductWithCategory> filtered = productDao.getProductsByCategoryWithCategory(categoryId);
        displayProducts(filtered);
    }
    
    private void updateCartBadge() {
        int count = CartSession.getCartItemCount();
        cartBadgeLabel.setText("(" + count + ")");
        cartBadgeLabel.setVisible(true);
    }
    
    private void navigateToProductDetail(int productId) {
        ViewState.setSelectedProductId(productId);
        navigateTo("/com/example/demo/views/customer/product-detail.fxml", "Product Details - Smart E-Commerce");
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
