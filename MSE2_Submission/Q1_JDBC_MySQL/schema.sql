CREATE DATABASE IF NOT EXISTS retail_db;
USE retail_db;

CREATE TABLE IF NOT EXISTS Product (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL
);

-- Initial sample records for testing
INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES
(101, 'Logitech Wireless Mouse', 'Electronics', 15, 899.00),
(102, 'Mechanical Gaming Keyboard', 'Electronics', 8, 2499.50),
(103, 'USB-C Fast Charging Cable', 'Electronics', 45, 349.00),
(104, 'Ballpoint Pen Set (10-Pack)', 'Stationery', 5, 120.00),
(105, 'Spiral Bound Notebook A4', 'Stationery', 25, 180.00),
(106, 'Out of Stock Item Sample', 'Stationery', 0, 99.00)
ON DUPLICATE KEY UPDATE product_name=VALUES(product_name);
