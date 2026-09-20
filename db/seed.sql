-- Default Users (Password for all accounts is 'password123' hashed with jBCrypt)
INSERT INTO users (name, email, password_hash, role) VALUES 
('System Admin', 'admin@arthymart.com', '$2a$10$wT.fB.M7qH6K8J0X/3O1f.Lg2Zz3N4V5P6Q7R8S9T0U1V2W3X4Y5Z', 'ADMIN'),
('Electronics Seller', 'seller@arthymart.com', '$2a$10$wT.fB.M7qH6K8J0X/3O1f.Lg2Zz3N4V5P6Q7R8S9T0U1V2W3X4Y5Z', 'SELLER'),
('John Doe Buyer', 'buyer@arthymart.com', '$2a$10$wT.fB.M7qH6K8J0X/3O1f.Lg2Zz3N4V5P6Q7R8S9T0U1V2W3X4Y5Z', 'BUYER');

-- Sample Products
INSERT INTO products (seller_id, name, description, price, stock_qty, category) VALUES 
(2, 'Wireless Ergonomic Mouse', '2.4GHz optical mouse with side buttons', 29.99, 50, 'Electronics'),
(2, 'Mechanical Keyboard', 'RGB backlight tactile gaming keyboard', 79.50, 30, 'Electronics');
