package com.oopj.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJdbcApplication.class, args);
        System.out.println("==========================================================");
        System.out.println(" Spring Boot JDBC MySQL Application Started Successfully!");
        System.out.println(" Base API URL: http://localhost:8081/api/v1/employees");
        System.out.println("==========================================================");
    }
}
