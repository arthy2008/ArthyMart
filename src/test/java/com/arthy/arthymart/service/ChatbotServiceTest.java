package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatbotServiceTest {

    private ProductDAO productDAO;
    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() throws SQLException {
        productDAO = mock(ProductDAO.class);
        chatbotService = new ChatbotService(productDAO);

        Product p1 = new Product();
        p1.setId(1);
        p1.setName("Wireless Mouse");
        p1.setDescription("Silent 2.4GHz optical mouse");
        p1.setPrice(new BigDecimal("25.00"));
        p1.setCategory("Electronics");
        p1.setStockQty(50);

        Product p2 = new Product();
        p2.setId(2);
        p2.setName("Java Programming");
        p2.setDescription("Complete guide to modern Java");
        p2.setPrice(new BigDecimal("45.00"));
        p2.setCategory("Books");
        p2.setStockQty(20);

        when(productDAO.findAll()).thenReturn(List.of(p1, p2));
    }

    @Test
    void testEmptyMessageGreeting() {
        Map<String, Object> resp = chatbotService.handleMessage("");
        assertNotNull(resp.get("reply"));
        assertTrue(resp.get("reply").toString().contains("Arthy"));
    }

    @Test
    void testPriceBudgetQuery() {
        Map<String, Object> resp = chatbotService.handleMessage("find products under 30");
        assertTrue(resp.get("reply").toString().contains("under") && resp.get("reply").toString().contains("30"));
        List<?> products = (List<?>) resp.get("products");
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    void testCategoryQuery() {
        Map<String, Object> resp = chatbotService.handleMessage("show me books");
        assertTrue(resp.get("reply").toString().contains("Books"));
        List<?> products = (List<?>) resp.get("products");
        assertEquals(1, products.size());
    }

    @Test
    void testKeywordSearchQuery() {
        Map<String, Object> resp = chatbotService.handleMessage("do you have a mouse?");
        assertTrue(resp.get("reply").toString().contains("matching your request"));
        List<?> products = (List<?>) resp.get("products");
        assertEquals(1, products.size());
    }

    @Test
    void testFaqQueries() {
        Map<String, Object> resp = chatbotService.handleMessage("how does shipping work?");
        assertTrue(resp.get("reply").toString().contains("fast delivery"));

        Map<String, Object> returnResp = chatbotService.handleMessage("what is your return policy?");
        assertTrue(returnResp.get("reply").toString().contains("return"));
    }

    @Test
    void testFallbackForUnknownQuery() {
        Map<String, Object> resp = chatbotService.handleMessage("supercalifragilistic");
        assertTrue(resp.get("reply").toString().contains("couldn't find an exact match"));
    }
}
