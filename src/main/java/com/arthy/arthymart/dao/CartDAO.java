package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.CartItem;
import com.arthy.arthymart.util.DatabaseConnectionListener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAO {

    public List<CartItem> findByUserId(int userId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, p.price AS unit_price, p.name AS product_name, p.image_url "
                + "FROM cart_items ci JOIN products p ON p.id = ci.product_id WHERE ci.user_id = ? ORDER BY ci.id";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setProductName(rs.getString("product_name"));
                    item.setImageUrl(rs.getString("image_url"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public Optional<CartItem> findByUserAndProduct(int userId, int productId) throws SQLException {
        String sql = "SELECT * FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    public void upsert(int userId, int productId, int quantity) throws SQLException {
        Optional<CartItem> existing = findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            String sql = "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";
            try (Connection conn = DatabaseConnectionListener.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantity);
                stmt.setInt(2, userId);
                stmt.setInt(3, productId);
                stmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnectionListener.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, productId);
                stmt.setInt(3, quantity);
                stmt.executeUpdate();
            }
        }
    }

    public boolean remove(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() == 1;
        }
    }

    public void clear(int userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
