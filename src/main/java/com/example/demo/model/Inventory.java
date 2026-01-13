package com.example.demo.model;

import java.sql.Timestamp;

public class Inventory {
    private int inventory_id;
    private int product_id;
    private int stock_quantity;
    private String warehouse_location;
    private Timestamp updated_at;

    public Inventory(int product_id, int stock_quantity, String warehouse_location) {
        this.product_id = product_id;
        this.stock_quantity = stock_quantity;
        this.warehouse_location = warehouse_location;
    }

    public Inventory(int inventory_id, int product_id, int stock_quantity, String warehouse_location, Timestamp updated_at) {
        this.inventory_id = inventory_id;
        this.product_id = product_id;
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
