package com.kiet.Student.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiet.Student.Model.Student;

@RestController
@RequestMapping("/students")
public class StudentController {

    List<Student> users = new ArrayList<>();

    @PostMapping
    public String postMethodName(@RequestBody Student u) {
        users.add(u);
        return "Added " + u.getName();
    }

    @GetMapping
    public List<Student> getAllUsers() {
        return users;
    }

    @GetMapping("/{id}")
    public Student getUser(@PathVariable Long id) {
        for (Student u : users) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        for (Student user : users) {
            if (user.getId().equals(id)) {
                users.remove(user);
                return "Deleted user " + id;
            }
        }
        return "User not found";
    }
}
