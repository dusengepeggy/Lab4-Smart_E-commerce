package com.example.demo.dao;

import com.example.demo.model.OrderItem;
import com.example.demo.dbConnection.dataConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDao {
    
    public String createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO OrderItem (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderItem.getOrder_id());
            pstmt.setInt(2, orderItem.getProduct_id());
            pstmt.setInt(3, orderItem.getQuantity());
            pstmt.setBigDecimal(4, orderItem.getUnit_price());
            
            pstmt.executeUpdate();
            return "SUCCESS";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                return "Invalid order ID or product ID.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public OrderItem getOrderItemById(int orderItemId) {
        String sql = "SELECT * FROM OrderItem WHERE order_item_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderItemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("unit_price")
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
    
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        String sql = "SELECT * FROM OrderItem WHERE order_id = ? ORDER BY order_item_id";
        List<OrderItem> orderItems = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orderItems.add(new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("unit_price")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return orderItems;
    }
    
    public String updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE OrderItem SET quantity = ?, unit_price = ? WHERE order_item_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderItem.getQuantity());
            pstmt.setBigDecimal(2, orderItem.getUnit_price());
            pstmt.setInt(3, orderItem.getOrder_item_id());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Order item not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteOrderItem(int orderItemId) {
        String sql = "DELETE FROM OrderItem WHERE order_item_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderItemId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Order item not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteOrderItemsByOrderId(int orderId) {
        String sql = "DELETE FROM OrderItem WHERE order_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
            return "SUCCESS";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
