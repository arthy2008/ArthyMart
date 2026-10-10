package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.WishlistItem;
import com.arthy.arthymart.util.DatabaseConnectionListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    public List<WishlistItem> findByUserId(int userId) throws SQLException {
        List<WishlistItem> items = new ArrayList<>();
        String sql = "SELECT w.id, w.user_id, w.product_id, w.created_at, "
                + "p.name AS product_name, p.description AS product_desc, p.price, "
                + "p.category, p.image_url, p.stock_qty "
                + "FROM wishlist w "
                + "JOIN products p ON p.id = w.product_id "
                + "WHERE w.user_id = ? "
                + "ORDER BY w.created_at DESC";

        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductDescription(rs.getString("product_desc"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setCategory(rs.getString("category"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setStockQty(rs.getInt("stock_qty"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public boolean isWishlisted(int userId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean add(int userId, int productId) throws SQLException {
        if (isWishlisted(userId, productId)) {
            return false;
        }
        String sql = "INSERT INTO wishlist (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public boolean remove(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public void clear(int userId) throws SQLException {
        String sql = "DELETE FROM wishlist WHERE user_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
