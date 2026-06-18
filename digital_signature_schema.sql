-- =====================================================
-- ShopSphere Digital Signature System - Database Schema
-- =====================================================
-- Run this AFTER ecommerce_db.sql and migration_v2.sql

USE ecommerce_db;

-- 1. Khóa công khai của người dùng
CREATE TABLE IF NOT EXISTS user_keys (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT NOT NULL,
    public_key  LONGTEXT NOT NULL,
    fingerprint VARCHAR(64) NOT NULL,       -- SHA256(public_key_base64) hex
    issue_date  DATETIME DEFAULT NOW(),
    revoke_date DATETIME,
    status      VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE | REVOKED
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Lịch sử hành động với khóa
CREATE TABLE IF NOT EXISTS key_history (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT NOT NULL,
    key_id      INT NOT NULL,
    action      VARCHAR(20) NOT NULL,       -- GENERATED | REVOKED
    action_date DATETIME DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (key_id)  REFERENCES user_keys(id) ON DELETE CASCADE
);

-- 3. Yêu cầu ký - lưu JSON gốc mỗi lần Download order.json
CREATE TABLE IF NOT EXISTS order_sign_requests (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    order_id            INT NOT NULL,
    user_id             INT NOT NULL,
    original_order_json LONGTEXT NOT NULL,
    original_order_hash VARCHAR(255) NOT NULL,
    download_time       DATETIME DEFAULT NOW(),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Chữ ký của đơn hàng (1 đơn chỉ có 1 chữ ký)
CREATE TABLE IF NOT EXISTS order_signatures (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    order_id        INT NOT NULL UNIQUE,
    key_id          INT NOT NULL,
    request_id      INT NOT NULL,
    signature_data  LONGTEXT NOT NULL,
    sign_time       DATETIME DEFAULT NOW(),
    verify_status   VARCHAR(20) DEFAULT 'SIGNED',
    -- NOT_SIGNED | SIGNED | VERIFIED | INVALID | TAMPERED | KEY_REVOKED
    FOREIGN KEY (order_id)   REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (key_id)     REFERENCES user_keys(id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES order_sign_requests(id) ON DELETE CASCADE
);

-- 5. Ghi mỗi lần verify (kể cả thất bại)
CREATE TABLE IF NOT EXISTS verification_logs (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    order_id    INT NOT NULL,
    user_id     INT NOT NULL,
    verify_time DATETIME DEFAULT NOW(),
    result      VARCHAR(20) NOT NULL,       -- VERIFIED | INVALID | TAMPERED | KEY_REVOKED
    message     TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- 6. Nhật ký hành động bảo mật
CREATE TABLE IF NOT EXISTS audit_logs (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT,
    order_id    INT,
    action_type VARCHAR(50) NOT NULL,
    -- UPLOAD_PUBLIC_KEY | UPLOAD_SIG | VERIFY
    -- REVOKE_KEY | DOWNLOAD_JSON | DOWNLOAD_SIGNATURE
    description TEXT,
    created_at  DATETIME DEFAULT NOW()
);
