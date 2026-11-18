package com.oopj;

import com.oopj.controller.ProductController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MainApplication {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/products", new ProductController());
        server.setExecutor(null);
        System.out.println("==================================================================");
        System.out.println("  Q1 Retail Inventory Management HTTP Server (JDBC MySQL) Started");
        System.out.println("  Postman Endpoint: http://localhost:8080/api/products");
        System.out.println("==================================================================");
        server.start();
    }
}
