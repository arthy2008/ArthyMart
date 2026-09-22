package com.arthy.arthymart.servlet;

import com.arthy.arthymart.model.CartItem;
import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.CartService;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();
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
            List<CartItem> items = cartService.getCart(user.getId());
            BigDecimal total = cartService.getCartTotal(user.getId());
            Map<String, Object> out = new HashMap<>();
            out.put("status", "success");
            out.put("items", items);
            out.put("total", total);
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
            int productId = json.get("productId").getAsInt();
            int quantity = json.get("quantity").getAsInt();
            cartService.addToCart(user.getId(), productId, quantity);
            sendSuccess(resp, "Added to cart.");
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            int quantity = json.get("quantity").getAsInt();
            cartService.updateQuantity(user.getId(), productId, quantity);
            sendSuccess(resp, "Cart updated.");
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
                cartService.removeFromCart(user.getId(), productId);
            } else {
                String body = readBody(req);
                if (body != null && !body.trim().isEmpty()) {
                    JsonObject json = gson.fromJson(body, JsonObject.class);
                    if (json.has("productId")) {
                        cartService.removeFromCart(user.getId(), json.get("productId").getAsInt());
                    } else {
                        cartService.clearCart(user.getId());
                    }
                } else {
                    cartService.clearCart(user.getId());
                }
            }
            sendSuccess(resp, "Cart updated.");
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

    private void sendSuccess(HttpServletResponse resp, String message) throws IOException {
        JsonObject out = new JsonObject();
        out.addProperty("status", "success");
        out.addProperty("message", message);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(out));
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        JsonObject err = new JsonObject();
        err.addProperty("status", "error");
        err.addProperty("message", message);
        resp.getWriter().write(gson.toJson(err));
    }
}
