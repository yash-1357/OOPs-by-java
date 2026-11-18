package com.oopj.model;

/**
 * Model class representing the Product entity.
 * Corresponding table: retail_db.Product
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

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Product [ID=%d, Name='%s', Category='%s', Quantity=%d, Price=Rs. %.2f]",
                productId, productName, category, quantity, price);
    }

    public String toJson() {
        return String.format(
            "{\"productId\": %d, \"productName\": \"%s\", \"category\": \"%s\", \"quantity\": %d, \"price\": %.2f}",
            productId, escapeJson(productName), escapeJson(category), quantity, price
        );
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
