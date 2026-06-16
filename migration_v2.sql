-- ShopSphere Phase 1 Migration Script

-- 1. Create Addresses Table
CREATE TABLE IF NOT EXISTS addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Add address_id and payment_method to orders table
ALTER TABLE orders ADD COLUMN address_id INT;
ALTER TABLE orders ADD FOREIGN KEY (address_id) REFERENCES addresses(id);
ALTER TABLE orders ADD COLUMN payment_method VARCHAR(50) DEFAULT 'COD';

-- 3. Add stock column to products table
ALTER TABLE products ADD COLUMN stock INT DEFAULT 10;
