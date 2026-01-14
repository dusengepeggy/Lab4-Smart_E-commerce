package com.example.demo.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InventoryWithProduct {
    private int inventory_id;
    private int product_id;
    private String product_name;
    private BigDecimal product_price;
    private String product_description;
    private int stock_quantity;
    private String warehouse_location;
    private Timestamp updated_at;

    public InventoryWithProduct(int inventory_id, int product_id, String product_name, 
                                BigDecimal product_price, String product_description,
                                int stock_quantity, String warehouse_location, Timestamp updated_at) {
        this.inventory_id = inventory_id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        this.stock_quantity = stock_quantity;
        this.warehouse_location = warehouse_location;
        this.updated_at = updated_at;
    }

    public int getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(int inventory_id) {
        this.inventory_id = inventory_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public BigDecimal getProduct_price() {
        return product_price;
    }

    public void setProduct_price(BigDecimal product_price) {
        this.product_price = product_price;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public String getWarehouse_location() {
        return warehouse_location;
    }

    public void setWarehouse_location(String warehouse_location) {
        this.warehouse_location = warehouse_location;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
}
