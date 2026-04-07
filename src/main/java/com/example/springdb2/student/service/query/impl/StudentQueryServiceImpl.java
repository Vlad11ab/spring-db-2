package com.example.springdb2.student.service.query.impl;

import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.mappers.StudentMapper;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import com.example.springdb2.student.service.query.StudentQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class StudentQueryServiceImpl implements StudentQueryService {

    private StudentRepository studentRepository;
    private StudentMapper studentMapper;

    public StudentQueryServiceImpl(StudentRepository studentRepository){
     this.studentRepository = studentRepository;

//     this.getAllStudents();
//        this.getStudentById(102L);
//        this.findByEmail("vlad@gmail.com");
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        return studentMapper.toDto(student);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        System.out.println("gasit");
        return studentRepository.findByEmail(email);
    }


}
