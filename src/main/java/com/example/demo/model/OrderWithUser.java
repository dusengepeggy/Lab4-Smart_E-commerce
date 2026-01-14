package com.example.demo.model;

import java.math.BigDecimal;
import java.sql.Date;

public class OrderWithUser {
    private int order_id;
    private int user_id;
    private String username;
    private String user_email;
    private Date order_date;
    private BigDecimal total_amount;
    private String status;

    public OrderWithUser(int order_id, int user_id, String username, String user_email,
                        Date order_date, BigDecimal total_amount, String status) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.username = username;
        this.user_email = user_email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
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
