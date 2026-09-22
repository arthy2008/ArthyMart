package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.OrderDAO;
import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderDAO = mock(OrderDAO.class);
        productDAO = mock(ProductDAO.class);
        orderService = new OrderService(orderDAO, productDAO);
    }

    @Test
    void testPlaceOrder_Success() throws SQLException {
        Product product = new Product();
        product.setId(101);
        product.setName("Java Capstone Guide");
        product.setPrice(new BigDecimal("299.00"));
        product.setStockQty(10);

        when(productDAO.findById(101)).thenReturn(Optional.of(product));

        Order expectedOrder = new Order();
        expectedOrder.setId(1);
        expectedOrder.setBuyerId(5);
        expectedOrder.setTotalAmount(new BigDecimal("598.00"));

        when(orderDAO.createOrderWithItems(any(Order.class), anyList())).thenReturn(expectedOrder);

        OrderItem item = new OrderItem();
        item.setProductId(101);
        item.setQuantity(2);

        Order result = orderService.placeOrder(5, List.of(item));

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(new BigDecimal("598.00"), result.getTotalAmount());
    }

    @Test
    void testPlaceOrder_InsufficientStock() throws SQLException {
        Product product = new Product();
        product.setId(101);
        product.setName("Java Capstone Guide");
        product.setPrice(new BigDecimal("299.00"));
        product.setStockQty(1);

        when(productDAO.findById(101)).thenReturn(Optional.of(product));

        OrderItem item = new OrderItem();
        item.setProductId(101);
        item.setQuantity(5);

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(5, List.of(item));
        });

        verify(orderDAO, never()).createOrderWithItems(any(Order.class), anyList());
    }

    @Test
    void testPlaceOrder_EmptyCart() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(5, Collections.emptyList());
        });
    }
}
