package com.example.demo.dao;

import org.mindrot.jbcrypt.BCrypt;
import com.example.demo.model.User;
import com.example.demo.dbConnection.dataConnection;
import com.example.demo.utils.UserSession;

import java.sql.*;

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
}
