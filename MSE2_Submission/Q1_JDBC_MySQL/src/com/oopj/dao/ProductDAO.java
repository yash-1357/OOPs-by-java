package com.oopj.dao;

import com.oopj.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for performing JDBC operations on retail_db.Product
 */
public class ProductDAO {

    // Retrieve all products
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

    // Retrieve single product by ID
    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT * FROM Product WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // (a) Insert Product Records using PreparedStatement (handling duplicate product IDs)
    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getProductId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getCategory());
            ps.setInt(4, product.getQuantity());
            ps.setDouble(5, product.getPrice());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("❌ Error: Duplicate Product ID " + product.getProductId() + " already exists!");
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Database Error while inserting product: " + e.getMessage());
            return false;
        }
    }

    // (b) Conditional Retrieval and Sorting:
    // Accept category, display products whose quantity < 20, sorted by price in descending order.
    public List<Product> getLowStockByCategory(String category) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE LOWER(category) = LOWER(?) AND quantity < 20 ORDER BY price DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // (c) Update Product Stock:
    // Accept product ID and quantity to add to existing stock.
    public boolean updateStock(int productId, int additionalQuantity) throws SQLException {
        Product existing = getProductById(productId);
        if (existing == null) {
            System.out.println("❌ Product with ID " + productId + " does not exist!");
            return false;
        }

        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, additionalQuantity);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // (d) Search and Modify Product Details:
    // Accept product ID and new price. Update price only if product exists and new price > 0.
    public Product updatePrice(int productId, double newPrice) throws SQLException {
        if (newPrice <= 0) {
            System.out.println("❌ Invalid Price: New price must be greater than zero (Rs. " + newPrice + ")!");
            return null;
        }

        Product existing = getProductById(productId);
        if (existing == null) {
            System.out.println("❌ Product with ID " + productId + " does not exist!");
            return null;
        }

        String sql = "UPDATE Product SET price = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return getProductById(productId); // Return updated product details
            }
        }
        return null;
    }

    // (e) Aggregate Query: Display total inventory value using SUM(quantity * price)
    public double getTotalInventoryValue() throws SQLException {
        String sql = "SELECT SUM(quantity * price) AS total_value FROM Product";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
        }
        return 0.0;
    }

    // (e) Conditional Deletion:
    // Delete record only if its quantity is zero. Display appropriate message if cannot be deleted.
    public boolean deleteIfZeroQuantity(int productId) throws SQLException {
        Product existing = getProductById(productId);
        if (existing == null) {
            System.out.println("❌ Product with ID " + productId + " not found!");
            return false;
        }

        if (existing.getQuantity() > 0) {
            System.out.println("❌ Cannot Delete: Product ID " + productId + " (" + existing.getProductName() +
                    ") still has " + existing.getQuantity() + " units in stock! Quantity must be 0 to delete.");
            return false;
        }

        String sql = "DELETE FROM Product WHERE product_id = ? AND quantity = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        }
    }

    // Helper method to map ResultSet to Product
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
