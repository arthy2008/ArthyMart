package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.CartDAO;
import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.CartItem;
import com.arthy.arthymart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public List<CartItem> getCart(int userId) throws SQLException {
        return cartDAO.findByUserId(userId);
    }

    public BigDecimal getCartTotal(int userId) throws SQLException {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartDAO.findByUserId(userId)) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }

    public void addToCart(int userId, int productId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (quantity > 1000) {
            throw new IllegalArgumentException("Quantity exceeds maximum allowed.");
        }
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product does not exist: " + productId);
        }
        Product product = productOpt.get();
        Optional<CartItem> existing = cartDAO.findByUserAndProduct(userId, productId);
        int newQty = quantity + existing.map(CartItem::getQuantity).orElse(0);
        if (newQty > product.getStockQty()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + product.getStockQty() + ").");
        }
        cartDAO.upsert(userId, productId, newQty);
    }

    public void updateQuantity(int userId, int productId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product does not exist: " + productId);
        }
        if (quantity > productOpt.get().getStockQty()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock.");
        }
        Optional<CartItem> existing = cartDAO.findByUserAndProduct(userId, productId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Item is not in the cart.");
        }
        cartDAO.upsert(userId, productId, quantity);
    }

    public void removeFromCart(int userId, int productId) throws SQLException {
        cartDAO.remove(userId, productId);
    }

    public void clearCart(int userId) throws SQLException {
        cartDAO.clear(userId);
    }
}
