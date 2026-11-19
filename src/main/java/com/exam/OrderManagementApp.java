package com.exam;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;

public class OrderManagementApp {
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    private static final String USER = "root";
    private static final String PASSWORD = "124421"; // Apna MySQL password yahan likhein

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to ecommerce_db successfully!");

            while (true) {
                System.out.println("\n===== ORDER MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert Order Record (a)");
                System.out.println("2. Search Orders by Status & Qty > 2 (b)");
                System.out.println("3. Increase Order Quantity (c)");
                System.out.println("4. Update Order Status (d)");
                System.out.println("5. Show Total Order Value & Delete Cancelled Order (e)");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        insertOrder(conn, sc);
                        break;
                    case 2:
                        displayByStatusAndQty(conn, sc);
                        break;
                    case 3:
                        increaseQuantity(conn, sc);
                        break;
                    case 4:
                        updateOrderStatus(conn, sc);
                        break;
                    case 5:
                        totalValueAndDeleteCancelled(conn, sc);
                        break;
                    case 6:
                        System.out.println("Exiting application...");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // (a) Insert order records using PreparedStatement & handle duplicate order_id
    private static void insertOrder(Connection conn, Scanner sc) {
        System.out.print("How many orders do you want to insert? (Min 3 recommended): ");
        int count = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO Orders (order_id, customer_name, product_name, quantity, unit_price, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                System.out.println("\nEnter details for Order " + i + ":");
                System.out.print("Order ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Customer Name: ");
                String customer = sc.nextLine();

                System.out.print("Product Name: ");
                String product = sc.nextLine();

                System.out.print("Quantity: ");
                int qty = sc.nextInt();

                System.out.print("Unit Price: ");
                double price = sc.nextDouble();
                sc.nextLine();

                System.out.print("Status (Pending/Shipped/Delivered/Cancelled): ");
                String status = sc.nextLine();

                pstmt.setInt(1, id);
                pstmt.setString(2, customer);
                pstmt.setString(3, product);
                pstmt.setInt(4, qty);
                pstmt.setDouble(5, price);
                pstmt.setString(6, status);

                try {
                    pstmt.executeUpdate();
                    System.out.println("Order ID " + id + " inserted successfully.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Error: Order ID " + id + " already exists! Duplicate Order ID not allowed.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    // (b) Display orders by status and quantity > 2, sorted by unit_price DESC
    private static void displayByStatusAndQty(Connection conn, Scanner sc) {
        System.out.print("Enter order status to search: ");
        String status = sc.nextLine();

        String sql = "SELECT * FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            System.out.println("\n--- Matching Orders ---");
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | Customer: %s | Product: %s | Qty: %d | Unit Price: %.2f | Status: %s%n",
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("status"));
            }
            if (!found) {
                System.out.println("No orders found with status '" + status + "' and quantity > 2.");
            }
        } catch (SQLException e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }

    // (c) Increase existing quantity of an order
    private static void increaseQuantity(Connection conn, Scanner sc) {
        System.out.print("Enter Order ID: ");
        int id = sc.nextInt();
        System.out.print("Enter additional quantity to add: ");
        int addQty = sc.nextInt();

        String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addQty);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Quantity updated successfully for Order ID " + id + ".");
            } else {
                System.out.println("Order ID " + id + " does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Update quantity error: " + e.getMessage());
        }
    }

    // (d) Update status only if order exists and status is valid, then display details
    private static void updateOrderStatus(Connection conn, Scanner sc) {
        System.out.print("Enter Order ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new status (Pending / Shipped / Delivered): ");
        String newStatus = sc.nextLine();

        if (!newStatus.equalsIgnoreCase("Pending") &&
            !newStatus.equalsIgnoreCase("Shipped") &&
            !newStatus.equalsIgnoreCase("Delivered")) {
            System.out.println("Invalid status! Allowed values: Pending, Shipped, Delivered.");
            return;
        }

        String checkSql = "SELECT * FROM Orders WHERE order_id = ?";
        String updateSql = "UPDATE Orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Order ID " + id + " does not exist.");
                return;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, newStatus);
                updateStmt.setInt(2, id);
                updateStmt.executeUpdate();
                System.out.println("Status updated successfully!");
            }

            // Display updated order details
            ResultSet updatedRs = checkStmt.executeQuery();
            if (updatedRs.next()) {
                System.out.println("--- Updated Order Details ---");
                System.out.printf("ID: %d | Customer: %s | Product: %s | Qty: %d | Unit Price: %.2f | Status: %s%n",
                        updatedRs.getInt("order_id"),
                        updatedRs.getString("customer_name"),
                        updatedRs.getString("product_name"),
                        updatedRs.getInt("quantity"),
                        updatedRs.getDouble("unit_price"),
                        updatedRs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Update status error: " + e.getMessage());
        }
    }

    // (e) Display total order value and delete order only if status is Cancelled
    private static void totalValueAndDeleteCancelled(Connection conn, Scanner sc) {
        String sumSql = "SELECT SUM(quantity * unit_price) AS total_value FROM Orders";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sumSql)) {
            if (rs.next()) {
                System.out.printf("Total Order Value (all orders): %.2f%n", rs.getDouble("total_value"));
            }
        } catch (SQLException e) {
            System.out.println("Error calculating total value: " + e.getMessage());
        }

        System.out.print("\nEnter Order ID to delete (only if Cancelled): ");
        int id = sc.nextInt();

        String checkSql = "SELECT status FROM Orders WHERE order_id = ?";
        String deleteSql = "DELETE FROM Orders WHERE order_id = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Order ID " + id + " does not exist.");
                return;
            }

            String currentStatus = rs.getString("status");
            if (!"Cancelled".equalsIgnoreCase(currentStatus)) {
                System.out.println("Cannot delete Order ID " + id + " because its status is '" + currentStatus + "' (must be 'Cancelled').");
                return;
            }

            try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                delStmt.setInt(1, id);
                delStmt.executeUpdate();
                System.out.println("Cancelled Order ID " + id + " deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }
}