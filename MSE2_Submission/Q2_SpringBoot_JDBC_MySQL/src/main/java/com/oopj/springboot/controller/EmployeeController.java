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

/**
 * REST Controller fulfilling all requirements of MSE-2 Question 2:
 * (b) @PostMapping with @RequestBody
 * (c) @GetMapping for all and by ID
 * (d) @PutMapping for update
 * (e) @DeleteMapping for deletion with HTTP response handling
 */
@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeJdbcRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeJdbcRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // (c) GET: Retrieve all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }

    // (c) GET: Retrieve employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") int id) {
        Optional<Employee> emp = employeeRepository.findById(id);
        if (emp.isPresent()) {
            return ResponseEntity.ok(emp.get());
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Employee not found with ID: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // (b) POST: Add a new employee using @PostMapping and @RequestBody
    @PostMapping
    public ResponseEntity<Map<String, Object>> addEmployee(@RequestBody Employee employee) {
        int rows = employeeRepository.save(employee);
        Map<String, Object> response = new HashMap<>();
        if (rows > 0) {
            response.put("status", "SUCCESS");
            response.put("message", "Employee record created successfully in MySQL database!");
            response.put("data", employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        response.put("status", "FAILED");
        response.put("message", "Failed to insert employee record.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // (d) PUT: Update an employee's details using @PutMapping
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@PathVariable("id") int id, @RequestBody Employee employee) {
        int rows = employeeRepository.update(id, employee);
        Map<String, Object> response = new HashMap<>();
        if (rows > 0) {
            employee.setId(id);
            response.put("status", "SUCCESS");
            response.put("message", "Employee updated successfully!");
            response.put("data", employee);
            return ResponseEntity.ok(response);
        }
        response.put("status", "FAILED");
        response.put("error", "Employee with ID " + id + " not found to update.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // (e) DELETE: Delete an employee by ID using @DeleteMapping
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable("id") int id) {
        int rows = employeeRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        if (rows > 0) {
            response.put("status", "SUCCESS");
            response.put("message", "Employee with ID " + id + " deleted successfully!");
            return ResponseEntity.ok(response);
        }
        response.put("status", "FAILED");
        response.put("error", "Employee with ID " + id + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
