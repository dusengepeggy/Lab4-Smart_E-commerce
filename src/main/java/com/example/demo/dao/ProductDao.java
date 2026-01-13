package com.example.demo.dao;

import com.example.demo.model.Product;
import com.example.demo.dbConnection.dataConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    
    public String createProduct(Product product) {
        String sql = "INSERT INTO Product (category_id, name, description, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, product.getCategory_id());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setBigDecimal(4, product.getPrice());
            
            pstmt.executeUpdate();
            return "SUCCESS";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                return "Invalid category ID.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM Product WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getInt("product_id"),
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getDate("created_at")
                );
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM Product ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getDate("created_at")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return products;
    }
    
    public List<Product> getProductsByCategory(int categoryId) {
        String sql = "SELECT * FROM Product WHERE category_id = ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getDate("created_at")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return products;
    }
    
    public String updateProduct(Product product) {
        String sql = "UPDATE Product SET category_id = ?, name = ?, description = ?, price = ? WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, product.getCategory_id());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getProduct_id());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Product not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                return "Invalid category ID.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteProduct(int productId) {
        String sql = "DELETE FROM Product WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Product not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
