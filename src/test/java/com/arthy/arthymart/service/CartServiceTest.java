package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.CartDAO;
import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.CartItem;
import com.arthy.arthymart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartDAO = mock(CartDAO.class);
        productDAO = mock(ProductDAO.class);
        cartService = new CartService(cartDAO, productDAO);
    }

    private Product product(int stock) {
        Product p = new Product();
        p.setId(10);
        p.setName("Widget");
        p.setPrice(new BigDecimal("9.99"));
        p.setStockQty(stock);
        return p;
    }

    @Test
    void testAddToCart_Success() throws SQLException {
        when(productDAO.findById(10)).thenReturn(Optional.of(product(5)));
        when(cartDAO.findByUserAndProduct(1, 10)).thenReturn(Optional.empty());
        cartService.addToCart(1, 10, 2);
        verify(cartDAO, times(1)).upsert(1, 10, 2);
    }

    @Test
    void testAddToCart_ExceedsStockRejected() throws SQLException {
        when(productDAO.findById(10)).thenReturn(Optional.of(product(2)));
        when(cartDAO.findByUserAndProduct(1, 10)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(1, 10, 5));
        verify(cartDAO, never()).upsert(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testAddToCart_ZeroQuantityRejected() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(1, 10, 0));
    }

    @Test
    void testAddToCart_MissingProductRejected() throws SQLException {
        when(productDAO.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(1, 99, 1));
    }

    @Test
    void testCartTotal_ServerSidePricing() throws SQLException {
        CartItem a = new CartItem();
        a.setProductId(10);
        a.setQuantity(2);
        a.setUnitPrice(new BigDecimal("9.99"));
        CartItem b = new CartItem();
        b.setProductId(11);
        b.setQuantity(1);
        b.setUnitPrice(new BigDecimal("5.00"));
        when(cartDAO.findByUserId(1)).thenReturn(List.of(a, b));
        assertEquals(new BigDecimal("24.98"), cartService.getCartTotal(1));
    }
}
