CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS employee (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary DOUBLE NOT NULL
);

INSERT INTO employee (name, department, salary) VALUES
('Rohit Sharma', 'Engineering', 85000.00),
('Neha Gupta', 'Human Resources', 62000.00),
('Vikram Mehta', 'Product', 95000.00);
