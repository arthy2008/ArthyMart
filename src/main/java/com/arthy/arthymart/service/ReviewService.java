package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.OrderDAO;
import com.arthy.arthymart.dao.ReviewDAO;
import com.arthy.arthymart.model.Review;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
        this.orderDAO = new OrderDAO();
    }

    public ReviewService(ReviewDAO reviewDAO, OrderDAO orderDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
    }

    public Review addReview(int userId, int productId, int rating, String comment) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (comment != null && comment.length() > 2000) {
            throw new IllegalArgumentException("Comment is too long (max 2000 characters).");
        }
        if (!orderDAO.hasPurchasedProduct(userId, productId)) {
            throw new IllegalArgumentException("You can only review products you have purchased (delivered/confirmed/shipped orders).");
        }
        if (reviewDAO.existsByUserAndProduct(userId, productId)) {
            throw new IllegalArgumentException("You have already reviewed this product.");
        }
        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment == null ? null : comment.trim());
        return reviewDAO.create(review);
    }

    public List<Review> getReviewsForProduct(int productId) throws SQLException {
        return reviewDAO.findByProductId(productId);
    }

    public double getAverageRating(int productId) throws SQLException {
        return reviewDAO.averageRating(productId);
    }
}
