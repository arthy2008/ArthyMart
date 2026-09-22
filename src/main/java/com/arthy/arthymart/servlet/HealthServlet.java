package com.arthy.arthymart.servlet;

import com.arthy.arthymart.util.DatabaseConnectionListener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Health check endpoint verifying service status and active database connectivity.
 * GET /api/v1/health or GET /api/health
 *
 * Expected response:
 * {
 *   "status": "UP",
 *   "db": "UP"
 * }
 */
@WebServlet(name = "HealthServlet", urlPatterns = {"/api/v1/health", "/api/health"})
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        boolean dbUp = DatabaseConnectionListener.checkHealth();

        if (dbUp) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"status\":\"UP\",\"db\":\"UP\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.getWriter().write("{\"status\":\"DOWN\",\"db\":\"DOWN\"}");
        }
    }
}
