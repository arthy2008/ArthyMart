package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.Product;
import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOTest {

    private ProductDAO productDAO;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:productdaotest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        productDAO = new ProductDAO();
    }

    @Test
    void testCreateFindUpdateDeleteProduct() throws SQLException {
        Product p = new Product();
        p.setSellerId(2);
        p.setName("Gaming Monitor");
        p.setDescription("144Hz 27-inch IPS panel");
        p.setPrice(new BigDecimal("249.99"));
        p.setStockQty(15);
        p.setCategory("Electronics");
        p.setImageUrl("https://example.com/monitor.jpg");

        Product created = productDAO.create(p);
        assertTrue(created.getId() > 0);

        Optional<Product> found = productDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Gaming Monitor", found.get().getName());
        assertEquals(new BigDecimal("249.99"), found.get().getPrice());
        assertEquals(15, found.get().getStockQty());

        // Update
        found.get().setPrice(new BigDecimal("229.99"));
        found.get().setStockQty(10);
        boolean updated = productDAO.update(found.get());
        assertTrue(updated);

        Optional<Product> afterUpdate = productDAO.findById(created.getId());
        assertTrue(afterUpdate.isPresent());
        assertEquals(new BigDecimal("229.99"), afterUpdate.get().getPrice());
        assertEquals(10, afterUpdate.get().getStockQty());

        // Delete
        boolean deleted = productDAO.delete(created.getId(), 2);
        assertTrue(deleted);
        assertTrue(productDAO.findById(created.getId()).isEmpty());
    }

    @Test
    void testSearchAndFilter() throws SQLException {
        List<Product> searchResults = productDAO.search("mouse", "Electronics");
        assertFalse(searchResults.isEmpty());
        assertTrue(searchResults.stream().anyMatch(p -> p.getName().toLowerCase().contains("mouse")));

        List<Product> emptySearch = productDAO.search("NonexistentProductXYZ123", null);
        assertTrue(emptySearch.isEmpty());

        List<Product> bySeller = productDAO.findBySellerId(2);
        assertFalse(bySeller.isEmpty());
    }

    @Test
    void testAdminDelete() throws SQLException {
        Product p = new Product();
        p.setSellerId(2);
        p.setName("Admin Removal Candidate");
        p.setDescription("Test listing");
        p.setPrice(new BigDecimal("10.00"));
        p.setStockQty(5);
        p.setCategory("Miscellaneous");

        Product created = productDAO.create(p);
        boolean adminDeleted = productDAO.deleteByAdmin(created.getId());
        assertTrue(adminDeleted);
        assertTrue(productDAO.findById(created.getId()).isEmpty());
    }
}
