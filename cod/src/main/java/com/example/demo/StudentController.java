package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {

    private List<Student> students = new ArrayList<>();

    public StudentController() {
        // Pre‑load some data
        students.add(new Student(1, "Anand"));
        students.add(new Student(2, "Ravi"));
    }

    // GET all
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return students;
    }

    // GET by ID – throws StudentNotFoundException if not found
    @GetMapping("/students/{id}")
    public Student getStudentById(@PathVariable int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new StudentNotFoundException("Student not found with id: " + id);
    }

    // POST – add new student (handles duplicate ID)
    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        // Check for duplicate ID
        for (Student s : students) {
            if (s.getId() == student.getId()) {
                throw new RuntimeException("Student with id " + student.getId() + " already exists");
            }
        }
        students.add(student);
        return student;   // 200 OK by default (or 201 Created)
    }
}