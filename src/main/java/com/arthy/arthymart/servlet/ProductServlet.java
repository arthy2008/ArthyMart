package com.arthy.arthymart.servlet;

import com.arthy.arthymart.model.Product;
import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.ProductService;
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
import java.util.List;
import java.util.Optional;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<Product> product = productService.getProductById(id);
                if (product.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(product.get()));
                } else {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                }
                return;
            }
            String query = req.getParameter("q");
            String category = req.getParameter("category");
            String mine = req.getParameter("mine");
            User user = SecurityUtil.currentUser(req);
            List<Product> products;
            if ("true".equalsIgnoreCase(mine)) {
                if (user == null || user.getRole() != User.Role.SELLER) {
                    sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Seller login required.");
                    return;
                }
                products = productService.getProductsBySeller(user.getId());
            } else if ((query != null && !query.trim().isEmpty()) || (category != null && !category.trim().isEmpty())) {
                products = productService.searchProducts(query, category);
            } else {
                products = productService.getAllProducts();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(products));
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
        if (user.getRole() != User.Role.SELLER && user.getRole() != User.Role.ADMIN) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only sellers can create products.");
            return;
        }
        try {
            JsonObject json = gson.fromJson(readBody(req), JsonObject.class);
            Product product = fromJson(json, user.getId());
            Product created = productService.addProduct(product);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(created));
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
        if (user.getRole() != User.Role.SELLER && user.getRole() != User.Role.ADMIN) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only sellers can edit products.");
            return;
        }
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product id is required.");
                return;
            }
            int id = Integer.parseInt(pathInfo.substring(1));
            Optional<Product> existing = productService.getProductById(id);
            if (existing.isEmpty()) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                return;
            }
            if (user.getRole() == User.Role.SELLER && existing.get().getSellerId() != user.getId()) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "You can only edit your own products.");
                return;
            }
            JsonObject json = gson.fromJson(readBody(req), JsonObject.class);
            Product product = fromJson(json, existing.get().getSellerId());
            product.setId(id);
            productService.updateProduct(product);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(product));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product id.");
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
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product id is required.");
                return;
            }
            int id = Integer.parseInt(pathInfo.substring(1));
            Optional<Product> existing = productService.getProductById(id);
            if (existing.isEmpty()) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                return;
            }
            if (user.getRole() == User.Role.ADMIN) {
                // Admin moderation: allowed to remove any listing.
                productService.deleteProduct(id, existing.get().getSellerId());
            } else if (user.getRole() == User.Role.SELLER) {
                if (existing.get().getSellerId() != user.getId()) {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "You can only delete your own products.");
                    return;
                }
                productService.deleteProduct(id, user.getId());
            } else {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only sellers or admins can delete products.");
                return;
            }
            JsonObject out = new JsonObject();
            out.addProperty("status", "success");
            out.addProperty("message", "Product deleted.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product id.");
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Product fromJson(JsonObject json, int sellerId) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(json.has("name") && !json.get("name").isJsonNull() ? json.get("name").getAsString() : null);
        product.setDescription(json.has("description") && !json.get("description").isJsonNull()
                ? json.get("description").getAsString() : null);
        if (json.has("price") && !json.get("price").isJsonNull()) {
            try {
                product.setPrice(new BigDecimal(json.get("price").getAsString()));
            } catch (NumberFormatException e) {
                product.setPrice(BigDecimal.valueOf(json.get("price").getAsDouble()));
            }
        }
        product.setStockQty(json.has("stockQty") && !json.get("stockQty").isJsonNull()
                ? json.get("stockQty").getAsInt() : 0);
        if (json.has("stock_qty") && !json.get("stock_qty").isJsonNull()) {
            product.setStockQty(json.get("stock_qty").getAsInt());
        }
        product.setCategory(json.has("category") && !json.get("category").isJsonNull()
                ? json.get("category").getAsString() : null);
        product.setImageUrl(json.has("imageUrl") && !json.get("imageUrl").isJsonNull()
                ? json.get("imageUrl").getAsString() : null);
        if (json.has("image_url") && !json.get("image_url").isJsonNull()) {
            product.setImageUrl(json.get("image_url").getAsString());
        }
        return product;
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
