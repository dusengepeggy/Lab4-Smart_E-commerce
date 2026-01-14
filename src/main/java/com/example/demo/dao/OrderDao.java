package com.example.demo.dao;

import com.example.demo.model.Order;
import com.example.demo.model.OrderWithUser;
import com.example.demo.dbConnection.dataConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    
    public int createOrder(Order order) {
        String sql = "INSERT INTO \"Order\" (user_id, total_amount, status) VALUES (?, ?, CAST(? AS order_status)) RETURNING order_id";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, order.getUser_id());
            pstmt.setBigDecimal(2, order.getTotal_amount());
            pstmt.setString(3, order.getStatus());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("order_id");
            }
            return -1;
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                System.err.println("Invalid user ID.");
            }
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
            return -1;
        }
    }
    
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM \"Order\" WHERE order_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Order(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getDate("order_date"),
                    rs.getBigDecimal("total_amount"),
                    rs.getString("status")
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
    
    public OrderWithUser getOrderByIdWithUser(int orderId) {
        String sql = "SELECT o.order_id, o.user_id, u.username, u.email as user_email, " +
                     "o.order_date, o.total_amount, o.status " +
                     "FROM \"Order\" o " +
                     "INNER JOIN \"User\" u ON o.user_id = u.user_id " +
                     "WHERE o.order_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new OrderWithUser(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("user_email"),
                    rs.getDate("order_date"),
                    rs.getBigDecimal("total_amount"),
                    rs.getString("status")
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
    
    public List<Order> getOrdersByUserId(int userId) {
        String sql = "SELECT * FROM \"Order\" WHERE user_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(new Order(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getDate("order_date"),
                    rs.getBigDecimal("total_amount"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM \"Order\" ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(new Order(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getDate("order_date"),
                    rs.getBigDecimal("total_amount"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<OrderWithUser> getAllOrdersWithUser() {
        String sql = "SELECT o.order_id, o.user_id, u.username, u.email as user_email, " +
                     "o.order_date, o.total_amount, o.status " +
                     "FROM \"Order\" o " +
                     "INNER JOIN \"User\" u ON o.user_id = u.user_id " +
                     "ORDER BY o.order_date DESC";
        List<OrderWithUser> orders = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(new OrderWithUser(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("user_email"),
                    rs.getDate("order_date"),
                    rs.getBigDecimal("total_amount"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return orders;
    }
    
    public String updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE \"Order\" SET status = CAST(? AS order_status) WHERE order_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Order not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String updateOrderTotal(int orderId, BigDecimal totalAmount) {
        String sql = "UPDATE \"Order\" SET total_amount = ? WHERE order_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, totalAmount);
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Order not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteOrder(int orderId) {
        String sql = "DELETE FROM \"Order\" WHERE order_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Order not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
