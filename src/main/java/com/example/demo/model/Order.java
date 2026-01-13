package com.example.demo.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Order {
    private int order_id;
    private int user_id;
    private Date order_date;
    private BigDecimal total_amount;
    private String status;

    public Order(int user_id, BigDecimal total_amount, String status) {
        this.user_id = user_id;
        this.total_amount = total_amount;
        this.status = status;
    }

    public Order(int order_id, int user_id, Date order_date, BigDecimal total_amount, String status) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.status = status;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
