package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.CartItem;
import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CartDAOTest {

    private CartDAO cartDAO;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:cartdaotest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        cartDAO = new CartDAO();
    }

    @Test
    void testUpsertFindAndRemoveCartItem() throws SQLException {
        // Buyer id = 3, Product id = 1
        cartDAO.upsert(3, 1, 2);

        Optional<CartItem> item = cartDAO.findByUserAndProduct(3, 1);
        assertTrue(item.isPresent());
        assertEquals(2, item.get().getQuantity());

        // Update quantity via upsert
        cartDAO.upsert(3, 1, 5);
        item = cartDAO.findByUserAndProduct(3, 1);
        assertTrue(item.isPresent());
        assertEquals(5, item.get().getQuantity());

        List<CartItem> cart = cartDAO.findByUserId(3);
        assertFalse(cart.isEmpty());
        assertEquals("Wireless Ergonomic Mouse", cart.get(0).getProductName());

        // Remove item
        boolean removed = cartDAO.remove(3, 1);
        assertTrue(removed);
        assertTrue(cartDAO.findByUserAndProduct(3, 1).isEmpty());

        // Clear cart
        cartDAO.upsert(3, 1, 3);
        cartDAO.clear(3);
        assertTrue(cartDAO.findByUserId(3).isEmpty());
    }
}
