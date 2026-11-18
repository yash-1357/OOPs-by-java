# OOPJ (CS336B) - Moodle MSE2 Submission

---

### Student Information
| Field | Details |
| :--- | :--- |
| **Name** | Yash Raj Singh |
| **Branch** | Information Technology (IT) |
| **Roll Number** | 2500290130224 |
| **Section** | C |
| **Shift** | Evening |
| **Case Study Name** | **Q1:** Inventory Management System \| **Q2:** Employee Management REST API |

---

## Q1. JDBC with MySQL — Scenario-Based (10 Marks)
**Case Study:** Inventory Management System (`retail_db.Product`)

### Moodle Upload / GitHub Repository Link
| Platform | Repository / Upload Link |
| :--- | :--- |
| **GitHub Repository Link** | `https://github.com/yash-1357/OOPs-by-java/tree/main/MSE2_Submission/Q1_JDBC_MySQL` |
| **Moodle Upload** | Submitted on Moodle Portal (`Yash_2500290130224_MSE2_Q1.zip`) |

---

### Project Structure (VS Code / IntelliJ / Eclipse)
```text
Q1_JDBC_MySQL/
│
├── schema.sql                               # Database Schema (retail_db.Product)
└── src/
    └── com/
        └── oopj/
            ├── model/
            │   └── Product.java             # Entity Model (id, name, category, quantity, price)
            ├── dao/
            │   ├── DBConnection.java        # JDBC MySQL Connection Manager
            │   └── ProductDAO.java          # Operations (a, b, c, d, e) with PreparedStatement
            ├── controller/
            │   └── ProductController.java   # HTTP Controller for Postman API testing
            ├── InventoryMenuApp.java        # Menu-Driven Console Application (Scenario Requirements)
            └── MainApplication.java         # Server Runner for Postman (Port 8080)
```

---

### Database Schema (`schema.sql`)
```sql
CREATE DATABASE IF NOT EXISTS retail_db;
USE retail_db;

CREATE TABLE IF NOT EXISTS Product (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL
);

-- Seed records for testing
INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES
(101, 'Logitech Wireless Mouse', 'Electronics', 15, 899.00),
(102, 'Mechanical Gaming Keyboard', 'Electronics', 8, 2499.50),
(103, 'USB-C Fast Charging Cable', 'Electronics', 45, 349.00),
(104, 'Ballpoint Pen Set (10-Pack)', 'Stationery', 5, 120.00),
(105, 'Spiral Bound Notebook A4', 'Stationery', 25, 180.00),
(106, 'Out of Stock Item Sample', 'Stationery', 0, 99.00);
```

---

### Project Code (Q1)

#### 1. Model: `src/com/oopj/model/Product.java`
```java
package com.oopj.model;

/**
 * Model class representing the Product entity.
 * Table: retail_db.Product
 */
public class Product {
    private int productId;
    private String productName;
    private String category;
    private int quantity;
    private double price;

    public Product() {}

    public Product(int productId, String productName, String category, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("Product [ID=%d, Name='%s', Category='%s', Quantity=%d, Price=Rs. %.2f]",
                productId, productName, category, quantity, price);
    }

    public String toJson() {
        return String.format(
            "{\"productId\": %d, \"productName\": \"%s\", \"category\": \"%s\", \"quantity\": %d, \"price\": %.2f}",
            productId, productName.replace("\"", "\\\""), category.replace("\"", "\\\""), quantity, price
        );
    }
}
```

#### 2. DB Connection: `src/com/oopj/dao/DBConnection.java`
```java
package com.oopj.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/retail_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Update as per local MySQL credentials

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found in classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

#### 3. DAO: `src/com/oopj/dao/ProductDAO.java`
*(Covers operations a, b, c, d, e)*
```java
package com.oopj.dao;

