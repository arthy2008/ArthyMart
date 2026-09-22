package com.arthy.arthymart.servlet;

import com.arthy.arthymart.model.Review;
import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.ReviewService;
import com.arthy.arthymart.util.SecurityUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reviews/*")
public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String productIdParam = req.getParameter("productId");
            String pathInfo = req.getPathInfo();
            int productId = -1;
            if (productIdParam != null) {
                productId = Integer.parseInt(productIdParam);
            } else if (pathInfo != null && pathInfo.length() > 1) {
                productId = Integer.parseInt(pathInfo.substring(1));
            }
            if (productId <= 0) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "productId is required.");
                return;
            }
            List<Review> reviews = reviewService.getReviewsForProduct(productId);
            double avg = reviewService.getAverageRating(productId);
            Map<String, Object> out = new HashMap<>();
            out.put("status", "success");
            out.put("averageRating", avg);
            out.put("reviews", reviews);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product id.");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        User user = SecurityUtil.currentUser(req);
        if (user == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Login required.");
            return;
        }
        try {
            JsonObject json = gson.fromJson(readBody(req), JsonObject.class);
            int productId = json.get("productId").getAsInt();
            int rating = json.get("rating").getAsInt();
            String comment = json.has("comment") && !json.get("comment").isJsonNull()
                    ? json.get("comment").getAsString() : null;
            Review review = reviewService.addReview(user.getId(), productId, rating, comment);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(review));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        JsonObject err = new JsonObject();
        err.addProperty("status", "error");
        err.addProperty("message", message);
        resp.getWriter().write(gson.toJson(err));
    }
}
