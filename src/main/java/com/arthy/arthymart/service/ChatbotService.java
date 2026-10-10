package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Product;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatbotService {

    private final ProductDAO productDAO;
    private final Gson gson = new Gson();

    public ChatbotService() {
        this.productDAO = new ProductDAO();
    }

    public ChatbotService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Map<String, Object> handleMessage(String userMessage) {
        Map<String, Object> response = new HashMap<>();
        if (userMessage == null || userMessage.trim().isEmpty()) {
            response.put("reply", "Hello! I am Arthy, your AI shopping assistant. How can I help you discover products today?");
            response.put("products", Collections.emptyList());
            response.put("suggestions", defaultSuggestions());
            return response;
        }

        String msg = userMessage.trim().toLowerCase(Locale.ROOT);

        try {
            List<Product> allProducts = productDAO.findAll();

            // 1. Check for price queries (e.g. "under 50", "below 100", "less than 30", "under ₹500")
            Pattern pricePattern = Pattern.compile("(under|below|less than|max|budget)\\s*[\\$₹rs\\.]*\\s*(\\d+(\\.\\d+)?)");
            Matcher priceMatcher = pricePattern.matcher(msg);
            if (priceMatcher.find()) {
                BigDecimal maxPrice = new BigDecimal(priceMatcher.group(2));
                List<Product> matches = allProducts.stream()
                        .filter(p -> p.getPrice() != null && p.getPrice().compareTo(maxPrice) <= 0)
                        .limit(4)
                        .collect(Collectors.toList());

                if (!matches.isEmpty()) {
                    response.put("reply", "Here are great options under ₹" + maxPrice + " available right now:");
                    response.put("products", matches);
                    response.put("suggestions", List.of("Show electronics", "Show all deals", "How does checkout work?"));
                    return response;
                } else {
                    response.put("reply", "I couldn't find any products under ₹" + maxPrice + ". Our budget items start around ₹19.99.");
                    response.put("products", Collections.emptyList());
                    response.put("suggestions", defaultSuggestions());
                    return response;
                }
            }

            // 2. Check for Category queries
            List<String> categories = allProducts.stream()
                    .map(Product::getCategory)
                    .filter(c -> c != null && !c.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

            for (String cat : categories) {
                if (msg.contains(cat.toLowerCase(Locale.ROOT))) {
                    List<Product> catProducts = allProducts.stream()
                            .filter(p -> cat.equalsIgnoreCase(p.getCategory()))
                            .limit(4)
                            .collect(Collectors.toList());

                    response.put("reply", "Here are popular items in the " + cat + " category:");
                    response.put("products", catProducts);
                    response.put("suggestions", List.of("Items under ₹50", "Best rated products", "View all products"));
                    return response;
                }
            }

            // 3. Check for specific product keyword search
            List<Product> keywordMatches = allProducts.stream()
                    .filter(p -> containsKeyword(p.getName(), msg) || containsKeyword(p.getDescription(), msg))
                    .limit(4)
                    .collect(Collectors.toList());

            if (!keywordMatches.isEmpty()) {
                response.put("reply", "I found " + keywordMatches.size() + " product(s) matching your request:");
                response.put("products", keywordMatches);
                response.put("suggestions", List.of("Check stock", "Add to cart", "Show customer reviews"));
                return response;
            }

            // 4. E-commerce FAQs & Store Information
            if (msg.contains("shipping") || msg.contains("delivery")) {
                response.put("reply", "🚚 We offer standard fast delivery on all ArthyMart orders! You can track the status of your order directly from the Orders page.");
                response.put("products", Collections.emptyList());
                response.put("suggestions", List.of("View my orders", "Browse electronics", "Help with payment"));
                return response;
            }

            if (msg.contains("return") || msg.contains("refund")) {
                response.put("reply", "🔄 ArthyMart offers a 7-day easy return policy for delivered items. If your item hasn't shipped yet, you can also cancel pending orders immediately.");
                response.put("products", Collections.emptyList());
                response.put("suggestions", List.of("View my orders", "Contact support", "Browse products"));
                return response;
            }

            if (msg.contains("payment") || msg.contains("checkout") || msg.contains("pay")) {
                response.put("reply", "💳 Checkout is safe and seamless! We verify inventory in real-time and provide instant mock payment approval for this demo platform.");
                response.put("products", Collections.emptyList());
                response.put("suggestions", List.of("Go to cart", "Browse products", "View my orders"));
                return response;
            }

            if (msg.contains("seller") || msg.contains("sell")) {
                response.put("reply", "🏪 Interested in selling? Register an account with the 'SELLER' role to list products, manage inventory, and fulfill orders through your Seller Dashboard.");
                response.put("products", Collections.emptyList());
                response.put("suggestions", List.of("Register account", "Seller dashboard", "Browse products"));
                return response;
            }

            if (msg.contains("hi") || msg.contains("hello") || msg.contains("hey")) {
                response.put("reply", "👋 Hello! Welcome to ArthyMart! You can ask me to find products, recommend items under a budget, or explain our shipping and checkout process.");
                response.put("products", allProducts.stream().limit(3).collect(Collectors.toList()));
                response.put("suggestions", defaultSuggestions());
                return response;
            }

            // Default fallback with featured suggestions
            response.put("reply", "I couldn't find an exact match for \"" + userMessage.trim() + "\". Here are some of our top-rated products you might like:");
            response.put("products", allProducts.stream().limit(3).collect(Collectors.toList()));
            response.put("suggestions", defaultSuggestions());

        } catch (SQLException e) {
            response.put("reply", "I am having trouble accessing the catalog right now. Please try browsing from the Products page.");
            response.put("products", Collections.emptyList());
            response.put("suggestions", defaultSuggestions());
        }

        return response;
    }

    private boolean containsKeyword(String source, String query) {
        if (source == null || query == null) {
            return false;
        }
        String s = source.toLowerCase(Locale.ROOT);
        String cleanQuery = query.replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] tokens = cleanQuery.split("\\s+");
        for (String t : tokens) {
            if (t.length() > 2 && s.contains(t)) {
                return true;
            }
        }
        return false;
    }

    private List<String> defaultSuggestions() {
        return List.of("Show Electronics", "Products under $50", "Shipping policy", "Best sellers");
    }
}
