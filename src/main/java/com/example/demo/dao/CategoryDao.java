package com.example.demo.dao;

import com.example.demo.model.Category;
import com.example.demo.dbConnection.dataConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    
    public String createCategory(Category category) {
        String sql = "INSERT INTO Category (category_name, description) VALUES (?, ?)";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getCategory_name());
            pstmt.setString(2, category.getDescription());
            
            pstmt.executeUpdate();
            return "SUCCESS";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return "Category name already exists.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM Category WHERE category_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description")
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
    
    public List<Category> getAllCategories() {
        String sql = "SELECT * FROM Category ORDER BY category_name";
        List<Category> categories = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return categories;
    }
    
    public String updateCategory(Category category) {
        String sql = "UPDATE Category SET category_name = ?, description = ? WHERE category_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getCategory_name());
            pstmt.setString(2, category.getDescription());
            pstmt.setInt(3, category.getCategory_id());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Category not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return "Category name already exists.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteCategory(int categoryId) {
        String sql = "DELETE FROM Category WHERE category_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Category not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
