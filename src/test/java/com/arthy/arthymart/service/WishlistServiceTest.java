package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.dao.WishlistDAO;
import com.arthy.arthymart.model.Product;
import com.arthy.arthymart.model.WishlistItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WishlistServiceTest {

    private WishlistDAO wishlistDAO;
    private ProductDAO productDAO;
    private WishlistService wishlistService;

    @BeforeEach
    void setUp() {
        wishlistDAO = mock(WishlistDAO.class);
        productDAO = mock(ProductDAO.class);
        wishlistService = new WishlistService(wishlistDAO, productDAO);
    }

    private Product sampleProduct(int id) {
        Product p = new Product();
        p.setId(id);
        p.setName("Test Product");
        p.setPrice(new BigDecimal("49.99"));
        p.setStockQty(10);
        return p;
    }

    @Test
    void testAddToWishlist_Success() throws SQLException {
        when(productDAO.findById(10)).thenReturn(Optional.of(sampleProduct(10)));
        when(wishlistDAO.add(1, 10)).thenReturn(true);

        boolean added = wishlistService.addToWishlist(1, 10);
        assertTrue(added);
        verify(wishlistDAO, times(1)).add(1, 10);
    }

    @Test
    void testAddToWishlist_ProductNotFound() throws SQLException {
        when(productDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> wishlistService.addToWishlist(1, 999));
        verify(wishlistDAO, never()).add(1, 999);
    }

    @Test
    void testIsWishlisted() throws SQLException {
        when(wishlistDAO.isWishlisted(1, 10)).thenReturn(true);
        when(wishlistDAO.isWishlisted(1, 20)).thenReturn(false);

        assertTrue(wishlistService.isWishlisted(1, 10));
        assertFalse(wishlistService.isWishlisted(1, 20));
    }

    @Test
    void testGetWishlist() throws SQLException {
        WishlistItem item = new WishlistItem();
        item.setId(1);
        item.setProductId(10);
        item.setUserId(1);
        item.setProductName("Test Item");

        when(wishlistDAO.findByUserId(1)).thenReturn(List.of(item));

        List<WishlistItem> list = wishlistService.getWishlist(1);
        assertEquals(1, list.size());
        assertEquals("Test Item", list.get(0).getProductName());
    }

    @Test
    void testRemoveAndClearWishlist() throws SQLException {
        when(wishlistDAO.remove(1, 10)).thenReturn(true);

        assertTrue(wishlistService.removeFromWishlist(1, 10));
        verify(wishlistDAO, times(1)).remove(1, 10);

        wishlistService.clearWishlist(1);
        verify(wishlistDAO, times(1)).clear(1);
    }
}
