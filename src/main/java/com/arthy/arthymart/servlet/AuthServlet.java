package com.arthy.arthymart.servlet;

import com.arthy.arthymart.model.User;
import com.arthy.arthymart.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
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
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        JsonObject jsonRequest = gson.fromJson(sb.toString(), JsonObject.class);

        try {
            if ("/register".equals(pathInfo)) {
                String name = jsonRequest.get("name").getAsString();
                String email = jsonRequest.get("email").getAsString();
                String password = jsonRequest.get("password").getAsString();
                String roleStr = jsonRequest.has("role") ? jsonRequest.get("role").getAsString() : "BUYER";

                User.Role role = User.Role.valueOf(roleStr.toUpperCase());
                User user = userService.registerUser(name, email, password, role);

                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "User registered successfully");
                responseJson.addProperty("userId", user.getId());

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(responseJson));

            } else if ("/login".equals(pathInfo)) {
                String email = jsonRequest.get("email").getAsString();
                String password = jsonRequest.get("password").getAsString();

                Optional<User> userOpt = userService.authenticateUser(email, password);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    HttpSession session = req.getSession(true);
                    session.setAttribute("user", user);

                    JsonObject responseJson = new JsonObject();
                    responseJson.addProperty("status", "success");
                    responseJson.addProperty("message", "Login successful");
                    responseJson.addProperty("userId", user.getId());
                    responseJson.addProperty("role", user.getRole().name());

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(responseJson));
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid credentials\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"status\":\"error\",\"message\":\"Endpoint not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", e.getMessage());
            resp.getWriter().write(gson.toJson(errorJson));
        }
    }
}
