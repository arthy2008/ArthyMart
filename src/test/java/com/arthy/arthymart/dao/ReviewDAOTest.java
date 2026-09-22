package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.Review;
import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOTest {

    private ReviewDAO reviewDAO;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:reviewdaotest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        reviewDAO = new ReviewDAO();
    }

    @Test
    void testCreateFindAndAverageRating() throws SQLException {
        // Product 2 currently has no reviews
        List<Review> initialReviews = reviewDAO.findByProductId(2);
        assertTrue(initialReviews.isEmpty());
        assertEquals(0.0, reviewDAO.averageRating(2));

        Review r = new Review();
        r.setProductId(2);
        r.setUserId(3);
        r.setRating(4);
        r.setComment("Great mechanical keyboard");

        Review created = reviewDAO.create(r);
        assertTrue(created.getId() > 0);

        List<Review> reviews = reviewDAO.findByProductId(2);
        assertEquals(1, reviews.size());
        assertEquals(4, reviews.get(0).getRating());
        assertEquals(4.0, reviewDAO.averageRating(2));

        assertTrue(reviewDAO.existsByUserAndProduct(3, 2));
    }

    @Test
    void testDuplicateReviewPerUserAndProductThrowsSQLException() throws SQLException {
        // Buyer 3 already reviewed product 1 in seed
        assertTrue(reviewDAO.existsByUserAndProduct(3, 1));

        Review duplicate = new Review();
        duplicate.setProductId(1);
        duplicate.setUserId(3);
        duplicate.setRating(5);
        duplicate.setComment("Duplicate review attempt");

        assertThrows(SQLException.class, () -> reviewDAO.create(duplicate));
    }
}
