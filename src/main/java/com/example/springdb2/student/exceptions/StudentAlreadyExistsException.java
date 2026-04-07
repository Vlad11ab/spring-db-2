package com.example.springdb2.student.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StudentAlreadyExistsException extends RuntimeException {
    public StudentAlreadyExistsException() {
        super("STUDENT_ALREADY_EXISTS_EXCEPTION");
    }
}
