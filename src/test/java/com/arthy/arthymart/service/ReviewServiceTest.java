package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.OrderDAO;
import com.arthy.arthymart.dao.ReviewDAO;
import com.arthy.arthymart.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewDAO reviewDAO;
    private OrderDAO orderDAO;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewDAO = mock(ReviewDAO.class);
        orderDAO = mock(OrderDAO.class);
        reviewService = new ReviewService(reviewDAO, orderDAO);
    }

    @Test
    void testAddReview_Success() throws SQLException {
        when(orderDAO.hasPurchasedProduct(1, 20)).thenReturn(true);
        when(reviewDAO.existsByUserAndProduct(1, 20)).thenReturn(false);
        when(reviewDAO.create(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(5);
            return r;
        });
        Review r = reviewService.addReview(1, 20, 5, "Great product!");
        assertEquals(5, r.getId());
    }

    @Test
    void testAddReview_WithoutPurchaseRejected() throws SQLException {
        when(orderDAO.hasPurchasedProduct(1, 20)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(1, 20, 5, "Nice"));
        verify(reviewDAO, never()).create(any(Review.class));
    }

    @Test
    void testAddReview_DuplicateRejected() throws SQLException {
        when(orderDAO.hasPurchasedProduct(1, 20)).thenReturn(true);
        when(reviewDAO.existsByUserAndProduct(1, 20)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(1, 20, 4, "Again"));
    }

    @Test
    void testAddReview_BadRatingRejected() {
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(1, 20, 0, "bad"));
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(1, 20, 6, "bad"));
    }
}
