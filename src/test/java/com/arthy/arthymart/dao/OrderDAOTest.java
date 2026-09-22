package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.model.Product;
import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderDAOTest {

    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private CartDAO cartDAO;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:orderdaotest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        cartDAO = new CartDAO();
    }

    @Test
    void testCreateOrderTransactionalStockReductionAndCartClear() throws SQLException {
        // Buyer id = 3, Product id = 1
        Optional<Product> pBefore = productDAO.findById(1);
        assertTrue(pBefore.isPresent());
        int initialStock = pBefore.get().getStockQty();

        cartDAO.upsert(3, 1, 2);
        assertFalse(cartDAO.findByUserId(3).isEmpty());

        Order order = new Order();
        order.setBuyerId(3);
        order.setTotalAmount(new BigDecimal("59.98"));
        order.setStatus(Order.Status.PENDING);

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(1);
        item.setQuantity(2);
        item.setPricePerUnit(new BigDecimal("29.99"));
        items.add(item);

        Order created = orderDAO.createOrderWithItems(order, items);
        assertTrue(created.getId() > 0);

        // Check stock reduced
        Optional<Product> pAfter = productDAO.findById(1);
        assertTrue(pAfter.isPresent());
        assertEquals(initialStock - 2, pAfter.get().getStockQty());

        // Check cart cleared
        assertTrue(cartDAO.findByUserId(3).isEmpty());

        // Check order retrieval
        Order retrieved = orderDAO.findById(created.getId());
        assertNotNull(retrieved);
        assertEquals(Order.Status.PENDING, retrieved.getStatus());
        assertEquals(1, retrieved.getItems().size());

        // Update status
        boolean updated = orderDAO.updateStatus(created.getId(), Order.Status.CONFIRMED);
        assertTrue(updated);
        assertEquals(Order.Status.CONFIRMED, orderDAO.findById(created.getId()).getStatus());
    }

    @Test
    void testInsufficientStockRollsBackTransaction() throws SQLException {
        Optional<Product> pBefore = productDAO.findById(1);
        assertTrue(pBefore.isPresent());
        int initialStock = pBefore.get().getStockQty();

        Order order = new Order();
        order.setBuyerId(3);
        order.setTotalAmount(new BigDecimal("99999.00"));
        order.setStatus(Order.Status.PENDING);

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(1);
        item.setQuantity(initialStock + 10); // Exceeds available stock
        item.setPricePerUnit(new BigDecimal("29.99"));
        items.add(item);

        assertThrows(SQLException.class, () -> orderDAO.createOrderWithItems(order, items));

        // Ensure stock was not changed
        Optional<Product> pAfter = productDAO.findById(1);
        assertTrue(pAfter.isPresent());
        assertEquals(initialStock, pAfter.get().getStockQty());
    }

    @Test
    void testHasPurchasedProduct() throws SQLException {
        // From seed data, buyer 3 has a DELIVERED order for product 1
        boolean purchased = orderDAO.hasPurchasedProduct(3, 1);
        assertTrue(purchased);

        // Has not purchased product 2
        boolean notPurchased = orderDAO.hasPurchasedProduct(3, 2);
        assertFalse(notPurchased);
    }
}
