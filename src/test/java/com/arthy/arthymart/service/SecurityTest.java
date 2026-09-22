package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.UserDAO;
import com.arthy.arthymart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    void testWeakPasswordRejected() throws Exception {
        when(userDAO.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("A", "a@example.com", "short", User.Role.BUYER));
    }

    @Test
    void testInvalidEmailRejected() throws Exception {
        when(userDAO.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("A", "not-an-email", "password123", User.Role.BUYER));
    }

    @Test
    void testPasswordIsHashedNotPlaintext() throws Exception {
        when(userDAO.findByEmail("h@example.com")).thenReturn(Optional.empty());
        when(userDAO.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User created = userService.registerUser("H", "h@example.com", "password123", User.Role.BUYER);
        assertNotNull(created.getPasswordHash());
        assertNotEquals("password123", created.getPasswordHash());
        assertTrue(created.getPasswordHash().startsWith("$2a$"));
    }

    @Test
    void testAuthenticateNullSafe() throws Exception {
        assertTrue(userService.authenticateUser(null, null).isEmpty());
        assertTrue(userService.authenticateUser("x@example.com", null).isEmpty());
    }
}
