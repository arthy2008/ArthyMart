package com.arthy.arthymart.dao;

import com.arthy.arthymart.model.User;
import com.arthy.arthymart.util.DatabaseConnectionListener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
        return user;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, role, created_at FROM users ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
        }
        return users;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnectionListener.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }
}
