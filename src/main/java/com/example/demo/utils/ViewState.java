package com.example.demo.utils;

public class ViewState {
    private static Integer selectedProductId;
    private static Integer selectedOrderId;
    
    public static void setSelectedProductId(Integer productId) {
        selectedProductId = productId;
    }
    
    public static Integer getSelectedProductId() {
        return selectedProductId;
    }
    
    public static void clearSelectedProductId() {
        selectedProductId = null;
    }
    
    public static void setSelectedOrderId(Integer orderId) {
        selectedOrderId = orderId;
    }
    
    public static Integer getSelectedOrderId() {
        return selectedOrderId;
    }
    
    public static void clearSelectedOrderId() {
        selectedOrderId = null;
    }
}
