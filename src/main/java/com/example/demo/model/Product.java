package com.example.demo.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Product {
    private int product_id;
    private int category_id;
    private String name;
    private String description;
    private BigDecimal price;
    private Date created_at;

    public Product(int category_id, String name, String description, BigDecimal price) {
        this.category_id = category_id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(int product_id, int category_id, String name, String description, BigDecimal price, Date created_at) {
        this.product_id = product_id;
        this.category_id = category_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.created_at = created_at;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
