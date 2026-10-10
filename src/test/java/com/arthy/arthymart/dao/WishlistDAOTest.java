package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.WishlistItem;
import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WishlistDAOTest {

    private WishlistDAO wishlistDAO;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:wishlistdaotest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        wishlistDAO = new WishlistDAO();
    }

    @Test
    void testAddFindAndRemoveWishlistItem() throws SQLException {
        // Buyer id = 3, Product id = 1
        boolean added = wishlistDAO.add(3, 1);
        assertTrue(added);
        assertTrue(wishlistDAO.isWishlisted(3, 1));

        // Duplicate add returns false
        boolean duplicate = wishlistDAO.add(3, 1);
        assertFalse(duplicate);

        // Find items
        List<WishlistItem> items = wishlistDAO.findByUserId(3);
        assertEquals(1, items.size());
        assertEquals("Wireless Ergonomic Mouse", items.get(0).getProductName());

        // Remove item
        boolean removed = wishlistDAO.remove(3, 1);
        assertTrue(removed);
        assertFalse(wishlistDAO.isWishlisted(3, 1));
        assertTrue(wishlistDAO.findByUserId(3).isEmpty());
    }

    @Test
    void testClearWishlist() throws SQLException {
        wishlistDAO.add(3, 1);
        wishlistDAO.add(3, 2);
        assertEquals(2, wishlistDAO.findByUserId(3).size());

        wishlistDAO.clear(3);
        assertTrue(wishlistDAO.findByUserId(3).isEmpty());
    }
}
