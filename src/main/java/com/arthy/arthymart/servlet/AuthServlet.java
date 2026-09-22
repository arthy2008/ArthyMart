package com.arthy.arthymart.servlet;

import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.UserService;
import com.arthy.arthymart.util.SecurityUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        String body = readBody(req);
        JsonObject jsonRequest;
        try {
            jsonRequest = gson.fromJson(body, JsonObject.class);
            if (jsonRequest == null) {
                jsonRequest = new JsonObject();
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body.");
            return;
        }

        try {
            if ("/register".equals(pathInfo)) {
                handleRegister(req, resp, jsonRequest);
            } else if ("/login".equals(pathInfo)) {
                handleLogin(req, resp, jsonRequest);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(req, resp);
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if ("/logout".equals(pathInfo)) {
            handleLogout(req, resp);
            return;
        }
        if ("/me".equals(pathInfo)) {
            User user = SecurityUtil.currentUser(req);
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Not logged in.");
                return;
            }
            JsonObject out = new JsonObject();
            out.addProperty("status", "success");
            out.addProperty("userId", user.getId());
            out.addProperty("name", user.getName());
            out.addProperty("email", user.getEmail());
            out.addProperty("role", user.getRole().name());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(out));
            return;
        }
        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        handleLogout(req, resp);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        String name = getString(json, "name");
        String email = getString(json, "email");
        String password = getString(json, "password");
        String roleStr = json.has("role") && !json.get("role").isJsonNull()
                ? json.get("role").getAsString() : "BUYER";

        User.Role role;
        try {
            role = User.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid role. Use BUYER or SELLER.");
            return;
        }
        // Public registration must not create ADMIN accounts (privilege escalation).
        if (role == User.Role.ADMIN && !SecurityUtil.hasRole(req, User.Role.ADMIN)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only an admin can create admin accounts.");
            return;
        }
        try {
            User user = userService.registerUser(name, email, password, role);
            JsonObject out = new JsonObject();
            out.addProperty("status", "success");
            out.addProperty("message", "User registered successfully");
            out.addProperty("userId", user.getId());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(out));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        String email = getString(json, "email");
        String password = getString(json, "password");
        if (email == null || password == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Email and password are required.");
            return;
        }
        try {
            Optional<User> userOpt = userService.authenticateUser(email, password);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Session fixation protection: discard old session, create a new one.
                HttpSession old = req.getSession(false);
                if (old != null) {
                    old.invalidate();
                }
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(30 * 60);

                JsonObject out = new JsonObject();
                out.addProperty("status", "success");
                out.addProperty("message", "Login successful");
                out.addProperty("userId", user.getId());
                out.addProperty("role", user.getRole().name());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(out));
            } else {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login failed.");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        JsonObject out = new JsonObject();
        out.addProperty("status", "success");
        out.addProperty("message", "Logged out successfully");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(out));
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

    private String getString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return null;
        }
        try {
            return json.get(key).getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        JsonObject err = new JsonObject();
        err.addProperty("status", "error");
        err.addProperty("message", message == null ? "Request failed." : message);
        resp.getWriter().write(gson.toJson(err));
    }
}
