package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.UserDAO;
import com.arthy.arthymart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    void testRegisterUser_Success() throws SQLException {
        when(userDAO.findByEmail("test@example.com")).thenReturn(Optional.empty());

        User dummyUser = new User();
        dummyUser.setId(1);
        dummyUser.setName("Test User");
        dummyUser.setEmail("test@example.com");
        dummyUser.setRole(User.Role.BUYER);

        when(userDAO.create(any(User.class))).thenReturn(dummyUser);

        User created = userService.registerUser("Test User", "test@example.com", "password123", User.Role.BUYER);

        assertNotNull(created);
        assertEquals(1, created.getId());
        assertEquals("test@example.com", created.getEmail());
        verify(userDAO, times(1)).create(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws SQLException {
        User existingUser = new User();
        existingUser.setEmail("test@example.com");

        when(userDAO.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("Test User", "test@example.com", "password123", User.Role.BUYER);
        });

        verify(userDAO, never()).create(any(User.class));
    }
}
