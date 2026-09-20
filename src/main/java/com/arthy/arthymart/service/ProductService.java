package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Product;

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

    public Product addProduct(Product product) throws SQLException, IllegalArgumentException {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        if (product.getStockQty() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        return productDAO.create(product);
    }
}
