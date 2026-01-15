package com.example.demo.utils;

import com.example.demo.model.Product;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CartSession {
    private static final Map<Integer, CartItem> cartItems = new HashMap<>();
    
    public static class CartItem {
        private Product product;
        private int quantity;
        private BigDecimal unitPrice;
        
        public CartItem(Product product, int quantity, BigDecimal unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getSubtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
    }
    
    public static void addToCart(Product product, int quantity) {
        int productId = product.getProduct_id();
        if (cartItems.containsKey(productId)) {
            CartItem item = cartItems.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            cartItems.put(productId, new CartItem(product, quantity, product.getPrice()));
        }
    }
    
    public static void removeFromCart(int productId) {
        cartItems.remove(productId);
    }
    
    public static void updateQuantity(int productId, int quantity) {
        if (cartItems.containsKey(productId)) {
            if (quantity <= 0) {
                removeFromCart(productId);
            } else {
                cartItems.get(productId).setQuantity(quantity);
            }
        }
    }
    
    public static Map<Integer, CartItem> getCartItems() {
        return new HashMap<>(cartItems);
    }
    
    public static int getCartItemCount() {
        return cartItems.values().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
    
    public static BigDecimal getTotalAmount() {
        return cartItems.values().stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public static void clearCart() {
        cartItems.clear();
    }
}
