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

-- Insert sample products
INSERT INTO products (name, description, category, price, image) VALUES 
('Laptops X1', 'High performance laptop 16GB RAM', 'Electronics', 999.99, 'laptop.jpg'),
('Wireless Mouse', 'Ergonomic wireless mouse', 'Accessories', 29.99, 'mouse.jpg'),
('Mechanical Keyboard', 'RGB Mechanical Keyboard Blue Switches', 'Accessories', 79.99, 'keyboard.jpg'),
('Smartphone Pro', 'Latest smartphone with 5G', 'Electronics', 699.99, 'phone.jpg');
