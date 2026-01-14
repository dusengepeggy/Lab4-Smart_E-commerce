package com.example.demo.dao;

import org.mindrot.jbcrypt.BCrypt;
import com.example.demo.model.User;
import com.example.demo.dbConnection.dataConnection;
import com.example.demo.utils.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    
    public String registerUser(User user) {
        String sql = "INSERT INTO \"User\" (username, email, password, role) VALUES (?, ?, ?, CAST(? AS user_role))";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (Connection conn = dataConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getRole());

            pstmt.executeUpdate();
            return "SUCCESS";

        } catch (SQLException e) {

            if (e.getSQLState().equals("23505")) {
                return "This email is already registered. Please try logging in.";
            }

            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }

            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }

    public String login(String email, String plainPassword) {
        String sql = "SELECT * FROM \"User\" WHERE email = ?";

        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                if (BCrypt.checkpw(plainPassword, storedHash)) {
                    User userFromDb = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            storedHash,
                            rs.getString("role")
                    );

                    UserSession.setLoggedInUser(userFromDb);
                    return "SUCCESS";
                }
            }
            return "Invalid email or password. Please try again.";
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }

            e.printStackTrace();
            return "An unexpected error occurred during login. Please try again later.";
        }
    }
    
    public User getUserById(int userId) {
        String sql = "SELECT * FROM \"User\" WHERE user_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
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
    
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM \"User\" WHERE email = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
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
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM \"User\" WHERE username = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
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
    
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM \"User\" ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return users;
    }
    
    public List<User> getUsersByRole(String role) {
        String sql = "SELECT * FROM \"User\" WHERE role = CAST(? AS user_role) ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                System.err.println("The server is currently unreachable.");
            }
            e.printStackTrace();
        }
        return users;
    }
    
    public String updateUser(User user) {
        String sql = "UPDATE \"User\" SET username = ?, email = ? WHERE user_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getUser_id());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "User not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return "Username or email already exists.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String updatePassword(int userId, String oldPassword, String newPassword) {
        String sql = "SELECT password FROM \"User\" WHERE user_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                
                if (!BCrypt.checkpw(oldPassword, storedHash)) {
                    return "Current password is incorrect.";
                }
                
                String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                String updateSql = "UPDATE \"User\" SET password = ? WHERE user_id = ?";
                
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                    updatePstmt.setString(1, newHashedPassword);
                    updatePstmt.setInt(2, userId);
                    
                    int rowsAffected = updatePstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        return "SUCCESS";
                    }
                }
            }
            return "User not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String updateRole(int userId, String role) {
        String sql = "UPDATE \"User\" SET role = CAST(? AS user_role) WHERE user_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "User not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
    
    public String deleteUser(int userId) {
        String sql = "DELETE FROM \"User\" WHERE user_id = ?";
        
        try (Connection conn = dataConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "SUCCESS";
            }
            return "User not found.";
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                return "Cannot delete user. User has associated orders.";
            }
            if (e.getSQLState().startsWith("08")) {
                return "The server is currently unreachable. Please check your internet.";
            }
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
