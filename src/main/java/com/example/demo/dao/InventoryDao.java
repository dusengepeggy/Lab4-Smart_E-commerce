package com.example.demo.dao;

import com.example.demo.model.Inventory;
import com.example.demo.dbConnection.dataConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDao {
    
    public String createInventory(Inventory inventory) {
        String sql = "INSERT INTO Inventory (product_id, stock_quantity, warehouse_location) VALUES (?, ?, ?)";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, inventory.getProduct_id());
            pstmt.setInt(2, inventory.getStock_quantity());
            pstmt.setString(3, inventory.getWarehouse_location());
            
            pstmt.executeUpdate();
            return "SUCCESS";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return "Inventory record already exists for this product.";
            }
            if (e.getSQLState().equals("23503")) {
                return "Invalid product ID.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public Inventory getInventoryByProductId(int productId) {
        String sql = "SELECT * FROM Inventory WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Inventory(
                    rs.getInt("inventory_id"),
                    rs.getInt("product_id"),
                    rs.getInt("stock_quantity"),
                    rs.getString("warehouse_location"),
                    rs.getTimestamp("updated_at")
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
    
    public List<Inventory> getAllInventory() {
        String sql = "SELECT * FROM Inventory ORDER BY product_id";
        List<Inventory> inventories = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                inventories.add(new Inventory(
                    rs.getInt("inventory_id"),
                    rs.getInt("product_id"),
                    rs.getInt("stock_quantity"),
                    rs.getString("warehouse_location"),
                    rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return inventories;
    }
    
    public String updateStockQuantity(int productId, int newQuantity) {
        String sql = "UPDATE Inventory SET stock_quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Inventory record not found for this product.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String updateInventory(Inventory inventory) {
        String sql = "UPDATE Inventory SET stock_quantity = ?, warehouse_location = ?, updated_at = CURRENT_TIMESTAMP WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, inventory.getStock_quantity());
            pstmt.setString(2, inventory.getWarehouse_location());
            pstmt.setInt(3, inventory.getProduct_id());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Inventory record not found for this product.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteInventory(int productId) {
        String sql = "DELETE FROM Inventory WHERE product_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Inventory record not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
