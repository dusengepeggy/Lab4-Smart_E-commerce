package com.example.demo.utils;

public class ValidationUtils {
    
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public static String validateLoginFields(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return "Please fill in all fields.";
        }
        
        if (!isValidEmail(email)) {
            return "Please enter a valid email address.";
        }
        
        return null;
    }
    
    public static String validateRegistrationFields(String username, String email, String password, String confirmPassword) {
        if (username == null || username.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            password == null || password.isEmpty() || 
            confirmPassword == null || confirmPassword.isEmpty()) {
            return "Please fill in all fields.";
        }
        
        if (!isValidEmail(email)) {
            return "Please enter a valid email address.";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.";
        }
        
        if (username.trim().length() < MIN_USERNAME_LENGTH) {
            return "Username must be at least " + MIN_USERNAME_LENGTH + " characters long.";
        }
        
        return null;
    }
}
