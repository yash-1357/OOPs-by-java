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

/**
 * Controller class mapping HTTP requests from Postman to ProductDAO methods.
 */
public class ProductController implements HttpHandler {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange, query);
            } else if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                handleDelete(exchange, query);
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws Exception {
        if (query != null && query.contains("totalValue=true")) {
            double total = productDAO.getTotalInventoryValue();
            sendResponse(exchange, 200, String.format("{\"totalInventoryValue\": %.2f}", total));
            return;
        }

        if (query != null && query.contains("category=")) {
            String category = query.split("category=")[1].split("&")[0];
            List<Product> list = productDAO.getLowStockByCategory(category);
            sendResponse(exchange, 200, toJsonArray(list));
            return;
        }

        if (query != null && query.contains("id=")) {
            int id = Integer.parseInt(query.split("id=")[1].split("&")[0]);
            Product p = productDAO.getProductById(id);
            if (p != null) {
                sendResponse(exchange, 200, p.toJson());
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Product not found\"}");
            }
            return;
        }

        // Return all products
        List<Product> all = productDAO.getAllProducts();
        sendResponse(exchange, 200, toJsonArray(all));
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        int id = Integer.parseInt(extractField(body, "productId"));
        String name = extractField(body, "productName");
        String category = extractField(body, "category");
        int quantity = Integer.parseInt(extractField(body, "quantity"));
        double price = Double.parseDouble(extractField(body, "price"));

        Product product = new Product(id, name, category, quantity, price);
        boolean created = productDAO.insertProduct(product);

        if (created) {
            sendResponse(exchange, 201, "{\"status\": \"SUCCESS\", \"message\": \"Product inserted successfully!\", \"product\": " + product.toJson() + "}");
        } else {
            sendResponse(exchange, 400, "{\"status\": \"FAILED\", \"message\": \"Duplicate product_id or invalid data!\"}");
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws Exception {
        if (query != null && query.contains("id=")) {
            int id = Integer.parseInt(query.split("id=")[1].split("&")[0]);
            boolean deleted = productDAO.deleteIfZeroQuantity(id);
            if (deleted) {
                sendResponse(exchange, 200, "{\"message\": \"Product ID " + id + " deleted successfully (quantity was 0).\"}");
            } else {
                sendResponse(exchange, 400, "{\"error\": \"Cannot delete product ID " + id + ": Not found or quantity > 0.\"}");
            }
        } else {
            sendResponse(exchange, 400, "{\"error\": \"Missing id parameter.\"}");
        }
    }

    private String toJsonArray(List<Product> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toJson());
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String extractField(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1).trim() : "";
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
