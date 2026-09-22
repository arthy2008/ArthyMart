package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.util.DatabaseConnectionListener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public Order createOrderWithItems(Order order, List<OrderItem> items) throws SQLException {
        String insertOrderSql = "INSERT INTO orders (buyer_id, total_amount, status) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        String decrementStockSql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";

        Connection conn = null;
        try {
            conn = DatabaseConnectionListener.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getBuyerId());
                orderStmt.setBigDecimal(2, order.getTotalAmount());
                orderStmt.setString(3, order.getStatus().name());
                orderStmt.executeUpdate();

                try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        order.setId(rs.getInt(1));
                    }
                }
            }

            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql);
                 PreparedStatement stockStmt = conn.prepareStatement(decrementStockSql)) {
                for (OrderItem item : items) {
                    itemStmt.setInt(1, order.getId());
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setBigDecimal(4, item.getPricePerUnit());
                    itemStmt.addBatch();

                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setInt(2, item.getProductId());
                    stockStmt.setInt(3, item.getQuantity());
                    stockStmt.addBatch();
                }
                itemStmt.executeBatch();
                int[] stockResults = stockStmt.executeBatch();
                for (int r : stockResults) {
                    if (r == 0) {
                        throw new SQLException("Insufficient stock during checkout (concurrent update).");
                    }
                }
            }

            // Clear buyer's cart atomically as part of checkout
            try (PreparedStatement clearCart = conn.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")) {
                clearCart.setInt(1, order.getBuyerId());
                clearCart.executeUpdate();
            }

            conn.commit();
            order.setItems(items);
            return order;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                conn.close();
            }
        }
    }

    public List<Order> findByBuyerId(int buyerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        }
        return orders;
    }

    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
        }
        return orders;
    }

    /** Orders containing at least one product sold by the given seller. */
    public List<Order> findBySellerId(int sellerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT DISTINCT o.* FROM orders o JOIN order_items oi ON oi.order_id = o.id "
                + "JOIN products p ON p.id = oi.product_id WHERE p.seller_id = ? ORDER BY o.created_at DESC";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        }
        return orders;
    }

    public Order findById(int id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRow(rs);
                    order.setItems(findItemsByOrderId(order.getId()));
                    return order;
                }
            }
        }
        return null;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPricePerUnit(rs.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public boolean updateStatus(int orderId, Order.Status status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() == 1;
        }
    }

    /** Has the buyer purchased the product in a DELIVERED (or CONFIRMED/SHIPPED) order? Used for review eligibility. */
    public boolean hasPurchasedProduct(int buyerId, int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders o JOIN order_items oi ON oi.order_id = o.id "
                + "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status IN ('DELIVERED','CONFIRMED','SHIPPED')";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setBuyerId(rs.getInt("buyer_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(Order.Status.valueOf(rs.getString("status")));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }
}
