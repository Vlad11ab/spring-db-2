package com.example.springdb2.student.service.query;

import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface StudentQueryService {
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long studentId);
    Optional<Student> findByEmail(String email);

}
