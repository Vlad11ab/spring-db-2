package com.example.springdb2.student.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long studentId) {
        super("STUDENT_NOT_FOUND_EXCEPTION");
    }
}
