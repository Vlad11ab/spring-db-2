package com.example.springdb2.student.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyStudentUpdateRequestException extends RuntimeException {
    public EmptyStudentUpdateRequestException() {
        super("EMPTY_STUDENT_UPDATE_REQUEST_EXCEPTION");
    }
}
