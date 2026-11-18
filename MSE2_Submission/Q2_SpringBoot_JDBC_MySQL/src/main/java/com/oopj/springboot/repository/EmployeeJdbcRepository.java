package com.oopj.springboot.repository;

import com.oopj.springboot.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository layer using Spring's JdbcTemplate for MySQL operations on the Employee table.
 */
@Repository
public class EmployeeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to map SQL ResultSet row to Employee object
    private final RowMapper<Employee> employeeRowMapper = new RowMapper<Employee>() {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getDouble("salary")
            );
        }
    };

    // (c) Retrieve all employees
    public List<Employee> findAll() {
        String sql = "SELECT id, name, department, salary FROM employee ORDER BY id ASC";
        return jdbcTemplate.query(sql, employeeRowMapper);
    }

    // (c) Retrieve employee by ID
    public Optional<Employee> findById(int id) {
        String sql = "SELECT id, name, department, salary FROM employee WHERE id = ?";
        try {
            Employee employee = jdbcTemplate.queryForObject(sql, employeeRowMapper, id);
            return Optional.ofNullable(employee);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // (b) Add a new employee
    public int save(Employee employee) {
        String sql = "INSERT INTO employee (name, department, salary) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, employee.getName(), employee.getDepartment(), employee.getSalary());
    }

    // (d) Update an employee's details
    public int update(int id, Employee employee) {
        String sql = "UPDATE employee SET name = ?, department = ?, salary = ? WHERE id = ?";
        return jdbcTemplate.update(sql, employee.getName(), employee.getDepartment(), employee.getSalary(), id);
    }

    // (e) Delete an employee by ID
    public int deleteById(int id) {
        String sql = "DELETE FROM employee WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
