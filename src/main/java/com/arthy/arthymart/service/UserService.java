package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.UserDAO;
import com.arthy.arthymart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerUser(String name, String email, String password, User.Role role) throws SQLException, IllegalArgumentException {
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(role != null ? role : User.Role.BUYER);

        return userDAO.create(newUser);
    }

    public Optional<User> authenticateUser(String email, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
