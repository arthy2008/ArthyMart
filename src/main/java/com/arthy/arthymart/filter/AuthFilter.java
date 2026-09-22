package com.arthy.arthymart.filter;

import com.arthy.arthymart.model.User;
import com.arthy.arthymart.util.SecurityUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());
        if (path.isEmpty()) {
            path = "/";
        }

        // Always permit static resources and error page
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.equals("/favicon.ico") || path.equals("/error.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Always permit health endpoints and H2 console
        if (path.equals("/api/v1/health") || path.equals("/api/health")
                || path.startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        // Always permit public auth actions and public pages
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.equals("/api/auth/logout")
                || path.equals("/") || path.equals("/index.jsp") || path.equals("/login.jsp")
                || path.equals("/register.jsp") || path.equals("/products.jsp") || path.equals("/product-detail.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Public read-only endpoints: GET /api/products, GET /api/reviews
        String method = req.getMethod().toUpperCase();
        if (path.startsWith("/api/products") && "GET".equals(method)) {
            // GET /api/products?mine=true requires SELLER/ADMIN
            if ("true".equalsIgnoreCase(req.getParameter("mine"))) {
                User user = SecurityUtil.currentUser(req);
                if (user == null) {
                    sendApiError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Seller login required.");
                    return;
                }
                if (user.getRole() != User.Role.SELLER && user.getRole() != User.Role.ADMIN) {
                    sendApiError(resp, HttpServletResponse.SC_FORBIDDEN, "Seller access required.");
                    return;
                }
            }
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/reviews") && "GET".equals(method)) {
            chain.doFilter(request, response);
            return;
        }

        User user = SecurityUtil.currentUser(req);

        // 1. Admin area: /admin/* and /api/admin/*
        if (path.startsWith("/admin/") || path.equals("/admin") || path.startsWith("/api/admin")) {
            if (user == null) {
                if (path.startsWith("/api/")) {
                    sendApiError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please login.");
                } else {
                    resp.sendRedirect(contextPath + "/login.jsp");
                }
                return;
            }
            if (user.getRole() != User.Role.ADMIN) {
                if (path.startsWith("/api/")) {
                    sendApiError(resp, HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
                } else {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
                }
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 2. Seller area: /seller/* and product mutations (POST, PUT, DELETE)
        if (path.startsWith("/seller/") || path.equals("/seller")
                || (path.startsWith("/api/products") && ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)))) {
            if (user == null) {
                if (path.startsWith("/api/")) {
                    sendApiError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please login.");
                } else {
                    resp.sendRedirect(contextPath + "/login.jsp");
                }
                return;
            }
            if (user.getRole() != User.Role.SELLER && user.getRole() != User.Role.ADMIN) {
                if (path.startsWith("/api/")) {
                    sendApiError(resp, HttpServletResponse.SC_FORBIDDEN, "Only sellers can perform this action.");
                } else {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Seller access required.");
                }
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 3. Buyer / Authenticated User area: /cart.jsp, /checkout.jsp, /orders.jsp, /api/cart/*, /api/orders/*, POST /api/reviews, /api/auth/me
        if (path.equals("/cart.jsp") || path.equals("/checkout.jsp") || path.equals("/orders.jsp")
                || path.startsWith("/api/cart") || path.startsWith("/api/orders")
                || path.startsWith("/api/auth/me")
                || (path.startsWith("/api/reviews") && "POST".equals(method))) {
            if (user == null) {
                if (path.startsWith("/api/")) {
                    sendApiError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please login.");
                } else {
                    resp.sendRedirect(contextPath + "/login.jsp");
                }
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // Default pass-through
        chain.doFilter(request, response);
    }

    private void sendApiError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String json = "{\"success\":false,\"status\":\"error\",\"message\":\"" + escapeJson(message) + "\",\"error\":\"" + escapeJson(message) + "\"}";
        resp.getWriter().write(json);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public void destroy() {}
}
