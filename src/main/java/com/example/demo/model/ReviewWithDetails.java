package com.example.demo.model;

import java.sql.Date;

public class ReviewWithDetails {
    private int review_id;
    private int user_id;
    private String username;
    private int product_id;
    private String product_name;
    private int rating;
    private String comment;
    private Date review_date;

    public ReviewWithDetails(int review_id, int user_id, String username, int product_id,
                            String product_name, int rating, String comment, Date review_date) {
        this.review_id = review_id;
        this.user_id = user_id;
        this.username = username;
        this.product_id = product_id;
        this.product_name = product_name;
        this.rating = rating;
        this.comment = comment;
        this.review_date = review_date;
    }

    public int getReview_id() {
        return review_id;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReview_date() {
        return review_date;
    }

    public void setReview_date(Date review_date) {
        this.review_date = review_date;
    }
}
