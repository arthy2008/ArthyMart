package com.arthy.arthymart.servlet;

import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.OrderService;
import com.arthy.arthymart.util.SecurityUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/orders/*")
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please login.");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user.getRole() != User.Role.BUYER && user.getRole() != User.Role.ADMIN) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only buyers can place orders.");
            return;
        }

        String pathInfo = req.getPathInfo();
        try {
            // POST /api/orders/checkout -> checkout entire cart (mock payment approved)
            if ("/checkout".equals(pathInfo)) {
                Order order = orderService.checkoutCart(user.getId());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(order));
                return;
            }
            String body = readBody(req);
            JsonObject jsonRequest = gson.fromJson(body, JsonObject.class);
            if (jsonRequest == null || !jsonRequest.has("items")) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Request must contain 'items' array.");
                return;
            }
            JsonArray itemsArray = jsonRequest.getAsJsonArray("items");

            List<OrderItem> items = new ArrayList<>();
            for (JsonElement itemElement : itemsArray) {
                JsonObject itemObj = itemElement.getAsJsonObject();
                OrderItem item = new OrderItem();
                item.setProductId(itemObj.get("productId").getAsInt());
                item.setQuantity(itemObj.get("quantity").getAsInt());
                // Never trust client-supplied prices; OrderService re-prices from DB.
                items.add(item);
            }

            Order order = orderService.placeOrder(user.getId(), items);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(order));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        User user = SecurityUtil.currentUser(req);
        if (user == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please login.");
            return;
        }

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                int orderId = Integer.parseInt(pathInfo.substring(1));
                Order order = orderService.getOrderById(orderId);
                if (order == null) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    return;
                }
                // Authorization: buyer owns it, seller has items in it, or admin.
                if (user.getRole() == User.Role.BUYER && order.getBuyerId() != user.getId()) {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "You cannot view another user's order.");
                    return;
                }
                if (user.getRole() == User.Role.SELLER) {
                    boolean sellerInvolved = orderService.getOrdersForSeller(user.getId())
                            .stream().anyMatch(o -> o.getId() == orderId);
                    if (!sellerInvolved) {
                        sendError(resp, HttpServletResponse.SC_FORBIDDEN, "No access to this order.");
                        return;
                    }
                }
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(order));
                return;
            }
            List<Order> orders;
            if (user.getRole() == User.Role.SELLER && "seller".equals(req.getParameter("view"))) {
                orders = orderService.getOrdersForSeller(user.getId());
            } else if (user.getRole() == User.Role.ADMIN && "all".equals(req.getParameter("view"))) {
                orders = orderService.getAllOrders();
            } else {
                orders = orderService.getOrdersByBuyer(user.getId());
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(orders));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order id.");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Order id is required.");
                return;
            }
            int orderId = Integer.parseInt(pathInfo.substring(1));
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                return;
            }
            JsonObject json = gson.fromJson(readBody(req), JsonObject.class);
            Order.Status newStatus = Order.Status.valueOf(json.get("status").getAsString().toUpperCase());

            if (user.getRole() == User.Role.BUYER) {
                // Buyers may only cancel their own pending orders.
                if (order.getBuyerId() != user.getId() || newStatus != Order.Status.CANCELLED
                        || order.getStatus() != Order.Status.PENDING) {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Buyers can only cancel their own pending orders.");
                    return;
                }
            } else if (user.getRole() == User.Role.SELLER) {
                boolean involved = orderService.getOrdersForSeller(user.getId())
                        .stream().anyMatch(o -> o.getId() == orderId);
                if (!involved) {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "No access to this order.");
                    return;
                }
                if (newStatus == Order.Status.CANCELLED && order.getStatus() != Order.Status.PENDING) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Only pending orders can be cancelled.");
                    return;
                }
            } else if (user.getRole() != User.Role.ADMIN) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Not allowed.");
                return;
            }
            orderService.updateOrderStatus(orderId, newStatus);
            JsonObject out = new JsonObject();
            out.addProperty("status", "success");
            out.addProperty("message", "Order status updated.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
