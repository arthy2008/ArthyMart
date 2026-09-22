package com.arthy.arthymart.filter;

import com.arthy.arthymart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AuthFilterTest {

    private AuthFilter authFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private HttpSession session;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        authFilter = new AuthFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        session = mock(HttpSession.class);
        responseWriter = new StringWriter();

        when(request.getContextPath()).thenReturn("/arthymart");
        when(request.getSession(false)).thenReturn(session);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testPublicEndpointsPassThrough() throws Exception {
        when(request.getRequestURI()).thenReturn("/arthymart/api/v1/health");
        authFilter.doFilter(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);

        when(request.getRequestURI()).thenReturn("/arthymart/products.jsp");
        authFilter.doFilter(request, response, chain);
        verify(chain, times(2)).doFilter(request, response);

        when(request.getRequestURI()).thenReturn("/arthymart/api/products");
        when(request.getMethod()).thenReturn("GET");
        authFilter.doFilter(request, response, chain);
        verify(chain, times(3)).doFilter(request, response);
    }

    @Test
    void testUnauthenticatedAccessToCartApiReturns401() throws Exception {
        when(request.getRequestURI()).thenReturn("/arthymart/api/cart");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(null);

        authFilter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(request, response);
        assertTrue(responseWriter.toString().contains("Unauthorized"));
    }

    @Test
    void testUnauthenticatedAccessToSellerPageRedirectsToLogin() throws Exception {
        when(request.getRequestURI()).thenReturn("/arthymart/seller/dashboard.jsp");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(null);

        authFilter.doFilter(request, response, chain);

        verify(response).sendRedirect("/arthymart/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void testBuyerAccessingAdminApiReturns403() throws Exception {
        User buyer = new User();
        buyer.setId(3);
        buyer.setRole(User.Role.BUYER);
        when(session.getAttribute("user")).thenReturn(buyer);

        when(request.getRequestURI()).thenReturn("/arthymart/api/admin/users");
        when(request.getMethod()).thenReturn("GET");

        authFilter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(chain, never()).doFilter(request, response);
        assertTrue(responseWriter.toString().contains("Admin access required"));
    }

    @Test
    void testAdminAccessingAdminApiAllowed() throws Exception {
        User admin = new User();
        admin.setId(1);
        admin.setRole(User.Role.ADMIN);
        when(session.getAttribute("user")).thenReturn(admin);

        when(request.getRequestURI()).thenReturn("/arthymart/api/admin/users");
        when(request.getMethod()).thenReturn("GET");

        authFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void testSellerCreatingProductAllowed() throws Exception {
        User seller = new User();
        seller.setId(2);
        seller.setRole(User.Role.SELLER);
        when(session.getAttribute("user")).thenReturn(seller);

        when(request.getRequestURI()).thenReturn("/arthymart/api/products");
        when(request.getMethod()).thenReturn("POST");

        authFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void testBuyerCreatingProductRejectedWith403() throws Exception {
        User buyer = new User();
        buyer.setId(3);
        buyer.setRole(User.Role.BUYER);
        when(session.getAttribute("user")).thenReturn(buyer);

        when(request.getRequestURI()).thenReturn("/arthymart/api/products");
        when(request.getMethod()).thenReturn("POST");

        authFilter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(chain, never()).doFilter(request, response);
    }
}
