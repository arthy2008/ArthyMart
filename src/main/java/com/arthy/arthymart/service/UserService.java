package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.UserDAO;
import com.arthy.arthymart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerUser(String name, String email, String password, User.Role role) throws SQLException, IllegalArgumentException {
        if (name == null || name.trim().isEmpty() || name.trim().length() > 100) {
            throw new IllegalArgumentException("Name is required (max 100 characters).");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim().toLowerCase()).matches()) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if (password.length() > 72) {
            throw new IllegalArgumentException("Password is too long (max 72 characters for BCrypt).");
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (userDAO.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        User.Role finalRole = (role != null) ? role : User.Role.BUYER;

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        User newUser = new User();
        newUser.setName(name.trim());
        newUser.setEmail(normalizedEmail);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(finalRole);

        return userDAO.create(newUser);
    }

    public Optional<User> authenticateUser(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return Optional.empty();
        }
        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            try {
                if (BCrypt.checkpw(password, user.getPasswordHash())) {
                    return Optional.of(user);
                }
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
