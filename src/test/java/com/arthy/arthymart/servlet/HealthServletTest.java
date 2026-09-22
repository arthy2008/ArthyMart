package com.arthy.arthymart.servlet;

import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class HealthServletTest {

    private HealthServlet healthServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:healthtest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        healthServlet = new HealthServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testHealthEndpointReturnsUpWhenDbConnected() throws Exception {
        healthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");

        String json = responseWriter.toString();
        assertTrue(json.contains("\"status\":\"UP\""));
        assertTrue(json.contains("\"db\":\"UP\""));
    }

    @Test
    void testHealthEndpointReturnsDownWhenDbClosed() throws Exception {
        DatabaseConnectionListener.shutdown();

        healthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        String json = responseWriter.toString();
        assertTrue(json.contains("\"status\":\"DOWN\""));
        assertTrue(json.contains("\"db\":\"DOWN\""));
    }
}