import com.oopj.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product ORDER BY product_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT * FROM Product WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // (a) Insert Product Records using PreparedStatement (Handle duplicate product IDs)
    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProductId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getCategory());
            ps.setInt(4, product.getQuantity());
            ps.setDouble(5, product.getPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: Duplicate Product ID " + product.getProductId() + " already exists in retail_db!");
            return false;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // (b) Conditional Retrieval: Category filter, quantity < 20, sorted by price DESC
    public List<Product> getLowStockByCategory(String category) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE LOWER(category) = LOWER(?) AND quantity < 20 ORDER BY price DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // (c) Update Product Stock: Add quantity to existing stock
    public boolean updateStock(int productId, int additionalQuantity) throws SQLException {
        Product existing = getProductById(productId);
        if (existing == null) {
            System.out.println("Product with ID " + productId + " does not exist!");
            return false;
        }
        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, additionalQuantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        }
    }

    // (d) Search & Modify: Retrieve product details and update price only if price > 0
    public Product updatePrice(int productId, double newPrice) throws SQLException {
        if (newPrice <= 0) {
            System.out.println("Invalid Price: Price must be greater than zero!");
            return null;
        }
        Product existing = getProductById(productId);
        if (existing == null) {
            System.out.println("Product with ID " + productId + " does not exist!");
            return null;
        }
        String sql = "UPDATE Product SET price = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            return rows > 0 ? getProductById(productId) : null;
        }
    }

    // (e) Aggregate Query: Total inventory value using SUM(quantity * price)
    public double getTotalInventoryValue() throws SQLException {
        String sql = "SELECT SUM(quantity * price) AS total_value FROM Product";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble("total_value") : 0.0;
        }
    }

    // (e) Conditional Deletion: Delete record only if quantity is zero
    public boolean deleteIfZeroQuantity(int productId) throws SQLException {
        Product existing = getProductById(productId);
        if (existing == null) {
            System.out.println("Product ID " + productId + " not found!");
            return false;
        }
        if (existing.getQuantity() > 0) {
            System.out.println("Cannot delete: Product ID " + productId + " has " + existing.getQuantity() +
                    " items in stock! Only zero-quantity products can be deleted.");
            return false;
        }
        String sql = "DELETE FROM Product WHERE product_id = ? AND quantity = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price")
        );
    }
}
```

#### 4. Menu-Driven Console Application: `src/com/oopj/InventoryMenuApp.java`
```java
package com.oopj;

import com.oopj.dao.ProductDAO;
import com.oopj.model.Product;
import java.util.List;
import java.util.Scanner;

public class InventoryMenuApp {
    private static final ProductDAO productDAO = new ProductDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n========== INVENTORY MANAGEMENT SYSTEM (JDBC) ==========");
            System.out.println("1. (a) Insert Product Record (PreparedStatement)");
            System.out.println("2. (b) Retrieve by Category (Quantity < 20, Price DESC)");
            System.out.println("3. (c) Update Product Stock (Add Quantity)");
            System.out.println("4. (d) Search & Modify Price (Check Price > 0)");
            System.out.println("5. (e) Display Total Inventory Value & Delete Zero-Stock Item");
            System.out.println("6. View All Products");
            System.out.println("7. Exit");
            System.out.print("Enter choice (1-7): ");

            int choice = Integer.parseInt(scanner.nextLine().trim());
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Product ID: "); int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Product Name: "); String name = scanner.nextLine();
                        System.out.print("Category: "); String cat = scanner.nextLine();
                        System.out.print("Quantity: "); int qty = Integer.parseInt(scanner.nextLine());
                        System.out.print("Price: "); double pr = Double.parseDouble(scanner.nextLine());
                        boolean ok = productDAO.insertProduct(new Product(id, name, cat, qty, pr));
                        System.out.println(ok ? "Inserted successfully!" : "Failed to insert.");
                        break;
                    case 2:
                        System.out.print("Enter Category: "); String c = scanner.nextLine();
                        List<Product> lowStock = productDAO.getLowStockByCategory(c);
                        lowStock.forEach(System.out::println);
                        break;
                    case 3:
                        System.out.print("Product ID: "); int pid = Integer.parseInt(scanner.nextLine());
                        System.out.print("Quantity to add: "); int add = Integer.parseInt(scanner.nextLine());
                        productDAO.updateStock(pid, add);
                        break;
                    case 4:
                        System.out.print("Product ID: "); int mId = Integer.parseInt(scanner.nextLine());
                        System.out.print("New Price (>0): "); double nPr = Double.parseDouble(scanner.nextLine());
                        Product updated = productDAO.updatePrice(mId, nPr);
                        if (updated != null) System.out.println("Updated: " + updated);
                        break;
                    case 5:
                        System.out.printf("Total Inventory Value: Rs. %,.2f%n", productDAO.getTotalInventoryValue());
                        System.out.print("Enter Product ID to delete (only if quantity=0): ");
                        int delId = Integer.parseInt(scanner.nextLine());
                        productDAO.deleteIfZeroQuantity(delId);
                        break;
                    case 6:
                        productDAO.getAllProducts().forEach(System.out::println);
                        break;
                    case 7:
                        System.out.println("Exiting application."); return;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
