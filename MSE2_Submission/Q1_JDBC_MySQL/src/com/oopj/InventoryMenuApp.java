package com.oopj;

import com.oopj.dao.ProductDAO;
import com.oopj.model.Product;

import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven console application satisfying all Q1 scenario requirements (a) to (e).
 */
public class InventoryMenuApp {
    private static final ProductDAO productDAO = new ProductDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("     RETAIL STORE - INVENTORY MANAGEMENT SYSTEM (JDBC)        ");
        System.out.println("===============================================================");

        while (true) {
            System.out.println("\n----------------------- MAIN MENU ----------------------------");
            System.out.println("1. (a) Insert Product Records (PreparedStatement & Duplicate check)");
            System.out.println("2. (b) Retrieve Products by Category (Quantity < 20, Price DESC)");
            System.out.println("3. (c) Update Product Stock (Add quantity)");
            System.out.println("4. (d) Search & Modify Product Price (Validate Price > 0)");
            System.out.println("5. (e) Display Total Inventory Value & Delete Zero-Stock Item");
            System.out.println("6. View All Products");
            System.out.println("7. Exit");
            System.out.print("Enter your choice (1-7): ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        handleInsertProducts();
                        break;
                    case 2:
                        handleConditionalRetrieval();
                        break;
                    case 3:
                        handleUpdateStock();
                        break;
                    case 4:
                        handleModifyPrice();
                        break;
                    case 5:
                        handleAggregateAndDelete();
                        break;
                    case 6:
                        handleViewAll();
                        break;
                    case 7:
                        System.out.println("Exiting Inventory Management System. Thank you!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please select between 1 and 7.");
                }
            } catch (Exception e) {
                System.err.println("Error processing operation: " + e.getMessage());
            }
        }
    }

    // (a) Insert at least 3 records using PreparedStatement
    private static void handleInsertProducts() {
        System.out.println("\n--- [Operation a] Insert Product Record ---");
        System.out.print("Enter Product ID (Integer): ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Category (e.g., Electronics, Stationery): ");
        String category = scanner.nextLine().trim();

        System.out.print("Enter Quantity in Stock: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Price (e.g., 299.50): ");
        double price = Double.parseDouble(scanner.nextLine().trim());

        Product product = new Product(id, name, category, quantity, price);
        boolean success = productDAO.insertProduct(product);
        if (success) {
            System.out.println("✅ Product inserted successfully: " + product);
        } else {
            System.out.println("❌ Insertion failed (Check if Product ID already exists or DB connection error).");
        }
    }

    // (b) Conditional Retrieval and Sorting (quantity < 20, sorted by price DESC)
    private static void handleConditionalRetrieval() throws Exception {
        System.out.println("\n--- [Operation b] Retrieve Products (Quantity < 20, Price DESC) ---");
        System.out.print("Enter Category to filter (e.g., Electronics): ");
        String category = scanner.nextLine().trim();

        List<Product> products = productDAO.getLowStockByCategory(category);
        if (products.isEmpty()) {
            System.out.println("No products found for category '" + category + "' with quantity < 20.");
        } else {
            System.out.printf("%-10s | %-30s | %-15s | %-10s | %-12s%n", "ID", "Product Name", "Category", "Quantity", "Price (Rs)");
            System.out.println("----------------------------------------------------------------------------------------");
            for (Product p : products) {
                System.out.printf("%-10d | %-30s | %-15s | %-10d | %-12.2f%n",
                        p.getProductId(), p.getProductName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        }
    }

    // (c) Update Product Stock
    private static void handleUpdateStock() throws Exception {
        System.out.println("\n--- [Operation c] Update Product Stock ---");
        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Quantity to add to existing stock: ");
        int addQty = Integer.parseInt(scanner.nextLine().trim());

        boolean updated = productDAO.updateStock(id, addQty);
        if (updated) {
            Product p = productDAO.getProductById(id);
            System.out.println("✅ Stock updated successfully! New details: " + p);
        }
    }

    // (d) Search and Modify Product Details (price > 0 check)
    private static void handleModifyPrice() throws Exception {
        System.out.println("\n--- [Operation d] Modify Product Price ---");
        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter New Price (must be > 0): ");
        double newPrice = Double.parseDouble(scanner.nextLine().trim());

        Product updated = productDAO.updatePrice(id, newPrice);
        if (updated != null) {
            System.out.println("✅ Price updated successfully! Updated Product: " + updated);
        }
    }

    // (e) Aggregate Query (SUM(quantity * price)) & Delete Zero Quantity
    private static void handleAggregateAndDelete() throws Exception {
        System.out.println("\n--- [Operation e] Total Inventory Value & Conditional Deletion ---");
        double totalValue = productDAO.getTotalInventoryValue();
        System.out.printf("💰 Total Inventory Value (SUM(quantity * price)): Rs. %,.2f%n", totalValue);

        System.out.println("\nConditional Deletion (Only items with Quantity = 0 can be deleted):");
        System.out.print("Enter Product ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        boolean deleted = productDAO.deleteIfZeroQuantity(id);
        if (deleted) {
            System.out.println("✅ Product ID " + id + " was out of stock (quantity=0) and has been deleted successfully!");
        }
    }

    // View all products
    private static void handleViewAll() throws Exception {
        System.out.println("\n--- Current All Products in retail_db.Product ---");
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found in database.");
        } else {
            System.out.printf("%-10s | %-30s | %-15s | %-10s | %-12s%n", "ID", "Product Name", "Category", "Quantity", "Price (Rs)");
            System.out.println("----------------------------------------------------------------------------------------");
            for (Product p : products) {
                System.out.printf("%-10d | %-30s | %-15s | %-10d | %-12.2f%n",
                        p.getProductId(), p.getProductName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        }
    }
}
