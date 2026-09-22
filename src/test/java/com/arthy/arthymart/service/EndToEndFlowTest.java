package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.CartDAO;
import com.arthy.arthymart.dao.OrderDAO;
import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.dao.ReviewDAO;
import com.arthy.arthymart.dao.UserDAO;
import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.model.Product;
import com.arthy.arthymart.model.Review;
import com.arthy.arthymart.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * End-to-end flow with mocked DAOs:
 * Register -> Authenticate -> Browse/Search -> Add to cart -> Checkout/Order -> Review.
 * Verifies server-side pricing (client totals ignored) and purchase-gated reviews.
 */
class EndToEndFlowTest {

    @Test
    void testFullBuyerFlow() throws Exception {
        // --- Register / Authenticate (UserService + UserDAO mock) ---
        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.findByEmail("buyer2@example.com")).thenReturn(Optional.empty());
        User saved = new User();
        saved.setId(42);
        saved.setEmail("buyer2@example.com");
        saved.setRole(User.Role.BUYER);
        when(userDAO.create(any(User.class))).thenReturn(saved);
        UserService userService = new UserService(userDAO);
        User registered = userService.registerUser("Buyer Two", "buyer2@example.com", "password123", User.Role.BUYER);
        assertEquals(42, registered.getId());

        // --- Browse / Search (ProductService + ProductDAO mock) ---
        ProductDAO productDAO = mock(ProductDAO.class);
        Product product = new Product();
        product.setId(101);
        product.setSellerId(2);
        product.setName("Wireless Mouse");
        product.setPrice(new BigDecimal("29.99"));
        product.setStockQty(50);
        product.setCategory("Electronics");
        when(productDAO.findById(101)).thenReturn(Optional.of(product));
        when(productDAO.findAll()).thenReturn(List.of(product));
        when(productDAO.search(any(), any())).thenReturn(List.of(product));
        ProductService productService = new ProductService(productDAO);
        assertEquals(1, productService.getAllProducts().size());
        assertEquals(1, productService.searchProducts("mouse", null).size());

        // --- Cart (CartService) ---
        CartDAO cartDAO = mock(CartDAO.class);
        when(cartDAO.findByUserAndProduct(42, 101)).thenReturn(Optional.empty());
        CartService cartService = new CartService(cartDAO, productDAO);
        cartService.addToCart(42, 101, 2);
        verify(cartDAO, times(1)).upsert(42, 101, 2);

        // --- Checkout / Order: client sends items WITHOUT prices; server re-prices ---
        OrderDAO orderDAO = mock(OrderDAO.class);
        when(orderDAO.createOrderWithItems(any(Order.class), anyList())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(900);
            return o;
        });
        OrderService orderService = new OrderService(orderDAO, productDAO, cartDAO);
        OrderItem item = new OrderItem();
        item.setProductId(101);
        item.setQuantity(2);
        item.setPricePerUnit(new BigDecimal("0.01")); // malicious client price; must be ignored
        Order order = orderService.placeOrder(42, List.of(item));
        assertEquals(900, order.getId());
        // Server-side total = 29.99 * 2 = 59.98
        assertEquals(new BigDecimal("59.98"), order.getTotalAmount());
        assertEquals(new BigDecimal("29.99"), item.getPricePerUnit());

        // --- Review gated on purchase ---
        ReviewDAO reviewDAO = mock(ReviewDAO.class);
        OrderDAO orderDAO2 = mock(OrderDAO.class);
        when(orderDAO2.hasPurchasedProduct(42, 101)).thenReturn(true);
        when(reviewDAO.existsByUserAndProduct(42, 101)).thenReturn(false);
        when(reviewDAO.create(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(11);
            return r;
        });
        ReviewService reviewService = new ReviewService(reviewDAO, orderDAO2);
        Review review = reviewService.addReview(42, 101, 5, "Excellent!");
        assertEquals(11, review.getId());
        verify(reviewDAO, times(1)).create(any(Review.class));
    }

    @Test
    void testCheckoutRejectsEmptyCart_AndOrderRejectsBadQuantity() throws Exception {
        ProductDAO productDAO = mock(ProductDAO.class);
        OrderDAO orderDAO = mock(OrderDAO.class);
        CartDAO cartDAO = mock(CartDAO.class);
        when(cartDAO.findByUserId(anyInt())).thenReturn(List.of());
        OrderService orderService = new OrderService(orderDAO, productDAO, cartDAO);
        assertThrows(IllegalArgumentException.class, () -> orderService.checkoutCart(1));

        Product p = new Product();
        p.setId(1);
        p.setPrice(new BigDecimal("10.00"));
        p.setStockQty(5);
        when(productDAO.findById(1)).thenReturn(Optional.of(p));
        OrderItem bad = new OrderItem();
        bad.setProductId(1);
        bad.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(1, List.of(bad)));
    }
}
