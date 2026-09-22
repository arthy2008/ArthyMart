package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.Review;
import com.arthy.arthymart.util.DatabaseConnectionListener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public Review create(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getProductId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getInt(1));
                }
            }
        }
        return review;
    }

    public List<Review> findByProductId(int productId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS user_name FROM reviews r JOIN users u ON u.id = r.user_id "
                + "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }
        }
        return reviews;
    }

    public boolean existsByUserAndProduct(int userId, int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public double averageRating(int productId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM reviews WHERE product_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setProductId(rs.getInt("product_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        try { review.setUserName(rs.getString("user_name")); } catch (SQLException ignored) {}
        return review;
    }
}
