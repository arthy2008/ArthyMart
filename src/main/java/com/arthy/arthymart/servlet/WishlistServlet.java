package com.arthy.arthymart.servlet;

import com.arthy.arthymart.dto.WishlistDTO;
import com.arthy.arthymart.model.User;
import com.arthy.arthymart.model.WishlistItem;
import com.arthy.arthymart.service.WishlistService;
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
import java.util.stream.Collectors;

@WebServlet("/api/wishlist/*")
public class WishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        User user = SecurityUtil.currentUser(req);
        if (user == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Login required.");
            return;
        }

        try {
            String checkProductParam = req.getParameter("productId");
            if (checkProductParam != null) {
                int prodId = Integer.parseInt(checkProductParam);
                boolean exists = wishlistService.isWishlisted(user.getId(), prodId);
                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("isWishlisted", exists);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(out));
                return;
            }

            List<WishlistItem> items = wishlistService.getWishlist(user.getId());
            List<WishlistDTO> dtos = items.stream()
                    .map(WishlistDTO::fromModel)
                    .collect(Collectors.toList());

            Map<String, Object> out = new HashMap<>();
            out.put("status", "success");
            out.put("items", dtos);
            out.put("count", dtos.size());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
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
            if (json == null || !json.has("productId")) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "productId is required.");
                return;
            }
            int productId = json.get("productId").getAsInt();
            boolean added = wishlistService.addToWishlist(user.getId(), productId);

            Map<String, Object> out = new HashMap<>();
            out.put("status", "success");
            out.put("message", added ? "Product added to wishlist." : "Product is already in wishlist.");
            out.put("added", added);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        User user = SecurityUtil.currentUser(req);
        if (user == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Login required.");
            return;
        }

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                int productId = Integer.parseInt(pathInfo.substring(1));
                wishlistService.removeFromWishlist(user.getId(), productId);
            } else {
                String body = readBody(req);
                if (body != null && !body.trim().isEmpty()) {
                    JsonObject json = gson.fromJson(body, JsonObject.class);
                    if (json.has("productId")) {
                        wishlistService.removeFromWishlist(user.getId(), json.get("productId").getAsInt());
                    } else {
                        wishlistService.clearWishlist(user.getId());
                    }
                } else {
                    wishlistService.clearWishlist(user.getId());
                }
            }

            Map<String, Object> out = new HashMap<>();
            out.put("status", "success");
            out.put("message", "Wishlist updated.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
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
        err.addProperty("message", message == null ? "Request failed." : message);
        resp.getWriter().write(gson.toJson(err));
    }
}
