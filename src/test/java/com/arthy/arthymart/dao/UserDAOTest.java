package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.User;
import com.arthy.arthymart.util.DatabaseConnectionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnectionListener.initForTests("jdbc:h2:mem:userdaotest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        userDAO = new UserDAO();
    }

    @Test
    void testCreateAndFindUser() throws SQLException {
        User user = new User();
        user.setName("Alice Test");
        user.setEmail("alice@test.com");
        user.setPasswordHash("hashedpassword123");
        user.setRole(User.Role.BUYER);

        User created = userDAO.create(user);
        assertTrue(created.getId() > 0);

        Optional<User> byEmail = userDAO.findByEmail("alice@test.com");
        assertTrue(byEmail.isPresent());
        assertEquals("Alice Test", byEmail.get().getName());
        assertEquals(User.Role.BUYER, byEmail.get().getRole());

        Optional<User> byId = userDAO.findById(created.getId());
        assertTrue(byId.isPresent());
        assertEquals("alice@test.com", byId.get().getEmail());
    }

    @Test
    void testDuplicateEmailThrowsSQLException() throws SQLException {
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("duplicate@test.com");
        user1.setPasswordHash("hash1");
        user1.setRole(User.Role.BUYER);
        userDAO.create(user1);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("duplicate@test.com");
        user2.setPasswordHash("hash2");
        user2.setRole(User.Role.BUYER);

        assertThrows(SQLException.class, () -> userDAO.create(user2));
    }

    @Test
    void testFindAllAndDeleted() throws SQLException {
        List<User> initialUsers = userDAO.findAll();
        assertFalse(initialUsers.isEmpty());

        User temp = new User();
        temp.setName("To Delete");
        temp.setEmail("todelete@test.com");
        temp.setPasswordHash("hash");
        temp.setRole(User.Role.BUYER);
        User created = userDAO.create(temp);

        boolean deleted = userDAO.delete(created.getId());
        assertTrue(deleted);

        Optional<User> afterDelete = userDAO.findById(created.getId());
        assertTrue(afterDelete.isEmpty());
    }
}
