package com.arthy.arthymart.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@WebListener
public class DatabaseConnectionListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionListener.class);
    private static HikariDataSource dataSource;
    private static String jdbcUrl = resolveJdbcUrl();

    private static String resolveJdbcUrl() {
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim();
        }
        String propUrl = System.getProperty("db.url");
        if (propUrl != null && !propUrl.trim().isEmpty()) {
            return propUrl.trim();
        }
        return "jdbc:h2:mem:arthymartdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing HikariCP connection pool for H2 Database...");
        try {
            initPool(jdbcUrl);
            runSchemaAndSeed();
            logger.info("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool", e);
            throw new RuntimeException("Database initialization failure", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        shutdown();
    }

    public static synchronized void initPool(String url) {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }
        jdbcUrl = url;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);
        dataSource = new HikariDataSource(config);
    }

    /** Health check ping executing SELECT 1 via PreparedStatement. */
    public static boolean checkHealth() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return false;
        }
    }

    /** Test hook: init an isolated in-memory DB and run schema (no seed duplicates). */
    public static synchronized void initForTests(String url) throws SQLException {
        shutdown();
        initPool(url);
        runSchemaAndSeed();
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            try {
                logger.info("Closing HikariCP connection pool...");
                dataSource.close();
            } catch (Exception e) {
                logger.warn("Error closing pool", e);
            } finally {
                dataSource = null;
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized.");
        }
        return dataSource.getConnection();
    }

    static void runSchemaAndSeed() throws SQLException {
        String schema = loadResource("/db/schema.sql");
        if (schema == null) {
            schema = loadResource("/schema.sql");
        }
        if (schema != null) {
            executeScript(schema);
        } else {
            logger.warn("schema.sql not found on classpath; assuming external migration.");
        }
        ensureAdminSeed();
        ensureSampleProducts();
    }

    private static void ensureAdminSeed() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
            check.setString(1, "admin@arthymart.com");
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            }
            String hash = BCrypt.hashpw("password123", BCrypt.gensalt(10));
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)")) {
                ins.setString(1, "System Admin");
                ins.setString(2, "admin@arthymart.com");
                ins.setString(3, hash);
                ins.setString(4, "ADMIN");
                ins.executeUpdate();
                logger.info("Seeded default admin account admin@arthymart.com");
            }
            ensureUser(conn, "Electronics Seller", "seller@arthymart.com", "SELLER");
            ensureUser(conn, "John Doe Buyer", "buyer@arthymart.com", "BUYER");
        }
    }

    private static void ensureUser(Connection conn, String name, String email, String role) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
            check.setString(1, email);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            }
        }
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt(10));
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)")) {
            ins.setString(1, name);
            ins.setString(2, email);
            ins.setString(3, hash);
            ins.setString(4, role);
            ins.executeUpdate();
        }
    }

    private static void ensureSampleProducts() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement seller = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
            seller.setString(1, "seller@arthymart.com");
            int sellerId = 2;
            try (ResultSet rs = seller.executeQuery()) {
                if (rs.next()) {
                    sellerId = rs.getInt(1);
                }
            }

            ensureProduct(conn, sellerId, "Wireless Ergonomic Mouse",
                    "2.4GHz optical mouse with side buttons and silent click",
                    new java.math.BigDecimal("29.99"), 50, "Electronics",
                    "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400");

            ensureProduct(conn, sellerId, "Mechanical Gaming Keyboard",
                    "RGB backlight tactile mechanical switches and braided cable",
                    new java.math.BigDecimal("79.50"), 30, "Electronics",
                    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400");

            ensureProduct(conn, sellerId, "Noise-Cancelling Headphones",
                    "Over-ear wireless Bluetooth headphones with 30h battery life",
                    new java.math.BigDecimal("149.00"), 20, "Electronics",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400");

            ensureProduct(conn, sellerId, "Java Programming Masterclass",
                    "Comprehensive modern Java 17+ guide from syntax to architecture",
                    new java.math.BigDecimal("39.95"), 100, "Books",
                    "https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=400");

            ensureProduct(conn, sellerId, "Stainless Steel Water Bottle",
                    "Double-wall vacuum insulated 750ml thermal flask",
                    new java.math.BigDecimal("19.99"), 75, "Home & Kitchen",
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400");

            ensureProduct(conn, sellerId, "Classic Cotton Crewneck T-Shirt",
                    "100% premium breathable combed cotton relaxed fit t-shirt",
                    new java.math.BigDecimal("24.99"), 60, "Clothing",
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=400");

            ensureProduct(conn, sellerId, "Pro Series Running Shoes",
                    "Lightweight cushioned athletic sneakers with high traction sole",
                    new java.math.BigDecimal("59.99"), 45, "Clothing",
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400");

            ensureProduct(conn, sellerId, "FIFA Official Match Football",
                    "All-weather synthetic leather competition ball with thermal bonding",
                    new java.math.BigDecimal("34.50"), 40, "Sports",
                    "https://images.unsplash.com/photo-1614632537423-1e6c2e7e0aab?w=400");

            ensureProduct(conn, sellerId, "Adjustable Dumbbells Set",
                    "Quick-change weight selector dumbbells ranging from 5 to 25 kg",
                    new java.math.BigDecimal("89.00"), 25, "Sports",
                    "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=400");

            // Seed a sample completed order and review so buyer can see order history and reviews immediately
            ensureSampleOrderAndReview(conn);
        }
    }

    private static void ensureProduct(Connection conn, int sellerId, String name, String desc,
                                      java.math.BigDecimal price, int stock, String category, String img) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT id, stock_qty FROM products WHERE name = ?")) {
            check.setString(1, name);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    int currentStock = rs.getInt("stock_qty");
                    if (currentStock <= 0) {
                        try (PreparedStatement upd = conn.prepareStatement("UPDATE products SET stock_qty = ? WHERE id = ?")) {
                            upd.setInt(1, stock);
                            upd.setInt(2, rs.getInt("id"));
                            upd.executeUpdate();
                        }
                    }
                    return;
                }
            }
        }
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ins.setInt(1, sellerId);
            ins.setString(2, name);
            ins.setString(3, desc);
            ins.setBigDecimal(4, price);
            ins.setInt(5, stock);
            ins.setString(6, category);
            ins.setString(7, img);
            ins.executeUpdate();
        }
    }

    private static void ensureSampleOrderAndReview(Connection conn) throws SQLException {
        try (PreparedStatement checkOrder = conn.prepareStatement("SELECT COUNT(*) FROM orders");
             ResultSet rs = checkOrder.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        int buyerId = 3;
        try (PreparedStatement buyer = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
            buyer.setString(1, "buyer@arthymart.com");
            try (ResultSet rs = buyer.executeQuery()) {
                if (rs.next()) {
                    buyerId = rs.getInt(1);
                }
            }
        }
        int orderId = 1;
        try (PreparedStatement insOrder = conn.prepareStatement(
                "INSERT INTO orders (buyer_id, total_amount, status) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            insOrder.setInt(1, buyerId);
            insOrder.setBigDecimal(2, new java.math.BigDecimal("29.99"));
            insOrder.setString(3, "DELIVERED");
            insOrder.executeUpdate();
            try (ResultSet rs = insOrder.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }
        }
        try (PreparedStatement insItem = conn.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, 1, 1, ?)")) {
            insItem.setInt(1, orderId);
            insItem.setBigDecimal(2, new java.math.BigDecimal("29.99"));
            insItem.executeUpdate();
        }
        try (PreparedStatement insReview = conn.prepareStatement(
                "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (1, ?, 5, ?)")) {
            insReview.setInt(1, buyerId);
            insReview.setString(2, "Super comfortable and battery life is great! Highly recommended.");
            insReview.executeUpdate();
        }
    }

    private static String loadResource(String path) {
        InputStream in = DatabaseConnectionListener.class.getResourceAsStream(path);
        if (in == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            logger.warn("Failed to load resource {}", path, e);
            return null;
        }
    }

    private static void executeScript(String script) throws SQLException {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            for (String stmt : script.split(";")) {
                String trimmed = stmt.trim();
                if (!trimmed.isEmpty()) {
                    st.execute(trimmed);
                }
            }
        }
    }
}
