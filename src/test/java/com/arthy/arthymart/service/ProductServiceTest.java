package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductDAO productDAO;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productDAO = mock(ProductDAO.class);
        productService = new ProductService(productDAO);
    }

    private Product validProduct() {
        Product p = new Product();
        p.setSellerId(2);
        p.setName("Test Product");
        p.setDescription("desc");
        p.setPrice(new BigDecimal("19.99"));
        p.setStockQty(10);
        p.setCategory("Electronics");
        p.setImageUrl("https://example.com/img.png");
        return p;
    }

    @Test
    void testAddProduct_Success() throws SQLException {
        Product p = validProduct();
        when(productDAO.create(any(Product.class))).thenAnswer(inv -> {
            Product arg = inv.getArgument(0);
            arg.setId(7);
            return arg;
        });
        Product created = productService.addProduct(p);
        assertEquals(7, created.getId());
        verify(productDAO, times(1)).create(any(Product.class));
    }

    @Test
    void testAddProduct_EmptyNameRejected() {
        Product p = validProduct();
        p.setName("  ");
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(p));
    }

    @Test
    void testAddProduct_ZeroPriceRejected() {
        Product p = validProduct();
        p.setPrice(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(p));
    }

    @Test
    void testAddProduct_NegativeStockRejected() {
        Product p = validProduct();
        p.setStockQty(-1);
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(p));
    }

    @Test
    void testSearchDelegatesToDAO() throws SQLException {
        when(productDAO.search("mouse", "Electronics")).thenReturn(List.of(validProduct()));
        List<Product> results = productService.searchProducts("mouse", "Electronics");
        assertEquals(1, results.size());
    }

    @Test
    void testGetById() throws SQLException {
        Product p = validProduct();
        p.setId(3);
        when(productDAO.findById(3)).thenReturn(Optional.of(p));
        assertTrue(productService.getProductById(3).isPresent());
    }
}
