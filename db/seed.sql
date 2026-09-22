-- Default Users (Password for all accounts is 'password123' hashed with jBCrypt, cost 10)
-- NOTE: The application also seeds these accounts at startup if missing, hashing at runtime.
INSERT INTO users (name, email, password_hash, role) VALUES
('System Admin', 'admin@arthymart.com', '$2a$10$xAeSXDa6GVImQMI6Z6t7Bekh5/gZvin4Qr2sTYiIXu4Oowo7bm3fG', 'ADMIN'),
('Electronics Seller', 'seller@arthymart.com', '$2a$10$xAeSXDa6GVImQMI6Z6t7Bekh5/gZvin4Qr2sTYiIXu4Oowo7bm3fG', 'SELLER'),
('John Doe Buyer', 'buyer@arthymart.com', '$2a$10$xAeSXDa6GVImQMI6Z6t7Bekh5/gZvin4Qr2sTYiIXu4Oowo7bm3fG', 'BUYER');

-- Sample Products across multiple categories
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) VALUES
(2, 'Wireless Ergonomic Mouse', '2.4GHz optical mouse with side buttons and silent click', 29.99, 50, 'Electronics', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400'),
(2, 'Mechanical Gaming Keyboard', 'RGB backlight tactile mechanical switches and braided cable', 79.50, 30, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400'),
(2, 'Noise-Cancelling Headphones', 'Over-ear wireless Bluetooth headphones with 30h battery life', 149.00, 20, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400'),
(2, 'Java Programming Masterclass', 'Comprehensive modern Java 17+ guide from syntax to architecture', 39.95, 100, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=400'),
(2, 'Stainless Steel Water Bottle', 'Double-wall vacuum insulated 750ml thermal flask', 19.99, 75, 'Home & Kitchen', 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400');

-- Sample Completed Order (so buyer can immediately review purchased products)
INSERT INTO orders (buyer_id, total_amount, status) VALUES
(3, 29.99, 'DELIVERED');

INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 29.99);

-- Sample Review on delivered product
INSERT INTO reviews (product_id, user_id, rating, comment) VALUES
(1, 3, 5, 'Super comfortable and battery life is great! Highly recommended.');
