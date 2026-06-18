CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       role VARCHAR(20) DEFAULT 'user'
);

CREATE TABLE products (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          category VARCHAR(100),
                          price DOUBLE NOT NULL,
                          image VARCHAR(255)
);

CREATE TABLE cart (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      user_id INT NOT NULL,
                      product_id INT NOT NULL,
                      quantity INT DEFAULT 1,
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                      FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE orders (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT NOT NULL,
                        total_amount DOUBLE NOT NULL,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(50) DEFAULT 'Pending',
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE order_items (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             price DOUBLE NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Insert admin
INSERT INTO users (name, email, password, role) VALUES ('Admin', 'admin@shop.com', 'admin123', 'admin');
INSERT INTO users (name, email, password, role) VALUES ('Test User', 'user@shop.com', 'user123', 'user');

-- Insert dummy data
INSERT INTO products (name, description, category, price, image) VALUES
                                                                     ('Giày Bóng Đá Nike Mercurial', 'Giày bóng đá sân cỏ nhân tạo siêu nhẹ', 'Football', 129.99, 'https://images.unsplash.com/photo-1511886929837-354d827aae26?w=500&q=80'),
                                                                     ('Quả Bóng Đá Adidas Al Rihla', 'Bóng thi đấu chính thức FIFA', 'Football', 45.00, 'https://images.unsplash.com/photo-1614632537190-23e4146777db?w=500&q=80'),
                                                                     ('Giày Chạy Bộ Asics Novablast', 'Giày chạy bộ đệm dày cực êm', 'Running', 150.00, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80'),
                                                                     ('Tạ Đơn Cao Su 10kg', 'Tạ tay bọc cao su cao cấp cho dân Gym', 'Gym & Fitness', 30.00, 'https://images.unsplash.com/photo-1638202993928-7267aad84c31?w=500&q=80'),
                                                                     ('Thảm Tập Yoga Manduka', 'Thảm Yoga chống trượt chuyên nghiệp', 'Accessories', 85.00, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500&q=80');