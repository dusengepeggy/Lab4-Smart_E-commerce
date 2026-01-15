-- Sample Data for Smart E-Commerce System
-- This script populates the database with sample data for testing and demonstration

BEGIN;

-- Insert Sample Categories
INSERT INTO Category (category_name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Physical and digital books'),
('Home & Garden', 'Home improvement and gardening supplies'),
('Sports & Outdoors', 'Sports equipment and outdoor gear'),
('Toys & Games', 'Toys, games, and entertainment items');

-- Insert Sample Users
-- Note: In production, passwords should be hashed using bcrypt or similar
-- For demo purposes, these are plain text (NOT SECURE - for testing only)
INSERT INTO "User" (username, password, role, email) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'admin@ecommerce.com'),
('john_doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Customer', 'john.doe@email.com'),
('jane_smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Customer', 'jane.smith@email.com'),
('bob_wilson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Customer', 'bob.wilson@email.com'),
('alice_brown', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Customer', 'alice.brown@email.com');

-- Insert Sample Products
INSERT INTO Product (category_id, name, description, price) VALUES
-- Electronics
(1, 'Laptop Pro 15', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99),
(1, 'Wireless Mouse', 'Ergonomic wireless mouse with 2-year battery life', 29.99),
(1, 'Mechanical Keyboard', 'RGB backlit mechanical keyboard with Cherry MX switches', 89.99),
(1, 'USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader', 49.99),
(1, 'Bluetooth Headphones', 'Noise-cancelling over-ear headphones with 30-hour battery', 199.99),
(1, 'Smart Watch', 'Fitness tracking smartwatch with heart rate monitor', 249.99),

-- Clothing
(2, 'Cotton T-Shirt', '100% organic cotton t-shirt, available in multiple colors', 19.99),
(2, 'Denim Jeans', 'Classic fit denim jeans with stretch fabric', 59.99),
(2, 'Winter Jacket', 'Waterproof winter jacket with insulated lining', 129.99),
(2, 'Running Shoes', 'Lightweight running shoes with cushioned sole', 79.99),
(2, 'Baseball Cap', 'Adjustable baseball cap with embroidered logo', 24.99),
(2, 'Wool Sweater', 'Warm wool sweater perfect for cold weather', 69.99),

-- Books
(3, 'Database Design Guide', 'Comprehensive guide to relational database design', 39.99),
(3, 'Java Programming', 'Introduction to Java programming for beginners', 49.99),
(3, 'Data Structures & Algorithms', 'Advanced algorithms and data structures textbook', 59.99),
(3, 'Web Development Handbook', 'Complete guide to modern web development', 44.99),
(3, 'Software Engineering Principles', 'Best practices in software engineering', 54.99),

-- Home & Garden
(4, 'Garden Tool Set', 'Complete set of 5 essential gardening tools', 34.99),
(4, 'Indoor Plant Pot', 'Ceramic plant pot with drainage holes, 8-inch', 15.99),
(4, 'LED Desk Lamp', 'Adjustable LED desk lamp with USB charging port', 29.99),
(4, 'Kitchen Knife Set', 'Professional 6-piece stainless steel knife set', 89.99),
(4, 'Storage Baskets', 'Set of 3 woven storage baskets for home organization', 24.99),

-- Sports & Outdoors
(5, 'Yoga Mat', 'Non-slip yoga mat with carrying strap', 29.99),
(5, 'Dumbbell Set', 'Adjustable dumbbell set, 5-25 lbs per dumbbell', 149.99),
(5, 'Camping Tent', '4-person camping tent with rainfly', 199.99),
(5, 'Bicycle Helmet', 'Safety-certified bicycle helmet with ventilation', 49.99),
(5, 'Water Bottle', 'Insulated stainless steel water bottle, 32oz', 24.99),

-- Toys & Games
(6, 'Board Game Collection', 'Set of 5 classic board games', 59.99),
(6, 'Puzzle Set', '1000-piece jigsaw puzzle with scenic landscape', 19.99),
(6, 'Building Blocks', '500-piece building blocks set for creative play', 34.99),
(6, 'Card Game', 'Strategy card game for 2-4 players', 14.99),
(6, 'Remote Control Car', 'RC car with 2.4GHz remote control', 49.99);

-- Insert Sample Inventory
INSERT INTO Inventory (product_id, stock_quantity, warehouse_location) VALUES
(1, 15, 'Warehouse A - Section 1'),
(2, 50, 'Warehouse A - Section 2'),
(3, 30, 'Warehouse A - Section 2'),
(4, 40, 'Warehouse A - Section 2'),
(5, 25, 'Warehouse A - Section 1'),
(6, 20, 'Warehouse A - Section 1'),
(7, 100, 'Warehouse B - Section 3'),
(8, 60, 'Warehouse B - Section 3'),
(9, 35, 'Warehouse B - Section 4'),
(10, 45, 'Warehouse B - Section 4'),
(11, 80, 'Warehouse B - Section 3'),
(12, 40, 'Warehouse B - Section 4'),
(13, 25, 'Warehouse C - Section 5'),
(14, 30, 'Warehouse C - Section 5'),
(15, 20, 'Warehouse C - Section 5'),
(16, 28, 'Warehouse C - Section 5'),
(17, 22, 'Warehouse C - Section 5'),
(18, 15, 'Warehouse D - Section 6'),
(19, 30, 'Warehouse D - Section 6'),
(20, 25, 'Warehouse D - Section 6'),
(21, 35, 'Warehouse D - Section 6'),
(22, 50, 'Warehouse D - Section 6'),
(23, 40, 'Warehouse E - Section 7'),
(24, 55, 'Warehouse E - Section 7'),
(25, 30, 'Warehouse E - Section 7'),
(26, 20, 'Warehouse E - Section 7'),
(27, 25, 'Warehouse E - Section 7'),
(28, 35, 'Warehouse E - Section 7'),
(29, 18, 'Warehouse E - Section 7'),
(30, 42, 'Warehouse E - Section 7');

-- Insert Sample Orders
INSERT INTO "Order" (user_id, order_date, total_amount, status) VALUES
(2, '2024-01-15', 89.99, 'Delivered'),
(2, '2024-02-20', 199.99, 'Shipped'),
(3, '2024-01-10', 149.98, 'Delivered'),
(3, '2024-03-05', 79.99, 'Pending'),
(4, '2024-02-14', 129.99, 'Delivered'),
(4, '2024-03-10', 49.99, 'Shipped'),
(5, '2024-01-25', 199.99, 'Delivered'),
(5, '2024-02-28', 59.99, 'Pending');

-- Insert Sample Order Items
INSERT INTO OrderItem (order_id, product_id, quantity, unit_price) VALUES
-- Order 1 (john_doe - Delivered)
(1, 3, 1, 89.99),

-- Order 2 (john_doe - Shipped)
(2, 5, 1, 199.99),

-- Order 3 (jane_smith - Delivered)
(3, 10, 1, 79.99),
(3, 11, 1, 24.99),
(3, 12, 1, 45.00), -- Note: Price may have changed

-- Order 4 (jane_smith - Pending)
(4, 10, 1, 79.99),

-- Order 5 (bob_wilson - Delivered)
(5, 9, 1, 129.99),

-- Order 6 (bob_wilson - Shipped)
(6, 4, 1, 49.99),

-- Order 7 (alice_brown - Delivered)
(7, 5, 1, 199.99),

-- Order 8 (alice_brown - Pending)
(8, 7, 1, 59.99);

-- Insert Sample Reviews
INSERT INTO Review (user_id, product_id, rating, comment, review_date) VALUES
(2, 3, 5, 'Excellent keyboard! The mechanical switches feel great and the RGB lighting is amazing.', '2024-01-20'),
(2, 5, 4, 'Great headphones, good sound quality. Battery life is as advertised.', '2024-02-25'),
(3, 10, 5, 'Very comfortable running shoes. Perfect for my daily runs.', '2024-01-15'),
(3, 11, 4, 'Nice cap, good quality material. Fits well.', '2024-01-18'),
(4, 9, 5, 'Warm and stylish jacket. Perfect for winter weather.', '2024-02-20'),
(4, 4, 4, 'Works well with my laptop. All ports function correctly.', '2024-03-12'),
(5, 5, 5, 'Best headphones I''ve owned. Noise cancellation is fantastic!', '2024-02-05'),
(5, 7, 3, 'Good quality jeans, but the fit could be better. Runs a bit large.', '2024-02-15'),
(2, 1, 5, 'Outstanding laptop! Fast performance and great build quality.', '2024-03-01'),
(3, 13, 4, 'Very informative book. Great for learning database concepts.', '2024-01-22');

COMMIT;

-- Verification Queries (Optional - run these to verify data insertion)
-- SELECT COUNT(*) FROM Category; -- Should return 6
-- SELECT COUNT(*) FROM "User"; -- Should return 5
-- SELECT COUNT(*) FROM Product; -- Should return 30
-- SELECT COUNT(*) FROM Inventory; -- Should return 30
-- SELECT COUNT(*) FROM "Order"; -- Should return 8
-- SELECT COUNT(*) FROM OrderItem; -- Should return 10
-- SELECT COUNT(*) FROM Review; -- Should return 10
