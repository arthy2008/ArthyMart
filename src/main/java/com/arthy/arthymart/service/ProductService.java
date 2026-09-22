package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    public Optional<Product> getProductById(int id) throws SQLException {
        return productDAO.findById(id);
    }

    public List<Product> getProductsBySeller(int sellerId) throws SQLException {
        return productDAO.findBySellerId(sellerId);
    }

    public List<Product> searchProducts(String query, String category) throws SQLException {
        return productDAO.search(query, category);
    }

    public Product addProduct(Product product) throws SQLException, IllegalArgumentException {
        validate(product);
        return productDAO.create(product);
    }

    public void updateProduct(Product product) throws SQLException {
        validate(product);
        boolean updated = productDAO.update(product);
        if (!updated) {
            throw new IllegalArgumentException("Product not found or you are not the owner.");
        }
    }

    public void deleteProduct(int productId, int sellerId) throws SQLException {
        boolean deleted = productDAO.delete(productId, sellerId);
        if (!deleted) {
            throw new IllegalArgumentException("Product not found or you are not the owner.");
        }
    }

    public void validate(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (product.getName().trim().length() > 150) {
            throw new IllegalArgumentException("Product name is too long (max 150).");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        if (product.getPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("Product price exceeds maximum allowed.");
        }
        if (product.getStockQty() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (product.getImageUrl() != null && product.getImageUrl().length() > 500) {
            throw new IllegalArgumentException("Image URL is too long (max 500).");
        }
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            String url = product.getImageUrl().trim();
            if (!(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"))) {
                throw new IllegalArgumentException("Image URL must start with http://, https:// or /.");
            }
        }
    }
}