```

#### 5. HTTP Controller (for Postman): `src/com/oopj/controller/ProductController.java`
```java
package com.oopj.controller;

import com.oopj.dao.ProductDAO;
import com.oopj.model.Product;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductController implements HttpHandler {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        try {
            if ("GET".equalsIgnoreCase(method)) {
                if (query != null && query.contains("category=")) {
                    String cat = query.split("category=")[1].split("&")[0];
                    sendResponse(exchange, 200, toJsonArray(productDAO.getLowStockByCategory(cat)));
                } else if (query != null && query.contains("totalValue=true")) {
                    sendResponse(exchange, 200, "{\"totalInventoryValue\": " + productDAO.getTotalInventoryValue() + "}");
                } else {
                    sendResponse(exchange, 200, toJsonArray(productDAO.getAllProducts()));
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                int id = Integer.parseInt(extract(body, "productId"));
                String name = extract(body, "productName");
                String cat = extract(body, "category");
                int qty = Integer.parseInt(extract(body, "quantity"));
                double price = Double.parseDouble(extract(body, "price"));

                boolean ok = productDAO.insertProduct(new Product(id, name, cat, qty, price));
                if (ok) sendResponse(exchange, 201, "{\"status\": \"SUCCESS\", \"message\": \"Product inserted successfully!\"}");
                else sendResponse(exchange, 400, "{\"error\": \"Duplicate ID or database error\"}");
            } else if ("DELETE".equalsIgnoreCase(method) && query != null && query.contains("id=")) {
                int id = Integer.parseInt(query.split("id=")[1].split("&")[0]);
                boolean deleted = productDAO.deleteIfZeroQuantity(id);
                sendResponse(exchange, deleted ? 200 : 400, "{\"deleted\": " + deleted + "}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String toJsonArray(List<Product> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toJson());
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    private String extract(String json, String k) {
        Matcher m = Pattern.compile("\"" + k + "\"\\s*:\\s*\"?([^,\"}]+)\"?").matcher(json);
        return m.find() ? m.group(1).trim() : "";
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, b.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(b); }
    }
}
```

---

### Screenshots Required & Postman Testing Details (Q1)
1. **Model Screenshot**: IntelliJ / Eclipse showing `Product.java` (fields, getters, setters).
2. **Controller Screenshot**: IntelliJ / Eclipse showing `ProductController.java` or `InventoryMenuApp.java`.
3. **Project Structure Screenshot**: Project Explorer showing `src/com/oopj/` folders (`model`, `dao`, `controller`).
4. **Postman Screenshots**:
   - `GET http://localhost:8080/api/products` (Status `200 OK`)
   - `POST http://localhost:8080/api/products` with JSON:
     ```json
     {
       "productId": 107,
       "productName": "Wireless Ergonomic Mouse",
       "category": "Electronics",
       "quantity": 12,
       "price": 1499.00
     }
     ```
     (Status `201 Created`)
   - `GET http://localhost:8080/api/products?category=Electronics` (Returns items with quantity < 20, sorted by price DESC).

---

### Three AI Prompts used for creating the Project (Q1)
| S.No. | AI Prompt Used | Purpose |
| :---: | :--- | :--- |
| **1** | *"Write a Java JDBC DAO class for MySQL retail_db.Product table using PreparedStatement handling duplicate key insertion, category filtering with quantity < 20 sorted by price DESC, and inventory total valuation SUM(quantity * price)."* | Implementing MySQL JDBC connection and all scenario requirements (a, b, c, d, e). |
| **2** | *"Develop a menu-driven console application in Java using Scanner to interactively execute all inventory operations including stock addition, price modification if > 0, and conditional deletion when quantity is 0."* | Building the user-friendly CLI menu satisfying all sub-parts of Question 1. |
| **3** | *"Create an HTTP REST controller using Java's built-in HttpServer to expose GET, POST, and DELETE endpoints for ProductDAO so they can be tested via Postman."* | Mapping DAO operations into RESTful endpoints to generate Postman screenshots. |

---

## Q2. Spring Boot REST API JDBC MySQL (10 Marks)
**Case Study:** Employee Management REST API (Fields: `id`, `name`, `department`, `salary`)

### Moodle Upload / GitHub Repository Link
| Platform | Repository / Upload Link |
| :--- | :--- |
| **GitHub Repository Link** | `https://github.com/yash-1357/OOPs-by-java/tree/main/MSE2_Submission/Q2_SpringBoot_JDBC_MySQL` |
| **Moodle Upload** | Submitted on Moodle Portal (`Yash_2500290130224_MSE2_Q2.zip`) |

---

### Project Structure (VS Code / IntelliJ / Eclipse)
```text
Q2_SpringBoot_JDBC_MySQL/
│
├── pom.xml                                      # Maven Dependencies (Web, JDBC, MySQL)
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── oopj/
    │   │           └── springboot/
    │   │               ├── model/
    │   │               │   └── Employee.java               # (a) Model (id, name, department, salary)
    │   │               ├── repository/
    │   │               │   └── EmployeeJdbcRepository.java # JdbcTemplate Data Access
    │   │               ├── controller/
    │   │               │   └── EmployeeController.java     # (b-e) REST Endpoints
    │   │               └── SpringBootJdbcApplication.java  # Main Application
    │   └── resources/
    │       ├── application.properties           # MySQL Connection Config (Port 8081)
    │       └── schema.sql                       # Database Table definition
    └── test/
```

---

### Project Code (Q2)

#### 1. Maven Dependency Configuration: `pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.4</version>
        <relativePath/>
    </parent>
    <groupId>com.oopj</groupId>
    <artifactId>spring-boot-jdbc-mysql</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-boot-jdbc-mysql</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

#### 2. Configuration: `src/main/resources/application.properties`
```properties
server.port=8081

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

logging.level.org.springframework.jdbc.core=DEBUG
```

#### 3. (a) Model Class: `src/main/java/com/oopj/springboot/model/Employee.java` (2 Marks)
```java
package com.oopj.springboot.model;

/**
 * Model class representing the Employee entity as per Scenario.
 * Attributes: id, name, department, salary.
 */
public class Employee {
    private Integer id;
    private String name;
    private String department;
    private Double salary;

    public Employee() {}

    public Employee(Integer id, String name, String department, Double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public Employee(String name, String department, Double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', department='" + department + "', salary=" + salary + "}";
    }
}
```

#### 4. Repository Layer: `src/main/java/com/oopj/springboot/repository/EmployeeJdbcRepository.java`
```java
package com.oopj.springboot.repository;

import com.oopj.springboot.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> new Employee(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getDouble("salary")
    );

    // Retrieve all employees
    public List<Employee> findAll() {
        String sql = "SELECT id, name, department, salary FROM employee ORDER BY id ASC";
        return jdbcTemplate.query(sql, employeeRowMapper);
    }

    // Retrieve employee by ID
    public Optional<Employee> findById(int id) {
        String sql = "SELECT id, name, department, salary FROM employee WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, employeeRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Add a new employee
    public int save(Employee employee) {
        String sql = "INSERT INTO employee (name, department, salary) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, employee.getName(), employee.getDepartment(), employee.getSalary());
    }

    // Update an employee
    public int update(int id, Employee employee) {
        String sql = "UPDATE employee SET name = ?, department = ?, salary = ? WHERE id = ?";
        return jdbcTemplate.update(sql, employee.getName(), employee.getDepartment(), employee.getSalary(), id);
    }

    // Delete an employee by ID
    public int deleteById(int id) {
        String sql = "DELETE FROM employee WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
```

#### 5. REST Controller: `src/main/java/com/oopj/springboot/controller/EmployeeController.java` (8 Marks)
*(Covers parts b, c, d, e)*
```java
package com.oopj.springboot.controller;

import com.oopj.springboot.model.Employee;
import com.oopj.springboot.repository.EmployeeJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeJdbcRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeJdbcRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // (c) GET /api/v1/employees - Retrieve all employees (2 Marks)
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    // (c) GET /api/v1/employees/{id} - Retrieve a specific employee by ID (2 Marks)
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") int id) {
        Optional<Employee> emp = employeeRepository.findById(id);
        if (emp.isPresent()) {
            return ResponseEntity.ok(emp.get());
        }
        Map<String, String> err = new HashMap<>();
        err.put("error", "Employee not found with ID: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    // (b) POST /api/v1/employees - Add a new employee using @PostMapping and @RequestBody (2 Marks)
    @PostMapping
    public ResponseEntity<Map<String, Object>> addEmployee(@RequestBody Employee employee) {
        int rows = employeeRepository.save(employee);
        Map<String, Object> resp = new HashMap<>();
        if (rows > 0) {
            resp.put("status", "SUCCESS");
            resp.put("message", "Employee record created successfully in MySQL database!");
            resp.put("data", employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        }
        resp.put("status", "FAILED");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    // (d) PUT /api/v1/employees/{id} - Update an employee's details using @PutMapping (2 Marks)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@PathVariable("id") int id, @RequestBody Employee employee) {
        int rows = employeeRepository.update(id, employee);
        Map<String, Object> resp = new HashMap<>();
        if (rows > 0) {
            employee.setId(id);
            resp.put("status", "SUCCESS");
            resp.put("message", "Employee updated successfully!");
            resp.put("data", employee);
            return ResponseEntity.ok(resp);
        }
        resp.put("error", "Employee with ID " + id + " not found to update.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    // (e) DELETE /api/v1/employees/{id} - Delete an employee by ID using @DeleteMapping (2 Marks)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable("id") int id) {
        int rows = employeeRepository.deleteById(id);
        Map<String, String> resp = new HashMap<>();
        if (rows > 0) {
            resp.put("status", "SUCCESS");
            resp.put("message", "Employee with ID " + id + " deleted successfully!");
            return ResponseEntity.ok(resp);
        }
        resp.put("status", "FAILED");
        resp.put("error", "Employee with ID " + id + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }
}
```

#### 6. Application Runner: `src/main/java/com/oopj/springboot/SpringBootJdbcApplication.java`
```java
package com.oopj.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootJdbcApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootJdbcApplication.class, args);
        System.out.println("Spring Boot Employee REST API running on http://localhost:8081/api/v1/employees");
    }
}
```

---

### Screenshots Required & Postman Testing Details (Q2)
1. **Model Screenshot**: Capture `Employee.java` showing the 4 attributes (`id`, `name`, `department`, `salary`), constructors, and getters/setters.
2. **Controller Screenshot**: Capture `EmployeeController.java` showing `@RestController`, `@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping`.
3. **Project Structure Screenshot**: Project Explorer showing `com.oopj.springboot` package hierarchy and `pom.xml`.
4. **Postman Screenshots**:
   - **GET All:** `GET http://localhost:8081/api/v1/employees` (Status `200 OK`)
   - **GET By ID:** `GET http://localhost:8081/api/v1/employees/1` (Status `200 OK`)
   - **POST Add Employee:** `POST http://localhost:8081/api/v1/employees`
     ```json
     {
       "name": "Yash Raj Singh",
       "department": "Information Technology",
       "salary": 92000.00
     }
     ```
     (Status `201 Created`)
   - **PUT Update Employee:** `PUT http://localhost:8081/api/v1/employees/1`
     ```json
     {
       "name": "Rohit Sharma",
       "department": "Senior Architect",
       "salary": 110000.00
     }
     ```
     (Status `200 OK`)
   - **DELETE Employee:** `DELETE http://localhost:8081/api/v1/employees/3` (Status `200 OK`)

---

### Three AI Prompts used for creating the Project (Q2)
| S.No. | AI Prompt Used | Purpose |
| :---: | :--- | :--- |
| **1** | *"Configure a Spring Boot 3 Maven application with spring-boot-starter-web, spring-boot-starter-jdbc, and MySQL connector for an Employee Management REST API."* | Generating project dependencies and setting up MySQL DataSource in `application.properties`. |
| **2** | *"Write a Spring Boot `@Repository` using `JdbcTemplate` to perform CRUD operations on an employee table (id, name, department, salary) using `RowMapper`."* | Establishing data persistence layer using Spring's JDBC template. |
| **3** | *"Implement a Spring Boot `@RestController` with @PostMapping, @GetMapping, @PutMapping, and @DeleteMapping endpoints returning ResponseEntity with status codes (200, 201, 404).'* | Implementing RESTful controller with proper HTTP response status codes. |
