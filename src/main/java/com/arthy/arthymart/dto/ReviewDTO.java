package com.arthy.arthymart.dto;

import com.arthy.arthymart.model.Review;

import java.sql.Timestamp;

public class ReviewDTO {
    private int id;
    private int productId;
    private int userId;
    private String userName;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public ReviewDTO() {}

    public ReviewDTO(int id, int productId, int userId, String userName, int rating, String comment, Timestamp createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static ReviewDTO fromReview(Review r) {
        if (r == null) return null;
        return new ReviewDTO(r.getId(), r.getProductId(), r.getUserId(), r.getUserName(), r.getRating(), r.getComment(), r.getCreatedAt());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
