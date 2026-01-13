package com.example.demo.dao;

import com.example.demo.model.Review;
import com.example.demo.dbConnection.dataConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {
    
    public String createReview(Review review) {
        String sql = "INSERT INTO Review (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, review.getUser_id());
            pstmt.setInt(2, review.getProduct_id());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            
            pstmt.executeUpdate();
            return "SUCCESS";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                return "Invalid user ID or product ID.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public Review getReviewById(int reviewId) {
        String sql = "SELECT * FROM Review WHERE review_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reviewId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Review(
                    rs.getInt("review_id"),
                    rs.getInt("user_id"),
                    rs.getInt("product_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getDate("review_date")
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
    
    public List<Review> getReviewsByProductId(int productId) {
        String sql = "SELECT * FROM Review WHERE product_id = ? ORDER BY review_date DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(new Review(
                    rs.getInt("review_id"),
                    rs.getInt("user_id"),
                    rs.getInt("product_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getDate("review_date")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return reviews;
    }
    
    public List<Review> getReviewsByUserId(int userId) {
        String sql = "SELECT * FROM Review WHERE user_id = ? ORDER BY review_date DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(new Review(
                    rs.getInt("review_id"),
                    rs.getInt("user_id"),
                    rs.getInt("product_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getDate("review_date")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return reviews;
    }
    
    public String updateReview(Review review) {
        String sql = "UPDATE Review SET rating = ?, comment = ? WHERE review_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, review.getRating());
            pstmt.setString(2, review.getComment());
            pstmt.setInt(3, review.getReview_id());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Review not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteReview(int reviewId) {
        String sql = "DELETE FROM Review WHERE review_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reviewId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "Review not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
