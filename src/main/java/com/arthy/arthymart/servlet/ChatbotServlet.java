package com.arthy.arthymart.servlet;

import com.arthy.arthymart.service.ChatbotService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/chat")
public class ChatbotServlet extends HttpServlet {

    private final ChatbotService chatbotService = new ChatbotService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> reply = chatbotService.handleMessage("hi");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(reply));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            String message = "";
            if (body != null && !body.trim().isEmpty()) {
                JsonObject json = gson.fromJson(body, JsonObject.class);
                if (json != null && json.has("message") && !json.get("message").isJsonNull()) {
                    message = json.get("message").getAsString();
                }
            }

            Map<String, Object> reply = chatbotService.handleMessage(message);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(reply));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject err = new JsonObject();
            err.addProperty("status", "error");
            err.addProperty("message", "Chatbot service temporarily unavailable.");
            resp.getWriter().write(gson.toJson(err));
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
}
