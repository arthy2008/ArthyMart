package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.dao.WishlistDAO;
import com.arthy.arthymart.model.Product;
import com.arthy.arthymart.model.WishlistItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class WishlistService {

    private final WishlistDAO wishlistDAO;
    private final ProductDAO productDAO;

    public WishlistService() {
        this.wishlistDAO = new WishlistDAO();
        this.productDAO = new ProductDAO();
    }

    public WishlistService(WishlistDAO wishlistDAO, ProductDAO productDAO) {
        this.wishlistDAO = wishlistDAO;
        this.productDAO = productDAO;
    }

    public List<WishlistItem> getWishlist(int userId) throws SQLException {
        return wishlistDAO.findByUserId(userId);
    }

    public boolean isWishlisted(int userId, int productId) throws SQLException {
        return wishlistDAO.isWishlisted(userId, productId);
    }

    public boolean addToWishlist(int userId, int productId) throws SQLException {
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product does not exist: " + productId);
        }
        return wishlistDAO.add(userId, productId);
    }

    public boolean removeFromWishlist(int userId, int productId) throws SQLException {
        return wishlistDAO.remove(userId, productId);
    }

    public void clearWishlist(int userId) throws SQLException {
        wishlistDAO.clear(userId);
    }
}
