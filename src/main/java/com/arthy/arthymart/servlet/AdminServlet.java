package com.arthy.arthymart.servlet;

import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.dao.UserDAO;
import com.arthy.arthymart.dto.UserResponseDTO;
import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.OrderService;
import com.arthy.arthymart.util.SecurityUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Admin-only endpoints: view users/orders, moderate listings. */
@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderService orderService = new OrderService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (!SecurityUtil.hasRole(req, User.Role.ADMIN)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }
        try {
            String pathInfo = req.getPathInfo();
            if ("/users".equals(pathInfo)) {
                String roleParam = req.getParameter("role");
                List<UserResponseDTO> users = userDAO.findAll().stream()
                        .filter(u -> roleParam == null || roleParam.trim().isEmpty()
                                || u.getRole().name().equalsIgnoreCase(roleParam.trim()))
                        .map(UserResponseDTO::fromUser)
                        .collect(Collectors.toList());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(users));
            } else if ("/orders".equals(pathInfo)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(orderService.getAllOrders()));
            } else if ("/products".equals(pathInfo)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(productDAO.findAll()));
            } else {
                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("endpoints", new String[]{"/api/admin/users", "/api/admin/orders", "/api/admin/products"});
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(out));
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (!SecurityUtil.hasRole(req, User.Role.ADMIN)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/products/")) {
                int productId = Integer.parseInt(pathInfo.substring("/products/".length()));
                boolean deleted = productDAO.deleteByAdmin(productId);
                if (!deleted) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                    return;
                }
                JsonObject out = new JsonObject();
                out.addProperty("status", "success");
                out.addProperty("message", "Listing removed by admin.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(out));
            } else if (pathInfo != null && pathInfo.startsWith("/users/")) {
                int userId = Integer.parseInt(pathInfo.substring("/users/".length()));
                User current = SecurityUtil.currentUser(req);
                if (current != null && current.getId() == userId) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Admin cannot delete themselves.");
                    return;
                }
                boolean deleted = userDAO.delete(userId);
                if (!deleted) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "User not found.");
                    return;
                }
                JsonObject out = new JsonObject();
                out.addProperty("status", "success");
                out.addProperty("message", "User removed.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(out));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid id.");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Admin order status update: PUT /api/admin/orders/{id} with {"status":"SHIPPED"}
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (!SecurityUtil.hasRole(req, User.Role.ADMIN)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/orders/")) {
                int orderId = Integer.parseInt(pathInfo.substring("/orders/".length()));
                JsonObject json = gson.fromJson(req.getReader(), JsonObject.class);
                Order.Status status = Order.Status.valueOf(json.get("status").getAsString().toUpperCase());
                orderService.updateOrderStatus(orderId, status);
                JsonObject out = new JsonObject();
                out.addProperty("status", "success");
                out.addProperty("message", "Order status updated.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(out));
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        JsonObject err = new JsonObject();
        err.addProperty("status", "error");
        err.addProperty("message", message);
        resp.getWriter().write(gson.toJson(err));
    }
}
